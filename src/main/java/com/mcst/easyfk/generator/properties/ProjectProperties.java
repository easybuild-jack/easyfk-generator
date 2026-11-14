package com.mcst.easyfk.generator.properties;

import com.mcst.easyfk.generator.enums.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建项目配置
 *
 * @author eb-jack
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
     * ORM 实现模块列表，仅 project-type 为 SMART_ORM 时生效。
     * 为空时默认生成全部：MYBATIS(plus) / MYBATIS_FLEX(flex) / HIBERNATE(hibernate)
     */
    private List<OrmType> ormModules;

    /**
     * RPC类型，用于微服务项目
     */
    private RpcType rpcType = RpcType.NONE;

    /**
     * 项目目录 必填
     */
    private String projectDir;

    /**
     * 框架版本
     */
    private String frameworkVersion = "3.5.16";

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
    private OrmType ormType = OrmType.NONE;

    /**
     * PRD层项目生成类型，none：不生成，single：生成一个PRD项目，separation：分别生成后台BMS 和 2C端PRD项目
     */
    private PrdType prdType = PrdType.NONE;

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
     * 应用类型，BMS(后台管理端) / CLIENT(面向C端)
     */
    private AppType appType = AppType.BMS;

    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 是否引入 auth 权限模块
     */
    private Boolean includeAuth = Boolean.FALSE;

    /**
     * 获取框架 ORM 组件的 artifactId 后缀
     * MYBATIS -> mybatis, MYBATIS_FLEX -> flex, HIBERNATE -> hibernate
     */
    public String getOrmModuleSuffix() {
        return switch (ormType) {
            case NONE -> "";
            case MYBATIS -> "mybatis";
            case MYBATIS_FLEX -> "flex";
            case HIBERNATE -> "hibernate";
        };
    }

    /**
     * 获取业务 ORM 组件的 artifactId 后缀
     * MYBATIS -> plus, MYBATIS_FLEX -> flex, HIBERNATE -> hibernate
     */
    public String getOrmBusinessSuffix() {
        return switch (ormType) {
            case NONE -> "";
            case MYBATIS -> "plus";
            case MYBATIS_FLEX -> "flex";
            case HIBERNATE -> "hibernate";
        };
    }

    /**
     * 获取 ORM 实现模块列表，未配置时默认返回全部实现（plus/flex/hibernate）
     */
    public List<OrmType> getOrmModules() {
        if (ormModules == null || ormModules.isEmpty()) {
            return List.of(OrmType.MYBATIS, OrmType.MYBATIS_FLEX, OrmType.HIBERNATE);
        }
        return new ArrayList<>(ormModules);
    }

    public void setOrmModules(List<OrmType> ormModules) {
        this.ormModules = ormModules;
    }

    /**
     * 获取 ORM 模块短名列表（plus/flex/hibernate），与 ormModules 顺序一致
     */
    public List<String> getOrmModuleList() {
        return getOrmModules().stream().map(ProjectProperties::ormModuleName).toList();
    }

    /**
     * ORM 类型 -> ORM 模块短名
     */
    public static String ormModuleName(OrmType ormType) {
        return switch (ormType) {
            case MYBATIS -> "plus";
            case MYBATIS_FLEX -> "flex";
            case HIBERNATE -> "hibernate";
            default -> throw new IllegalArgumentException("不支持的 ORM 实现模块类型: " + ormType);
        };
    }

}
