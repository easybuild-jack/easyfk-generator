dependencies {
    api ('com.mcst:remote-${rpcType?lower_case}')
<#if rpcType == "CLOUD">
    compileOnly ('org.springframework.cloud:spring-cloud-starter-openfeign')
    compileOnly ('org.springframework:spring-web')
</#if>
}
