package com.mcst.easyfk.generator;

import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import jakarta.annotation.Resource;

/**
 * easyfk 代码生成器
 *
 * @author liuyijun
 */
public class EasyfkGenerator {

    @Resource
    private ProjectGenerator projectGenerator;
    @Resource
    private CodeGenerator codeGenerator;
    @Resource
    private ModelGenerator modelGenerator;

    @Resource
    private ConfigGenerator configGenerator;

    public EasyfkGenerator() {}

    public EasyfkGenerator(ProjectProperties projectProperties, CodeProperties codeProperties) {
        this.projectGenerator = new ProjectGenerator(projectProperties);
        this.modelGenerator = new ModelGenerator(projectProperties, codeProperties);
        this.codeGenerator = new CodeGenerator(projectProperties, codeProperties);
        this.configGenerator = new ConfigGenerator(projectProperties, codeProperties);
    }

    /**
     * 生成项目
     */
    public void generateProject() {
        projectGenerator.create();
    }

    /**
     * 生成model
     */
    public void generateModel() {
        modelGenerator.create();
    }

    /**
     * 生成项目及model
     */
    public void generateProjectAndModel() {
        projectGenerator.create();
        modelGenerator.create();
    }

    /**
     * 生成代码
     */
    public void generateCode() {
        codeGenerator.create(false);
    }

    /**
     * 更新dto和param
     */
    public void updateDtoAndParam() {
        codeGenerator.create(true);
    }

    /**
     * 生成代码及自动装配 自动装配 一般用于 smart 类型项目
     */
    public void generateCodeAndConfig() {
        codeGenerator.create(false);
        configGenerator.create();
    }

    /**
     * 生成自动装配 一般用于 smart 类型项目
     */
    public void generateConfig() {
        configGenerator.create();
    }

    public void generateAll() {
        projectGenerator.create();
        modelGenerator.create();
        codeGenerator.create(false);
        configGenerator.create();
    }

}
