package com.mcst.easyfk.generator.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模板渲染与文件写入。
 * <p>
 * 先把模板完整渲染到内存，再写入同目录下的临时文件并原子替换目标文件：
 * 并发运行的其它生成任务、IDE 索引等读者只会看到旧的完整文件或新的完整文件，
 * 不会看到缺失、空或半截的文件；渲染/写入失败时抛出异常且不留下残缺文件。
 *
 * @author eb-jack
 * @version 1.0
 */
public class CreateFileUtil {

    /** Configuration 配置完成后线程安全，按模板根路径共享，模板由其内部缓存 */
    private static final Map<String, Configuration> CONFIGURATIONS = new ConcurrentHashMap<>();

    /** Windows 下目标文件被 IDE 索引、杀毒软件等短暂占用时，替换会失败，需要重试 */
    private static final int REPLACE_MAX_ATTEMPTS = 10;
    private static final long REPLACE_RETRY_BASE_MILLIS = 20;

    /**
     * 创建文件 文件存在时，不生成
     *
     */
    public static void createOnNotExistFile(String templateFile, String filePath, Object param,
            String templateBasePath) throws Exception {
        Path target = Paths.get(filePath);
        if (Files.exists(target)) {
            return;
        }
        String content = render(templateFile, filePath, param, templateBasePath);
        write(target, content, false);
    }

    public static void delAndCreateFile(String templateFile, String filePath, Object param,
            String templateBasePath) throws Exception {
        String content = render(templateFile, filePath, param, templateBasePath);
        write(Paths.get(filePath), content, true);
    }

    private static String render(String templateFile, String filePath, Object param, String templateBasePath)
            throws Exception {
        if (templateFile == null) {
            throw new IllegalArgumentException("未找到可用的模板，无法生成文件：" + filePath);
        }
        Template template = getConfiguration(templateBasePath).getTemplate(templateFile);
        StringWriter out = new StringWriter();
        template.process(param, out);
        return out.toString();
    }

    private static Configuration getConfiguration(String templateBasePath) {
        return CONFIGURATIONS.computeIfAbsent(templateBasePath, basePath -> {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_35);
            configuration.setClassForTemplateLoading(CreateFileUtil.class, basePath);
            // 默认的 DEBUG_HANDLER 会把错误堆栈写进生成的文件里
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setLogTemplateExceptions(false);
            return configuration;
        });
    }

    private static void write(Path target, String content, boolean overwrite) throws IOException {
        Path parent = target.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        Path temp = writeTempFile(parent, target, content);
        try {
            if (overwrite) {
                replace(temp, target);
            } else {
                try {
                    Files.move(temp, target);
                } catch (FileAlreadyExistsException e) {
                    // 其它任务已并发生成了该文件，保留已有文件
                }
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    /**
     * 临时文件以 "." 开头（目录扫描如 ConfigGenerator 需忽略），与目标同目录以保证可原子替换。
     * 不用 Files.createTempFile：它在 POSIX 上固定为 0600 权限，替换后生成文件会变成仅属主可读。
     */
    private static Path writeTempFile(Path dir, Path target, String content) throws IOException {
        while (true) {
            Path temp = dir.resolve("." + target.getFileName() + "."
                    + Integer.toHexString(ThreadLocalRandom.current().nextInt()) + ".tmp");
            try {
                Files.writeString(temp, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW,
                        StandardOpenOption.WRITE);
                return temp;
            } catch (FileAlreadyExistsException e) {
                // 临时文件重名，换一个名字重试
            } catch (IOException e) {
                Files.deleteIfExists(temp);
                throw e;
            }
        }
    }

    private static void replace(Path source, Path target) throws IOException {
        for (int attempt = 1; ; attempt++) {
            try {
                try {
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
                return;
            } catch (FileSystemException e) {
                if (attempt >= REPLACE_MAX_ATTEMPTS) {
                    throw e;
                }
                try {
                    Thread.sleep(REPLACE_RETRY_BASE_MILLIS * attempt);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }
}
