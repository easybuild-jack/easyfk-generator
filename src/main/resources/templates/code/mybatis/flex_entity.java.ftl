package ${package};

import ${modelInfo.superClass};
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mcst.easyfk.core.annotation.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import com.mcst.easyfk.service.flex.persistence.BaseFlexEntity;
import ${dtoPackage}.${modelInfo.modelName}Dto;
/**
* 作者: ${author}
* 日期: ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table("${modelInfo.tableName!}")
@AutoMapper(target = ${modelInfo.modelName}Dto.class)
public class ${modelInfo.modelName} extends ${modelInfo.superClass?keep_after_last(".")}<${modelInfo.modelName}> {
    <#if modelInfo.fieldList??>
    <#list modelInfo.fieldList as field>
    <#if field.keyFlag?? && field.keyFlag>

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @Id(type = KeyType.AUTO)
    <#else >
    @Id(type = KeyType.Generator)
    </#if>
    @Column(comment = "${field.desc!}")
    private ${field.type} ${field.name};
    <#else>

    @Column(comment = "${field.desc!}")
    private ${field.type} ${field.name};
    </#if>
    </#list>
    <#else >

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @Id(type = KeyType.AUTO)
    <#else >
    @Id(type = KeyType.Generator)
    </#if>
    @Column(comment = "${modelInfo.modelName}Id")
    private ${modelInfo.idType} ${modelInfo.modelName?uncap_first}Id;
    </#if>
}