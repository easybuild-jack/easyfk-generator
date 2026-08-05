<#if projectType == 'SMART' || projectType == 'SMART_ORM'>
package ${remoteConfigPackage};
<#else >
package ${apiConfigPackage};
</#if>


<#if projectType == 'SMART' || projectType == 'SMART_ORM'>
import ${apiPackage}.*;
import ${remoteClientImplPackage}.*;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
</#if>
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
*
* @author ${author}
*/
@AutoConfiguration
public class ${moduleName?cap_first}ApiConfig {
<#if projectType == 'SMART' || projectType == 'SMART_ORM'>
<#if fileMap.dubboRemoteImplFileSet??>
    <#list fileMap.dubboRemoteImplFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Api ${name?uncap_first}Api() {
         return new ${name}ApiDubboImpl();
    }

    </#list>
</#if>
</#if>

}
