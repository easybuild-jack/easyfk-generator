dependencies {
    api ('com.mcst:web-micro')
    api project(':${artifactId!projectName }-remote:${artifactId!projectName }-dubbo-api')
    api project(':${artifactId!projectName }-server')
    compileOnly('com.mcst:rpc-dubbo')
<#if modules?has_content>
<#list modules as module>
    implementation ('com.mcst:${module.name}-dubbo-provider')
</#list>
</#if>
}