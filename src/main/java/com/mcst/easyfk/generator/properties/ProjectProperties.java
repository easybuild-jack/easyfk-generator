package com.mcst.easyfk.generator.properties;

import com.mcst.easyfk.generator.enums.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 创建项目配置
 *
 * @author liu yijun
 */
@ConfigurationProperties(prefix = "easyfk.config.generator.project")
@Data
public class ProjectProperties {

    /**
     * 项目名称 必填
     */
    private String projectName;

    /**
     * 项目类型，必填
     */
    private ProjectType projectType = ProjectType.SMART;

    /**
     * RPC类型，用于微服务项目
     */
    private RpcType rpcType = RpcType.CLOUD;

    /**
     * 项目目录 必填
     */
    private String projectDir;

    /**
     * 框架版本
     */
    private String frameworkVersion = "3.2.12";

    private String projectVersion = "1.0.0-SNAPSHOT";

    /**
     * groupId 必填
     */
    private String groupId;
    /**
     * artifactId
     */
    private String artifactId;

    /**
     * 持久化类型，mybatis，mongo，all，none
     */
    private OrmType ormType = OrmType.MYBATIS;

    /**
     * PRD层项目生成类型，none：不生成，single：生成一个PRD项目，separation：分别生成后台BMS 和 2C端PRD项目
     */
    private PrdType prdType = PrdType.NONE;

    /**
     * 是否生成独立的Repository项目
     */
    private Boolean createRepository = Boolean.FALSE;

    /**
     * 项目构建方式，Maven/Gradle 默认Maven
     */
    private BuildType buildType = BuildType.MAVEN;

    /**
     * Groovy / Kotlin
     */
    private GradleType gradleType = GradleType.GROOVY;


    /**
     * 项目包根路径
     */
    private String basePackage;

    /**
     * 是否创建Prd项目的自动加载配置
     */
    private Boolean controllerAutoConfig = Boolean.FALSE;

    /**
     * 日志类型
     */
    private LogType logType = LogType.LOGBACK;

    /**
     * 是否生成PRD项目
     */
    private Boolean createPrdProject = Boolean.TRUE;


    /**
     * 是否包含influxdb
     */
    private Boolean influxDb = Boolean.FALSE;

}
