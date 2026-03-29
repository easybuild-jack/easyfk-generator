package com.mcst.easyfk.generator.properties;

import com.mcst.easyfk.generator.enums.DbType;
import com.mcst.easyfk.generator.vo.ModelInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 创建项目配置
 *
 * @author eb-jack
 */
@ConfigurationProperties(prefix = "easyfk.config.generator.code")
@Data
public class CodeProperties {

    /**
     * 模块名称 必填
     */
    private String moduleName;

    /**
     * 是否使用Spring注解方式
     */
    private Boolean springAnnotation = Boolean.TRUE;

    /**
     * 作者信息
     */
    private String author = "eb-jack";

    private Boolean extendsSupperClass = Boolean.TRUE;

    private List<ModelInfo> modelList;

    /**
     * 表前缀
     */
    private String tablePrefix;

    private String dbShortUrl;

    private String dbUser = "root";

    private String dbPwd;

    private DbType dbType = DbType.MYSQL;

    /**
     * 资源所属组
     */
    private String resourceGroup;

    /**
     * 资源权限名称
     */
    private String resourceName;

    /**
     * 资源权限ID
     */
    private String resourceId;

    /**
     * 资源权限指向的前端路由路径
     */
    private String resourcePath;

    /**
     * 资源组权限排序
     */
    private Integer resourceSort = 10000;

    /**
     * 当前模块资源排序
     */
    private Integer moduleResourceSort = 10000;

    /**
     * 排序的 增量起始值
     */
    private Integer incrementStar = 100;

    /**
     * 是否生成资源权限注解
     */
    private Boolean createResourceAnnotation = Boolean.FALSE;

    /**
     * 需要从数据库初始化生成Model的表
     */
    private String dbTables;

    public String getDbTables() {
        return dbTables;
    }

    public void setDbTables(String dbTables) {
        this.dbTables = dbTables;
    }

}
