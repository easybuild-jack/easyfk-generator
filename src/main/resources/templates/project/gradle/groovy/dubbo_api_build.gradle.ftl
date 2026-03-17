dependencies {
    api ('com.mcst:remote-dubbo')
    compileOnly("com.mcst:rpc-dubbo")
<#if modules?has_content>
<#list modules as module>
    implementation ('com.mcst:${module.name}-dubbo-api')
</#list>
</#if>
}