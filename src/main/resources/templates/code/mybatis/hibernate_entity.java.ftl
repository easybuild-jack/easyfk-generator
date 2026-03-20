package ${package};

<#if modelInfo.superClass?? && modelInfo.superClass?has_content>
import ${modelInfo.superClass};
</#if>
import com.mcst.easyfk.core.annotation.PrimaryKey;
import ${dtoPackage}.${modelInfo.modelName}Dto;
import lombok.Data;
<#if modelInfo.superClass?? && modelInfo.superClass?has_content>
import lombok.EqualsAndHashCode;
</#if>
import lombok.experimental.Accessors;
import io.github.linpeilie.annotations.AutoMapper;
import com.mcst.easyfk.repository.persistence.annotation.EntityColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Entity
<#if modelInfo.superClass?? && modelInfo.superClass?has_content>
@EqualsAndHashCode(callSuper = true)
</#if>
@Accessors(chain = true)
@Table(name = "${modelInfo.tableName!}")
@AutoMapper(target = ${modelInfo.modelName}Dto.class)
public class ${modelInfo.modelName}<#if modelInfo.superClass?? && modelInfo.superClass?has_content> extends ${modelInfo.superClass?keep_after_last(".")}<${modelInfo.modelName}></#if> {
    <#if modelInfo.fieldList??>
    <#list modelInfo.fieldList as field>
    <#if field.keyFlag?? && field.keyFlag>

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    @Id
    @EntityColumn(comment = "${modelInfo.modelDesc!}ID")
    private ${field.type} ${field.name};
    <#else>

    @EntityColumn(comment = "${field.desc!}")
    private ${field.type} ${field.name};
    </#if>
    </#list>
    <#else >

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    @Id
    @EntityColumn(comment = "${modelInfo.modelDesc!}ID")
    private ${modelInfo.idType} ${modelInfo.modelName?uncap_first}Id;
    </#if>
}
