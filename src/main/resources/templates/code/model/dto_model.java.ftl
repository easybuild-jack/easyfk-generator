package ${dtoPackage};
import com.mcst.easyfk.core.annotation.PrimaryKey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Accessors;
import com.mcst.easyfk.core.dto.BaseDto;
<#list modelInfo.basicPackages as pkg>
${pkg.packageName}
</#list>
<#list modelInfo.otherPackages as pkg>
${pkg.packageName}
</#list>

/**
* <p>
* ${modelInfo.modelDesc!}
* </p>
*
* @author ${author}
* @since ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description="${modelInfo.modelDesc!}数据对象")
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class ${modelInfo.modelName}Dto extends BaseDto {

<#list modelInfo.fieldList as field>

    <#if field.desc!?length gt 0>
    /**
    * ${field.desc}
    */
    </#if>
    <#if field.keyFlag>
    @PrimaryKey
    </#if>
    <#if field.singleUniqueField>
    @SingleUniqueField(repetitionMsg = "${field.repetitionMsg}")
    </#if>
    <#if field.combUniqueField>
    @CombUniqueField(repetitionMsg = "${field.repetitionMsg}", combinationField = "${field.combinationField}")
    </#if>
    @Schema(description= "${field.desc!}")
    private ${field.type} ${field.name};
</#list>


}
