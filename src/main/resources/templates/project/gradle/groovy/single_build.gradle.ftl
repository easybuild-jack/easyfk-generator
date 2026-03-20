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
<#if ormType??><#if ormType.name() == "MYBATIS_FLEX">
    annotationProcessor libs.flexProcessor
</#if></#if>
<#if ormType?? && ormType != "NONE">
    implementation ('com.mcst:orm-${ormModuleSuffix}')
<#if dbType?? && dbType.name() == "POSTGRE_SQL">
    implementation ('com.mcst:db-postgresql')
<#else>
    implementation ('com.mcst:db-mysql')
</#if>
</#if>
    implementation ('com.mcst:log-${logType?lower_case}')
    implementation ('com.mcst:web-prd')
    implementation ('com.mcst:doc-knife4j')
<#if includeAuth>
<#if appType?? && appType == "BMS">
    implementation ('com.mcst:auth-prd')
</#if>
    implementation ('com.mcst:auth-server')
<#if ormType?? && ormType != "NONE">
    implementation ('com.mcst:auth-orm-${ormBusinessSuffix}')
</#if>
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
            'Main-Class': '${basePackage}.${moduleName?cap_first}ServerApp'
        )
    }
}

tasks.withType(JavaCompile).configureEach {
    options.compilerArgs += ['-Amapstruct.plus.nullValuePropertyMappingStrategy=IGNORE']
}
