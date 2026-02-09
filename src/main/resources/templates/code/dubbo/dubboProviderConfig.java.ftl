package ${remoteProviderConfigPackage};

import ${remoteApiPackage}.*;
import ${remoteProviderPackage}.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
*
* @author ${author}
*/
@AutoConfiguration
public class ${moduleName?cap_first}ProviderConfig {

<#if fileMap.dubboRemoteProviderFileSet??>
    <#list fileMap.dubboRemoteProviderFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Remote ${name?uncap_first}Remote() {
         return new ${name}RemoteImpl();
    }

    </#list>
</#if>

}
