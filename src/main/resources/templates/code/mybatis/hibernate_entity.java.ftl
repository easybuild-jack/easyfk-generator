package ${package};

import ${modelInfo.superClass};
import com.mcst.easyfk.core.annotation.PrimaryKey;
import ${dtoPackage}.${modelInfo.modelName}Dto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.github.linpeilie.annotations.AutoMapper;
import com.mcst.easyfk.service.mybatisplus.annotation.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
* 作者: ${author}
* 日期: ${date}
*/
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name = "${modelInfo.tableName!}")
@AutoMapper(target = ${modelInfo.modelName}Dto.class)
public class ${modelInfo.modelName} extends ${modelInfo.superClass?keep_after_last(".")}<${modelInfo.modelName}> {
    <#if modelInfo.fieldList??>
    <#list modelInfo.fieldList as field>
    <#if field.keyFlag?? && field.keyFlag>

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    @Id
    @Column(comment = "${modelInfo.modelDesc!}ID")
    private ${field.type} ${field.name};
    <#else>

    @Column(comment = "${field.desc!}")
    private ${field.type} ${field.name};
    </#if>
    </#list>
    <#else >

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    @Id
    @Column(comment = "${modelInfo.modelDesc!}ID")
    private ${modelInfo.idType} ${modelInfo.modelName?uncap_first}Id;
    </#if>
}
