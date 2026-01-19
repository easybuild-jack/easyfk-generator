package ${package};

import ${modelInfo.superClass};

import com.mcst.easyfk.core.annotation.PrimaryKey;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import ${dtoPackage}.${modelInfo.modelName}Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import com.mcst.easyfk.service.mybatisplus.annotation.Column;

/**
* 作者: ${author}
* 日期: ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("${modelInfo.tableName!}")
@AutoMapper(target = ${modelInfo.modelName}Dto.class)
public class ${modelInfo.modelName} extends ${modelInfo.superClass?keep_after_last(".")}<${modelInfo.modelName}> {

    <#if modelInfo.fieldList??>
    <#list modelInfo.fieldList as field>
    <#if field.keyFlag?? && field.keyFlag>

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @TableId(type = IdType.AUTO)
    <#else >
    @TableId(type = IdType.ASSIGN_UUID)
    </#if>
    @Column(comment = "${modelInfo.modelDesc!}ID")
    private ${field.type} ${field.name};
    <#else>

    @Column(comment = "${field.desc}")
    private ${field.type} ${field.name};
    </#if>
    </#list>
    <#else >

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @TableId(type = IdType.AUTO)
    <#else >
    @TableId(type = IdType.ASSIGN_UUID)
    </#if>
    @Column(comment = "${modelInfo.modelDesc!}ID")
    private ${modelInfo.idType} ${modelInfo.modelName?uncap_first}Id;
    </#if>
}