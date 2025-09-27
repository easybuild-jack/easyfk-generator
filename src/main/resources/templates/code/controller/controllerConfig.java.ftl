package ${prdConfigPackage};

import ${prdControllerPackage}.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
/**
*
* @author ${author}
*/
@Configuration
@EnableConfigurationProperties
public class ${configJavaName} {

<#if fileMap.controllerFileSet??>
    <#list fileMap.controllerFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public ${name}Controller ${name?uncap_first}Controller() {
         return new ${name}Controller();
    }

    </#list>
</#if>
<#if fileMap.bmsControllerFileSet??>
    <#list fileMap.bmsControllerFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public ${name}BmsController ${name?uncap_first}BmsController() {
        return new ${name}BmsController();
    }

    </#list>
</#if>

}
