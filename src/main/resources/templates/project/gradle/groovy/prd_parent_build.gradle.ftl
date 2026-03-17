subprojects {
    dependencies {
        api project(':${artifactId!projectName }-api')
        api ('com.mcst:web-prd')
        implementation('com.mcst:rpc-${rpcType?lower_case}')
        <#if projectType == "MICROSERVICE">
        implementation('com.mcst:registry-nacos')
        </#if>
        <#if projectType != "SMART" >
        implementation ('com.mcst:log-${logType?lower_case}')
        </#if>
<#if modules?has_content>
<#list modules as module>
<#if module.hasPrd && !module.prdSplit>
        implementation ('com.mcst:${module.name}-prd')
</#if>
</#list>
</#if>
    }
}
