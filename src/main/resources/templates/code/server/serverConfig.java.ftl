package ${serverConfigPackage};

import ${apiPackage}.*;
import ${servicePackage}.*;
import ${repositoryPackage}.*;
import ${apiImplPackage}.*;
import ${serviceImplPackage}.*;
import ${repositoryImplPackage}.*;

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
public class ${moduleName?cap_first}ServerConfig {

<#if fileMap.apiImplFileSet??>
<#if projectType == 'SMART'>
  <#list fileMap.apiImplFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Api ${name?uncap_first}Api() {
        return new ${name}ApiServerImpl();
    }
  </#list>
<#else >
<#list fileMap.apiImplFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Remote ${name?uncap_first}Remote() {
        return new ${name}RemoteImpl();
    }
</#list>
</#if>
</#if>


<#if fileMap.serviceImplFileSet??>
    <#list fileMap.serviceImplFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Service ${name?uncap_first}Service() {
        return new ${name}ServiceImpl();
    }

    </#list>
</#if>
<#if fileMap.repositoryImplFileSet??>
    <#list fileMap.repositoryImplFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Repository ${name?uncap_first}Repository() {
        return new ${name}RepositoryImpl();
    }

    </#list>
</#if>

}
