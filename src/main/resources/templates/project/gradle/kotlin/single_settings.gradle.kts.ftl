rootProject.name = "${artifactId!projectName }"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("dependencies", "com.mcst:easyfk-dependencies:${frameworkVersion}")
            library("moduleDependencies", "com.mcst:module-dependencies:${frameworkVersion}")
            library("lombok", "org.projectlombok:lombok:1.18.46")
            library("lombokMapstruct", "org.projectlombok:lombok-mapstruct-binding:0.2.0")
            library("mapstructProcessor", "io.github.linpeilie:mapstruct-plus-processor:1.5.3")
            library("processor", "org.springframework.boot:spring-boot-configuration-processor:${frameworkVersion}")
<#if ormType??><#if ormType.name() == "MYBATIS_FLEX">
            library("flexProcessor", "com.mybatis-flex:mybatis-flex-processor:1.11.8")
</#if></#if>
        }
    }
}
