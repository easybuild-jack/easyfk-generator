package com.mcst.easyfk.generator;

import com.mcst.easyfk.generator.enums.BuildType;
import com.mcst.easyfk.generator.enums.OrmType;
import com.mcst.easyfk.generator.enums.ProjectType;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import com.mcst.easyfk.generator.util.GeneratorUtil;
import com.mcst.easyfk.generator.vo.FieldInfo;
import com.mcst.easyfk.generator.vo.ModelInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorSmokeTest {

    @Test
    void resolvesMyBatisPlusModelFromSpringModule() {
        assertEquals("com.baomidou.mybatisplus.spring.activerecord.Model",
                GeneratorUtil.resolveSuperClass(OrmType.MYBATIS, false));
        assertEquals("com.mybatisflex.core.activerecord.Model",
                GeneratorUtil.resolveSuperClass(OrmType.MYBATIS_FLEX, false));
        assertEquals("com.mcst.easyfk.service.mybatisplus.persistence.BaseMyBatisPlusEntity",
                GeneratorUtil.resolveSuperClass(OrmType.MYBATIS, true));
    }

    @Test
    void generatesSingleProjectAgainstSampleSchema(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("maven");
        ProjectProperties project = baseProject(output);
        project.setBuildType(BuildType.MAVEN);
        project.setOrmType(OrmType.MYBATIS);
        project.setProjectType(ProjectType.SINGLE);

        CodeProperties code = baseCode(false);
        new EasyfkGenerator(project, code).generateAll();

        String pom = Files.readString(output.resolve("demo-app/pom.xml"), StandardCharsets.UTF_8);
        assertTrue(pom.contains("<framework.version>3.5.16</framework.version>"));
        assertTrue(pom.contains("<version>1.18.46</version>"));
        assertTrue(pom.contains("<version>1.5.3</version>"));
        assertTrue(pom.contains("<version>3.14.1</version>"));
        assertTrue(pom.contains("<version>3.4.2</version>"));
        assertTrue(pom.contains("<version>3.8.1</version>"));
        assertTrue(pom.contains("<version>3.5.6</version>"));
        assertFalse(pom.contains("3.2.12"));
        assertFalse(pom.contains("1.18.32"));

        String entity = Files.readString(output.resolve(
                "demo-app/src/main/java/com/example/demo/service/persistence/model/Product.java"),
                StandardCharsets.UTF_8);
        assertTrue(entity.contains("import com.baomidou.mybatisplus.spring.activerecord.Model;"));
        assertTrue(entity.contains("extends Model<Product>"));
        assertFalse(entity.contains("extension.activerecord.Model"));
    }

    @Test
    void generatesGradleCatalogAtFrameworkVersion(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("gradle");
        ProjectProperties project = baseProject(output);
        project.setBuildType(BuildType.GRADLE);
        project.setOrmType(OrmType.MYBATIS_FLEX);
        project.setProjectType(ProjectType.SINGLE);

        CodeProperties code = baseCode(true);
        new EasyfkGenerator(project, code).generateProjectAndModel();

        String settings = Files.readString(output.resolve("demo-app/settings.gradle"), StandardCharsets.UTF_8);
        assertTrue(settings.contains("com.mcst:easyfk-dependencies:3.5.16"));
        assertTrue(settings.contains("com.mcst:module-dependencies:3.5.16"));
        assertTrue(settings.contains("org.projectlombok:lombok:1.18.46"));
        assertTrue(settings.contains("mapstruct-plus-processor:1.5.3"));
        assertTrue(settings.contains("spring-boot-configuration-processor:3.5.16"));
        assertTrue(settings.contains("mybatis-flex-processor:1.11.8"));
        assertFalse(settings.contains("3.2.12"));

        String entity = Files.readString(output.resolve(
                "demo-app/src/main/java/com/example/demo/service/persistence/model/Product.java"),
                StandardCharsets.UTF_8);
        assertTrue(entity.contains("com.mcst.easyfk.service.flex.persistence.BaseFlexEntity"));
    }

    private static ProjectProperties baseProject(Path output) {
        ProjectProperties project = new ProjectProperties();
        project.setProjectDir(output.toString());
        project.setProjectName("demo-app");
        project.setGroupId("com.example");
        project.setBasePackage("com.example.demo");
        project.setArtifactId("demo-app");
        return project;
    }

    private static CodeProperties baseCode(boolean extendsFramework) {
        CodeProperties code = new CodeProperties();
        code.setModuleName("demo");
        code.setExtendsSupperClass(extendsFramework);
        code.setDbShortUrl("127.0.0.1:3306/demo");
        code.setDbUser("root");
        code.setDbPwd("secret");
        ModelInfo product = new ModelInfo()
                .setModelName("Product")
                .setModelDesc("商品")
                .setTableName("product")
                .setFieldList(List.of(new FieldInfo().setName("productName").setType("String").setDesc("名称")));
        code.setModelList(List.of(product));
        return code;
    }
}
