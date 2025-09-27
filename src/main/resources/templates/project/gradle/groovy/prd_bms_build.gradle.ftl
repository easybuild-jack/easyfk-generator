<#if projectType == "MICROSERVICE">

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
                'Main-Class': '${basePackage}.BmsApp'
            )
        }
}
</#if>
