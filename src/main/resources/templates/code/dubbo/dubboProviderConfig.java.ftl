package ${remoteProviderConfigPackage};

import ${remoteApiPackage}.*;
import ${remoteProviderPackage}.*;
import org.apache.dubbo.config.ServiceConfig;
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
    public ServiceConfig<I${name}Remote> ${name?uncap_first}RemoteServiceConfig(${name}RemoteImpl ${name?uncap_first}RemoteImpl) {
        ServiceConfig<I${name}Remote> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(I${name}Remote.class);
        serviceConfig.setRef(${name?uncap_first}RemoteImpl);
        serviceConfig.export();
        return serviceConfig;
    }

    </#list>
</#if>

}
