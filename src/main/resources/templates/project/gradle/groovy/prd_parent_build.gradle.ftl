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
    }
}
