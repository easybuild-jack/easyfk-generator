package ${package};


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;


/**
* <p>
* ${modelInfo.modelDesc!}
* </p>
*
* @author ${author}
* @since ${date}
*/
@Data
@Accessors(chain = true)
@Measurement(name = "${modelInfo.tableName}")
public class ${modelInfo.modelName} implements Serializable {

<#if modelInfo.fieldList??>
<#list modelInfo.fieldList as field>

    <#if field.desc!?length gt 0>
    /**
    * ${field.desc}
    */
    </#if>
    <#if field.tagFlag>
    @Column(name = "${field.columnName}", tag = true)
    <#elseif field.timestampFlag>
    @Column(timestamp = true)
    <#else>
    @Column(name = "${field.columnName}")
    </#if>
    private ${field.type} ${field.name};
</#list>
</#if>


} 