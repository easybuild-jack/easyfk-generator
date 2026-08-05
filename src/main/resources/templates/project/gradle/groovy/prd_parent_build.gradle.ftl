subprojects {
    dependencies {
        api project(':${artifactId!projectName }-api')
        api ('com.mcst:web-prd')
        <#if projectType == "MICROSERVICE" || projectType == "MICRO_PRD">
        implementation('com.mcst:rpc-${rpcType?lower_case}')
        implementation('com.mcst:registry-nacos')
        </#if>
        <#if projectType != "SMART" && projectType != "SMART_ORM" >
        implementation ('com.mcst:log-${logType?lower_case}')
        </#if>
    }
}
