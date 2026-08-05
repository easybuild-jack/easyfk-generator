package com.mcst.easyfk.generator;

import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import jakarta.annotation.Resource;
import com.mcst.easyfk.generator.enums.ProjectType;

/**
 * easyfk 代码生成器
 *
 * @author eb-jack
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

    @Resource
    private ProjectProperties projectProperties;

    public EasyfkGenerator() {}

    public EasyfkGenerator(ProjectProperties projectProperties, CodeProperties codeProperties) {
        this.projectProperties = projectProperties;
        projectProperties.setModuleName(codeProperties.getModuleName());
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
        // SMART_ORM 模式默认全量生成自动装配配置（server/orm/remote/prd 全带 config）
        if (ProjectType.SMART_ORM.equals(projectProperties.getProjectType())) {
            configGenerator.create();
        }
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
