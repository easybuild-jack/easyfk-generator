package ${paramPackage};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.mcst.easyfk.core.dto.request.BasicParam;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
<#list modelInfo.basicPackages as pkg>
${pkg.packageName}
</#list>

/**
* <p>
* ${modelInfo.modelDesc!}数据参数对象
* </p>
*
* @author ${author}
* @since ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description="${modelInfo.modelDesc!}数据参数对象")
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class ${modelInfo.modelName}Param extends BasicParam {

<#list modelInfo.fieldList as field>
<#if field.desc!?length gt 0>
    /**
    * ${field.desc}
    */
</#if>
    @Schema(description= "${field.desc!}")
    private ${field.type} ${field.name};

</#list>

<#list modelInfo.fieldList as field><#if field.betweenField>
<#if field.desc!?length gt 0>
    /**
    * ${field.desc}开始
    */
</#if>
    @Schema(description= "${field.desc!}开始")
    private ${field.type} ${field.name}Start;

<#if field.desc!?length gt 0>
    /**
    * ${field.desc}
    */
</#if>
    @Schema(description= "${field.desc!}结束")
    private ${field.type} ${field.name}End;

</#if>
</#list>
}