dependencies {
    api project(':${artifactId!projectName }-api')
<#if ormType?? && ormType != "NONE">
    implementation ('com.mcst:orm-${ormType?lower_case}')
<#if dbType?? && dbType.name() == "POSTGRE_SQL">
    implementation ('com.mcst:db-postgresql')
<#else>
    implementation ('com.mcst:db-mysql')
</#if>
</#if>
<#if projectType == "SINGLE">
    implementation ('com.mcst:web-prd')
<#elseif  projectType == "MICROSERVICE">
    implementation ('com.mcst:web-micro')
    implementation('com.mcst:registry-nacos')
    implementation('com.mcst:rpc-${rpcType?lower_case}')
</#if >
<#if projectType != "SMART" >
    implementation ('com.mcst:log-${logType?lower_case}')
</#if>
<#if modules?has_content>
<#list modules as module>
<#if module.hasServer>
    implementation ('com.mcst:${module.name}-server')
</#if>
<#if module.hasOrm>
    implementation ('com.mcst:${module.name}-orm-${ormModuleSuffix}')
</#if>
</#list>
</#if>
}

<#if projectType != "SMART" >
// 将依赖包复制到lib目录
tasks.register('copyJar', Copy) {
    // 清除现有的lib目录
    delete layout.buildDirectory.dir("libs/lib")
    from configurations.runtimeClasspath
    into layout.buildDirectory.dir("libs/lib")
}

tasks.named('jar') {
    // 自定义jar包名称
    // archiveFileName = '${artifactId!projectName }.jar'
    // lib目录的清除和复制任务
    dependsOn copyJar
    // lib目录的清除和复制任务
    // dependsOn copyConfigFile
    manifest {
        attributes(
            'Class-Path': configurations.runtimeClasspath.files.collect { "lib/$it.name" }.join(' '),
            'Main-Class': '${basePackage}.${moduleName?cap_first}ServerApp'
        )
    }
}
</#if>
