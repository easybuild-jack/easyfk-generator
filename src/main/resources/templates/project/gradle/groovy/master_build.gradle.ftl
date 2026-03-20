// 所有项目通用配置
allprojects {
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
}
subprojects {
    dependencies {
        implementation platform(libs.dependencies)
        implementation platform(libs.moduleDependencies)
        compileOnly libs.lombok
        testCompileOnly libs.lombok
        annotationProcessor(libs.lombok, libs.processor,libs.lombokMapstruct,libs.mapstructProcessor)
        testAnnotationProcessor(libs.lombok, libs.processor,libs.lombokMapstruct,libs.mapstructProcessor)
<#if ormType??><#if ormType.name() == "MYBATIS_FLEX">
        annotationProcessor 'com.mybatis-flex:mybatis-flex-processor:1.11.6'
</#if></#if>
    }
    tasks.withType(JavaCompile).configureEach {
        options.compilerArgs += ['-Amapstruct.plus.nullValuePropertyMappingStrategy=IGNORE']
    }
}
