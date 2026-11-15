plugins {
    `java-library`
}

dependencies {
    api(project(":${artifactId!projectName }-api"))
    implementation("com.mcst:web-prd")
<#if projectType == "MICROSERVICE" || projectType == "MICRO_PRD">
    implementation("com.mcst:rpc-${rpcType?lower_case}")
    implementation("com.mcst:registry-nacos")
    implementation("com.mcst:doc-knife4j")
</#if>
<#if projectType != "SMART" && projectType != "SMART_ORM" >
    implementation("com.mcst:log-${logType?lower_case}")
</#if>
<#if includeAuth && appType?? && appType == "BMS">
    implementation("com.mcst:auth-prd")
</#if>
}
<#if projectType == "MICROSERVICE">
// 将依赖包复制到 lib 目录（Sync 会清理 lib 目录中已失效的文件，等价于原「删除目录 + Copy」）
tasks.register<Sync>("copyJar") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs/lib"))
}

tasks.named<Jar>("jar") {
    // 自定义 jar 包名称
    // archiveFileName.set("${artifactId!projectName }.jar")
    // lib 目录的清除和复制任务
    dependsOn("copyJar")
    // lib 目录的清除和复制任务
    // dependsOn("copyConfigFile")
    manifest {
        attributes(
            "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { "lib/" + it.name },
            "Main-Class" to "${basePackage}.${moduleName?cap_first}PrdApp"
        )
    }
}
</#if>
