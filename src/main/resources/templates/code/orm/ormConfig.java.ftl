package ${configPackage};

import ${repositoryPackage}.*;
import ${implPackage}.*;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
/**
*
* @author ${author}
*/
@AutoConfiguration
@EnableConfigurationProperties
public class ${configJavaName} {

<#if fileMap.ormRepositoryImplFileSet??>
    <#list fileMap.ormRepositoryImplFileSet as name>
    @Bean
    @ConditionalOnMissingBean
    public I${name}Repository ${name?uncap_first}Repository() {
        return new ${name}RepositoryImpl();
    }

    </#list>
</#if>

}
