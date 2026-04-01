package ${package};

<#if modelInfo.superClass?? && modelInfo.superClass?has_content>
import ${modelInfo.superClass};
</#if>
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Id;
import com.mcst.easyfk.core.annotation.PrimaryKey;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
<#if modelInfo.superClass?? && modelInfo.superClass?has_content>
import lombok.EqualsAndHashCode;
</#if>
import lombok.experimental.Accessors;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import ${dtoPackage}.${modelInfo.modelName}Dto;
import com.mcst.easyfk.repository.persistence.annotation.EntityColumn;
<#assign hasLocalDateTime = false /><#assign hasLocalDate = false /><#assign hasLocalTime = false /><#assign hasBigDecimal = false /><#assign hasUUID = false />
<#if modelInfo.fieldList??><#list modelInfo.fieldList as field><#if field.type == "LocalDateTime"><#assign hasLocalDateTime = true /></#if><#if field.type == "LocalDate"><#assign hasLocalDate = true /></#if><#if field.type == "LocalTime"><#assign hasLocalTime = true /></#if><#if field.type == "BigDecimal"><#assign hasBigDecimal = true /></#if><#if field.type == "UUID"><#assign hasUUID = true /></#if></#list></#if>
<#if hasLocalDateTime>
import java.time.LocalDateTime;
</#if>
<#if hasLocalDate>
import java.time.LocalDate;
</#if>
<#if hasLocalTime>
import java.time.LocalTime;
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
<#if hasUUID>
import java.util.UUID;
</#if>
/**
* 作者: ${author}
* 日期: ${date}
*/
@Data
<#if modelInfo.superClass?? && modelInfo.superClass?has_content>
@EqualsAndHashCode(callSuper = true)
</#if>
@Accessors(chain = true)
@Table("${modelInfo.tableName!}")
@AutoMapper(target = ${modelInfo.modelName}Dto.class)
@FieldNameConstants
public class ${modelInfo.modelName}<#if modelInfo.superClass?? && modelInfo.superClass?has_content> extends ${modelInfo.superClass?keep_after_last(".")}<${modelInfo.modelName}></#if> {
    <#if modelInfo.fieldList??>
    <#list modelInfo.fieldList as field>
    <#if field.keyFlag?? && field.keyFlag>

    <#if field.desc!?length gt 0>
    /**
    * ${field.desc}
    */
    </#if>
    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @Id(keyType = KeyType.AUTO)
    <#else >
    @Id(keyType = KeyType.Generator)
    </#if>
    @EntityColumn(comment = "${field.desc!}")
    private ${field.type} ${field.name};
    <#else>

    <#if field.desc!?length gt 0>
    /**
    * ${field.desc}
    */
    </#if>
    @EntityColumn(comment = "${field.desc!}")
    private ${field.type} ${field.name};
    </#if>
    </#list>
    <#else >

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @Id(keyType = KeyType.AUTO)
    <#else >
    @Id(keyType = KeyType.Generator)
    </#if>
    @EntityColumn(comment = "${modelInfo.modelDesc!}Id")
    private ${modelInfo.idType} ${modelInfo.modelName?uncap_first}Id;
    </#if>
}