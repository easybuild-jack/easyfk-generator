dependencies {
    compileOnly ('org.springframework:spring-web')
    compileOnly ('org.springframework.cloud:spring-cloud-starter-openfeign')
    api ('com.mcst:remote-cloud')
<#if includeAuth>
    implementation ('com.mcst:auth-cloud-api')
</#if>
}
