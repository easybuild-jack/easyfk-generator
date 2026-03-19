rootProject.name = '${artifactId!projectName }'

dependencyResolutionManagement.versionCatalogs{
    libs {
        library("dependencies", "com.mcst:easyfk-dependencies:3.2.12")
        library("moduleDependencies", "com.mcst:module-dependencies:3.2.12")
        library('lombok', 'org.projectlombok:lombok:1.18.38')
        library('lombokMapstruct', 'org.projectlombok:lombok-mapstruct-binding:0.2.0')
        library('mapstructProcessor', 'io.github.linpeilie:mapstruct-plus-processor:1.5.0')
        library('processor', 'org.springframework.boot:spring-boot-configuration-processor:3.2.12')
    }
}


include '${artifactId!projectName }-server'
include '${artifactId!projectName }-api'

<#if projectType =="SMART" >
include '${artifactId!projectName }-remote'
include '${artifactId!projectName }-remote:${artifactId!projectName }-cloud-api'
findProject(':${artifactId!projectName }-remote:${artifactId!projectName }-cloud-api')?.name = '${artifactId!projectName }-cloud-api'
include '${artifactId!projectName }-remote:${artifactId!projectName }-cloud-provider'
findProject(':${artifactId!projectName }-remote:${artifactId!projectName }-cloud-provider')?.name = '${artifactId!projectName }-cloud-provider'
include '${artifactId!projectName }-remote:${artifactId!projectName }-dubbo-api'
findProject(':${artifactId!projectName }-remote:${artifactId!projectName }-dubbo-api')?.name = '${artifactId!projectName }-dubbo-api'
include '${artifactId!projectName }-remote:${artifactId!projectName }-dubbo-provider'
findProject(':${artifactId!projectName }-remote:${artifactId!projectName }-dubbo-provider')?.name = '${artifactId!projectName }-dubbo-provider'
</#if>

<#if prdType =="SINGLE" >
include '${artifactId!projectName }-prd'
</#if>
<#if prdType =="SEPARATION" >
include '${artifactId!projectName }-prd-client'
include '${artifactId!projectName }-prd-bms'
</#if>
