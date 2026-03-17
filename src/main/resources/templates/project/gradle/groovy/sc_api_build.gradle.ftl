dependencies {
    compileOnly ('org.springframework:spring-web')
    compileOnly ('org.springframework.cloud:spring-cloud-starter-openfeign')
    api ('com.mcst:remote-cloud')
<#if modules?has_content>
<#list modules as module>
    implementation ('com.mcst:${module.name}-cloud-api')
</#list>
</#if>
}
