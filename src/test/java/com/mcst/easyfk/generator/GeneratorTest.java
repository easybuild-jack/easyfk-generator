package com.mcst.easyfk.generator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

@SpringBootTest
public class GeneratorTest {

    @Resource
    private EasyfkGenerator easyfkGenerator;

    /**
     * 生成项目及model
     */
    @Test
    public void generateProjectAndModel() {
        easyfkGenerator.generateProjectAndModel();
    }

    @Test
    public void generateCode() {
        easyfkGenerator.generateCode();
    }

    @Test
    public void generateModel() {
        easyfkGenerator.generateModel();
    }

    @Test
    public void generateCodeAndConfig() {
        easyfkGenerator.generateCodeAndConfig();
    }

    @Test
    public void updateDtoAndParam() {
        easyfkGenerator.updateDtoAndParam();
    }

    @Test
    public void generateConfig() {
        easyfkGenerator.generateConfig();
    }

    @Test
    public void generateAll() {
        easyfkGenerator.generateAll();
    }

    @Test
    public void generateProject() {
        easyfkGenerator.generateProject();
    }

}
