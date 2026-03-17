dependencies {
    api ('com.mcst:web-micro')
    api project(':${artifactId!projectName }-remote:${artifactId!projectName }-cloud-api')
    api project(':${artifactId!projectName }-server')
    compileOnly ('com.mcst:remote-cloud')
<#if modules?has_content>
<#list modules as module>
    implementation ('com.mcst:${module.name}-cloud-provider')
</#list>
</#if>
}
