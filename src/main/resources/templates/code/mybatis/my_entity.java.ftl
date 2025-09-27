package ${package};

import com.mcst.easyfk.service.mybatisplus.persistence.${modelInfo.superClass};

import com.mcst.easyfk.core.annotation.PrimaryKey;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
/**
* 作者: ${author}
* 日期: ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("${modelInfo.tableName!}")
@Table(name = "${modelInfo.tableName!}", comment = "${modelInfo.modelDesc!}")
public class ${modelInfo.modelName} extends ${modelInfo.superClass}<${modelInfo.modelName}> {
    <#if modelInfo.fieldList??>
    <#list modelInfo.fieldList as field>
    <#if field.keyFlag?? && field.keyFlag>

    @PrimaryKey
    <#if modelInfo.idIsAuto?? && modelInfo.idIsAuto>
    @TableId(type = IdType.AUTO)
    <#else >
    @TableId(type = IdType.ASSIGN_UUID)
    </#if>
    @Column(comment = "${field.desc!}", length = ${field.length?c} , isKey = true)
    private ${field.type} ${field.name};
    <#else>

    @Column(comment = "${field.desc!}", length = ${field.length?c})
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
    @Column(comment = "${modelInfo.modelName}Id", isKey = true, length = 50)
    private ${modelInfo.idType} ${modelInfo.modelName?uncap_first}Id;
    </#if>
}