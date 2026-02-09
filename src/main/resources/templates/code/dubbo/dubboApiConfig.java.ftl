<#if projectType == 'SMART'>
package ${remoteConfigPackage};
<#else >
package ${apiConfigPackage};
</#if>


<#if projectType == 'SMART'>
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
<#if projectType == 'SMART'>
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
