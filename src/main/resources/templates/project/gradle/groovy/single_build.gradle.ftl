//指定项目版本号和group
version = '${projectVersion }'
group = '${groupId }'
apply plugin: 'java-library'
apply plugin: 'maven-publish'


java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'

dependencies {
    implementation platform(libs.dependencies)
    implementation platform(libs.moduleDependencies)
    compileOnly libs.lombok
    testCompileOnly libs.lombok
    annotationProcessor(libs.lombok, libs.processor,libs.lombokMapstruct,libs.mapstructProcessor)
    testAnnotationProcessor(libs.lombok, libs.processor,libs.lombokMapstruct,libs.mapstructProcessor)
    implementation ('com.mcst:orm-${ormType?lower_case}')
    implementation ('com.mcst:log-${logType?lower_case}')
<#if projectType == "SINGLE" || projectType == "MICROSERVICE">
    implementation ('com.mcst:web-prd')
</#if >
<#if influxDb>
    implementation ('com.mcst:orm-influxdb')
</#if>
}

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
            'Main-Class': '${basePackage}.ServerApp'
        )
    }
}

tasks.withType(JavaCompile).configureEach {
    options.compilerArgs += ['-Amapstruct.plus.nullValuePropertyMappingStrategy=IGNORE']
}
