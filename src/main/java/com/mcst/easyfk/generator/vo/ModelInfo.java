package com.mcst.easyfk.generator.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author eb-jack
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class ModelInfo {
    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 数据库表名
     */
    private String tableName;
    /**
     * 模型描述
     */
    private String modelDesc;
    /**
     * 父类
     */
    private String superClass;

    /**
     * 主键类型 String/Long
     */
    private String idType = "String";

    /**
     * 主键是否自增
     */
    private Boolean idIsAuto = Boolean.FALSE;

    /**
     * 是否只生成Repository
     */
    private Boolean onlyRepository = Boolean.FALSE;

    /**
     * 字段列表
     */
    private List<FieldInfo> fieldList;

    /**
     * 需要导入的基础包列表
     */
    private List<ImportPackage> basicPackages;

    /**
     * 需要导入的其他包列表
     */
    private List<ImportPackage> otherPackages;

    /**
     * 禁用 字段 如果存在 禁用字段，controller 层会生成 启用/禁用 接口，反之则不生成
     */
    private String forbiddenFiled;

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
    private Integer resourceSort;

    /**
     * 当前model资源组权限排序
     */
    private Integer modelResourceSort;

    /**
     * 表前缀
     */
    private String tablePrefix;


    /**
     * 是否创建controller，控制当前model是否生成controller层
     */
    private Boolean createController = Boolean.TRUE;
}
