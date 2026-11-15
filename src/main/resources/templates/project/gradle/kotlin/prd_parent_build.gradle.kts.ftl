subprojects {
    dependencies {
        add("api", project(":${artifactId!projectName }-api"))
        add("api", "com.mcst:web-prd")
<#if projectType == "MICROSERVICE" || projectType == "MICRO_PRD">
        add("implementation", "com.mcst:rpc-${rpcType?lower_case}")
        add("implementation", "com.mcst:registry-nacos")
</#if>
<#if projectType != "SMART" && projectType != "SMART_ORM" >
        add("implementation", "com.mcst:log-${logType?lower_case}")
</#if>
    }
}
