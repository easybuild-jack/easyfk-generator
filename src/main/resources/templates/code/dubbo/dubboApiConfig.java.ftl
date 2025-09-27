<#if projectType == 'SMART'>
package ${remoteConfigPackage};
<#else >
package ${apiConfigPackage};
</#if>


<#if projectType == 'SMART'>
import ${apiPackage}.*;
import ${remoteClientImplPackage}.*;
import ${remoteBasePackage}.properties.${moduleName?cap_first}ApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
<#else >
import ${apiPropertyPackage}.${moduleName?cap_first}ApiProperties;
</#if>
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
*
* @author ${author}
*/
@Configuration
@EnableConfigurationProperties(${moduleName?cap_first}ApiProperties.class)
public class ${moduleName?cap_first}ApiConfig {
<#if projectType == 'SMART'>
<#if fileMap.remoteImplFileSet??>
    <#list fileMap.remoteImplFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Api ${name?uncap_first}Api() {
         return new ${name}ApiScImpl();
    }

    </#list>
</#if>
</#if>

}
