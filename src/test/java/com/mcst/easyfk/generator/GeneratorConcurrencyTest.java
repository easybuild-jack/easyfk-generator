package com.mcst.easyfk.generator;

import com.mcst.easyfk.generator.enums.BuildType;
import com.mcst.easyfk.generator.enums.OrmType;
import com.mcst.easyfk.generator.enums.PrdType;
import com.mcst.easyfk.generator.enums.ProjectType;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import com.mcst.easyfk.generator.vo.FieldInfo;
import com.mcst.easyfk.generator.vo.ModelInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 并发生成压力测试：多个生成任务同时写同一个 / 不同的输出目录时，不能丢文件、不能留下空文件或半截文件，
 * 写失败必须抛出异常而不是只打日志。
 */
@Tag("concurrency")
@Timeout(value = 180, unit = TimeUnit.SECONDS)
class GeneratorConcurrencyTest {

    private static final int MODEL_COUNT = 10;
    /** 字段足够多，使实体/DTO 超过 BufferedWriter 的 8KB 缓冲，写入会分多次落盘 */
    private static final int FIELD_COUNT = 120;
    private static final Pattern DATE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    @Test
    void concurrentRunsIntoSameDirectoryNeverExposeMissingOrPartialFiles(@TempDir Path tempDir) throws Exception {
        Path dir = tempDir.resolve("shared");
        new EasyfkGenerator(project(dir), code()).generateAll();
        Map<String, String> reference = snapshot(dir);
        new EasyfkGenerator(project(dir), code()).generateAll();
        assertSameTree(reference, snapshot(dir), "顺序重复生成应当幂等");

        List<Consumer<EasyfkGenerator>> operations = List.of(
                EasyfkGenerator::generateModel,
                EasyfkGenerator::generateCode,
                EasyfkGenerator::updateDtoAndParam,
                EasyfkGenerator::generateConfig,
                EasyfkGenerator::generateAll);

        AtomicBoolean done = new AtomicBoolean();
        AtomicInteger violationCount = new AtomicInteger();
        List<String> violations = Collections.synchronizedList(new ArrayList<>());
        Thread observer = new Thread(() -> {
            while (!done.get()) {
                for (Map.Entry<String, String> entry : reference.entrySet()) {
                    String problem = checkFile(dir.resolve(entry.getKey()), entry.getValue());
                    if (problem != null && violationCount.incrementAndGet() <= 15) {
                        violations.add(entry.getKey() + " -> " + problem);
                    }
                }
            }
        }, "generated-file-observer");

        int threads = 6;
        int iterations = 10;
        List<Throwable> errors = runConcurrently(threads, iterations, (thread, iteration) -> {
            Consumer<EasyfkGenerator> operation = operations.get((thread + iteration) % operations.size());
            operation.accept(new EasyfkGenerator(project(dir), code()));
        }, observer, done);

        assertNoErrors(errors);
        assertEquals(0, violationCount.get(),
                "并发生成期间观察到缺失/空/不完整的文件（共 " + violationCount.get() + " 次），示例：\n"
                        + String.join("\n", violations));
        assertSameTree(reference, snapshot(dir), "并发生成结束后目录内容应与顺序生成一致");
    }

    @Test
    void parallelRunsIntoSeparateDirectoriesWithSharedCodePropertiesMatchSequentialRun(@TempDir Path tempDir)
            throws Exception {
        Path referenceDir = tempDir.resolve("reference");
        new EasyfkGenerator(project(referenceDir), code()).generateAll();
        Map<String, String> reference = snapshot(referenceDir);

        // 与 Spring 单例 Bean / 服务端复用配置的场景一致：所有任务共用同一个 CodeProperties
        CodeProperties sharedCode = code();
        int threads = 8;
        int iterations = 2;
        List<Throwable> errors = runConcurrently(threads, iterations, (thread, iteration) ->
                new EasyfkGenerator(project(tempDir.resolve("run-" + thread + "-" + iteration)), sharedCode)
                        .generateAll(), null, null);

        assertNoErrors(errors);
        for (int thread = 0; thread < threads; thread++) {
            for (int iteration = 0; iteration < iterations; iteration++) {
                String run = "run-" + thread + "-" + iteration;
                assertSameTree(reference, snapshot(tempDir.resolve(run)), run);
            }
        }
    }

    @Test
    void repeatedRunsDoNotLeakStateIntoSharedCodeProperties(@TempDir Path tempDir) throws Exception {
        CodeProperties sharedCode = code();
        ModelInfo configured = sharedCode.getModelList().get(0);
        List<FieldInfo> configuredFields = configured.getFieldList();

        new EasyfkGenerator(project(tempDir.resolve("first")), sharedCode).generateAll();
        new EasyfkGenerator(project(tempDir.resolve("second")), sharedCode).generateAll();

        String ormDir = "second/stress-app/stress-app-orm/stress-app-orm-";
        String modelPath = "/src/main/java/com/example/stress/orm/%s/persistence/model/Model0.java";
        assertTrue(Files.readString(tempDir.resolve(ormDir + "plus" + modelPath.formatted("plus")))
                .contains("extends BaseMyBatisPlusEntity<Model0>"));
        assertTrue(Files.readString(tempDir.resolve(ormDir + "flex" + modelPath.formatted("flex")))
                .contains("extends BaseFlexEntity<Model0>"));
        assertTrue(Files.readString(tempDir.resolve(ormDir + "hibernate" + modelPath.formatted("hibernate")))
                .contains("extends BaseHibernateEntity"));
        assertEquals(null, configured.getSuperClass(), "生成过程不应改写调用方传入的 ModelInfo");
        assertTrue(configuredFields == configured.getFieldList(), "生成过程不应替换调用方传入的字段列表");
    }

    @Test
    void templateFailureFailsLoudlyAndDoesNotLeaveCorruptedFiles(@TempDir Path tempDir) throws Exception {
        ProjectProperties project = singleProject(tempDir);
        CodeProperties code = code();
        code.setDbPwd(null);
        Path devConfig = tempDir.resolve("single-app/config/application-dev.yml");

        assertThrows(RuntimeException.class, () -> new EasyfkGenerator(project, code).generateCode(),
                "模板渲染失败必须向调用方抛出异常");
        assertFalse(Files.exists(devConfig), "渲染失败时不应留下半截文件，否则后续 createOnNotExistFile 会一直跳过它");

        code.setDbPwd("secret");
        new EasyfkGenerator(project, code).generateCode();
        String content = Files.readString(devConfig);
        assertTrue(content.contains("password: secret"));
        assertFalse(content.contains("FreeMarker template error"));
        assertTrue(Files.exists(tempDir.resolve("single-app/config/application-prod.yml")));
        assertTrue(Files.exists(tempDir.resolve("single-app/config/logback.xml")));
    }

    @Test
    void codeOnlyRunWithoutEntityFileGeneratesAllDtoFiles(@TempDir Path tempDir) throws Exception {
        new EasyfkGenerator(singleProject(tempDir), code()).generateCode();

        Path apiDir = tempDir.resolve("single-app/src/main/java/com/example/stress/api");
        String dto = Files.readString(apiDir.resolve("dto/Model0Dto.java"));
        assertFalse(dto.contains("FreeMarker template error"));
        assertTrue(dto.contains("private String field" + (FIELD_COUNT - 1) + ";"));
        assertTrue(Files.exists(apiDir.resolve("param/Model0Param.java")));
        assertTrue(Files.exists(apiDir.resolve("request/Model0Req.java")));
        assertTrue(Files.exists(apiDir.resolve("request/Model0EditReq.java")));
        assertTrue(Files.exists(apiDir.resolve("response/Model0Resp.java")));
    }

    @Test
    void unwritableTargetFailsLoudlyAndKeepsExistingFile(@TempDir Path tempDir) throws Exception {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"), "需要 POSIX 文件权限");
        assumeTrue(!"root".equals(System.getProperty("user.name")), "root 不受目录权限限制");
        ProjectProperties project = singleProject(tempDir);
        new EasyfkGenerator(project, code()).generateAll();
        Path dtoDir = tempDir.resolve("single-app/src/main/java/com/example/stress/api/dto");
        String before = Files.readString(dtoDir.resolve("Model0Dto.java"));

        // 模拟 Windows 下文件被 IDE 索引 / 杀毒软件占用导致无法替换
        Files.setPosixFilePermissions(dtoDir, PosixFilePermissions.fromString("r-xr-xr-x"));
        try {
            assertThrows(RuntimeException.class, () -> new EasyfkGenerator(project, code()).updateDtoAndParam(),
                    "文件写不进去时必须抛出异常，不能静默跳过");
        } finally {
            Files.setPosixFilePermissions(dtoDir, PosixFilePermissions.fromString("rwxr-xr-x"));
        }
        assertEquals(before, Files.readString(dtoDir.resolve("Model0Dto.java")));
    }

    // ------------------------------------------------------------------

    @FunctionalInterface
    private interface Job {
        void run(int thread, int iteration) throws Exception;
    }

    private static List<Throwable> runConcurrently(int threads, int iterations, Job job, Thread observer,
            AtomicBoolean done) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            int thread = t;
            futures.add(executor.submit(() -> {
                start.await();
                for (int iteration = 0; iteration < iterations; iteration++) {
                    job.run(thread, iteration);
                }
                return null;
            }));
        }
        if (observer != null) {
            observer.start();
        }
        start.countDown();
        List<Throwable> errors = new ArrayList<>();
        try {
            for (Future<?> future : futures) {
                try {
                    future.get(150, TimeUnit.SECONDS);
                } catch (Exception e) {
                    errors.add(e.getCause() != null ? e.getCause() : e);
                }
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));
            if (observer != null) {
                done.set(true);
                observer.join();
            }
        }
        return errors;
    }

    private static String checkFile(Path file, String expected) {
        try {
            String content = normalize(Files.readString(file, StandardCharsets.UTF_8));
            if (content.equals(expected)) {
                return null;
            }
            return content.isEmpty() ? "空文件" : "内容不完整/不一致（" + content.length() + "/" + expected.length() + " 字符）";
        } catch (NoSuchFileException e) {
            return "文件不存在";
        } catch (IOException e) {
            return "读取失败（半截 UTF-8？）：" + e;
        }
    }

    private static void assertNoErrors(List<Throwable> errors) {
        if (!errors.isEmpty()) {
            StringBuilder message = new StringBuilder("并发生成任务抛出异常 " + errors.size() + " 个：");
            errors.stream().limit(5).forEach(e -> message.append("\n - ").append(e));
            fail(message.toString());
        }
    }

    private static void assertSameTree(Map<String, String> expected, Map<String, String> actual, String label) {
        List<String> missing = new ArrayList<>();
        List<String> extra = new ArrayList<>();
        List<String> different = new ArrayList<>();
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String content = actual.get(entry.getKey());
            if (content == null) {
                missing.add(entry.getKey());
            } else if (!content.equals(entry.getValue())) {
                different.add(entry.getKey());
            }
        }
        for (String path : actual.keySet()) {
            if (!expected.containsKey(path)) {
                extra.add(path);
            }
        }
        if (!missing.isEmpty() || !extra.isEmpty() || !different.isEmpty()) {
            fail(label + "：缺失 " + missing.size() + " 个，多出 " + extra.size() + " 个，内容不同 " + different.size()
                    + " 个\n缺失示例：" + missing.stream().limit(5).toList()
                    + "\n多出示例：" + extra.stream().limit(5).toList()
                    + "\n不同示例：" + different.stream().limit(5).toList());
        }
    }

    private static Map<String, String> snapshot(Path root) throws IOException {
        Map<String, String> files = new TreeMap<>();
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path file : stream.filter(Files::isRegularFile).toList()) {
                files.put(root.relativize(file).toString().replace(File.separatorChar, '/'),
                        normalize(Files.readString(file, StandardCharsets.UTF_8)));
            }
        }
        return files;
    }

    private static String normalize(String content) {
        return DATE.matcher(content).replaceAll("<date>");
    }

    private static ProjectProperties project(Path dir) {
        ProjectProperties project = new ProjectProperties();
        project.setProjectDir(dir.toString());
        project.setProjectName("stress-app");
        project.setGroupId("com.example");
        project.setArtifactId("stress-app");
        project.setBasePackage("com.example.stress");
        project.setProjectType(ProjectType.SMART_ORM);
        project.setPrdType(PrdType.SEPARATION);
        project.setBuildType(BuildType.MAVEN);
        return project;
    }

    private static ProjectProperties singleProject(Path dir) {
        ProjectProperties project = project(dir);
        project.setProjectName("single-app");
        project.setArtifactId("single-app");
        project.setProjectType(ProjectType.SINGLE);
        project.setPrdType(PrdType.NONE);
        project.setOrmType(OrmType.MYBATIS);
        return project;
    }

    private static CodeProperties code() {
        CodeProperties code = new CodeProperties();
        code.setModuleName("stress");
        code.setDbShortUrl("127.0.0.1:3306/stress");
        code.setDbUser("root");
        code.setDbPwd("secret");
        List<ModelInfo> models = new ArrayList<>();
        for (int m = 0; m < MODEL_COUNT; m++) {
            List<FieldInfo> fields = new ArrayList<>();
            for (int f = 0; f < FIELD_COUNT; f++) {
                fields.add(new FieldInfo().setName("field" + f).setType(f % 3 == 0 ? "Long" : "String")
                        .setDesc("字段" + f));
            }
            models.add(new ModelInfo().setModelName("Model" + m).setTableName("t_model_" + m)
                    .setModelDesc("模型" + m).setFieldList(fields));
        }
        code.setModelList(models);
        return code;
    }
}
