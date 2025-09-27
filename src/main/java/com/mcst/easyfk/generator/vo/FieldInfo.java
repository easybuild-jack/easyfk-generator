package com.mcst.easyfk.generator.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liu yijun
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class FieldInfo {
    /**
     * 类型
     */
    private String type = "String";

    /**
     * 字段名
     */
    private String name;

    /*
     * 描述
     */
    private String desc;

    /**
     * 是否有ID 标识
     */
    private Boolean keyFlag = Boolean.FALSE;

    /**
     * 是否自增长
     */
    private Boolean autoIncrement = Boolean.FALSE;

    /**
     * 是否是单字段唯一
     */
    private Boolean singleUniqueField = Boolean.FALSE;

    /**
     * 是否是组合唯一
     */
    private Boolean combUniqueField = Boolean.FALSE;

    /**
     * 是否支持区间查询
     */
    private Boolean betweenField = Boolean.FALSE;

    /**
     * 重复时的提示语
     */
    private String repetitionMsg;

    /**
     * 组合的 字段
     */
    private String combinationField;

    /**
     * 长度
     */
    private Integer length;

    /**
     * 小数位数
     */
    private Integer decimalLength;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 是否是influxdb的tag
     */
    private Boolean influxdbTag = Boolean.FALSE;

    /**
     * 是否是influxdb的时间戳
     */
    private Boolean influxdbTimestamp = Boolean.FALSE;
}
