plugins {
    `java-library`
    `maven-publish`
}

// 指定项目版本号和 group
version = "${projectVersion }"
group = "${groupId }"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    implementation(platform(libs.dependencies))
    implementation(platform(libs.moduleDependencies))
    compileOnly(libs.lombok)
    testCompileOnly(libs.lombok)
    annotationProcessor(libs.lombok.get())
    annotationProcessor(libs.processor.get())
    annotationProcessor(libs.lombokMapstruct.get())
    annotationProcessor(libs.mapstructProcessor.get())
    testAnnotationProcessor(libs.lombok.get())
    testAnnotationProcessor(libs.processor.get())
    testAnnotationProcessor(libs.lombokMapstruct.get())
    testAnnotationProcessor(libs.mapstructProcessor.get())
<#if ormType??><#if ormType.name() == "MYBATIS_FLEX">
    annotationProcessor(libs.flexProcessor.get())
</#if></#if>
<#if ormType?? && ormType != "NONE">
    implementation("com.mcst:orm-${ormModuleSuffix}")
<#if dbType?? && dbType.name() == "POSTGRE_SQL">
    implementation("com.mcst:db-postgresql")
<#else>
    implementation("com.mcst:db-mysql")
</#if>
</#if>
    implementation("com.mcst:log-${logType?lower_case}")
    implementation("com.mcst:web-prd")
    implementation("com.mcst:doc-knife4j")
<#if includeAuth>
<#if appType?? && appType == "BMS">
    implementation("com.mcst:auth-prd")
</#if>
    implementation("com.mcst:auth-server")
<#if ormType?? && ormType != "NONE">
    implementation("com.mcst:auth-orm-${ormBusinessSuffix}")
</#if>
</#if>
}

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
            "Main-Class" to "${basePackage}.${moduleName?cap_first}ServerApp"
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Amapstruct.plus.nullValuePropertyMappingStrategy=IGNORE")
}
