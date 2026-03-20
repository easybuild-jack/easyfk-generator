dependencies {
    api ('com.mcst:remote-dubbo')
    compileOnly("com.mcst:rpc-dubbo")
<#if includeAuth>
    implementation ('com.mcst:auth-dubbo-api')
</#if>
}