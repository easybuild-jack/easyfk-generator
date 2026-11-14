rootProject.name = '${artifactId!projectName }'

dependencyResolutionManagement.versionCatalogs{
    libs {
        library("dependencies", "com.mcst:easyfk-dependencies:${frameworkVersion}")
        library("moduleDependencies", "com.mcst:module-dependencies:${frameworkVersion}")
        library('lombok', 'org.projectlombok:lombok:1.18.46')
        library('lombokMapstruct', 'org.projectlombok:lombok-mapstruct-binding:0.2.0')
        library('mapstructProcessor', 'io.github.linpeilie:mapstruct-plus-processor:1.5.3')
        library('processor', 'org.springframework.boot:spring-boot-configuration-processor:${frameworkVersion}')
<#if ormType??><#if ormType.name() == "MYBATIS_FLEX">
        library('flexProcessor', 'com.mybatis-flex:mybatis-flex-processor:1.11.8')
</#if></#if>
    }
}


include '${artifactId!projectName }-server'
include '${artifactId!projectName }-api'

<#if projectType =="SMART" || projectType =="SMART_ORM" >
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

<#if projectType =="SMART_ORM" >
include '${artifactId!projectName }-orm'
include '${artifactId!projectName }-orm:${artifactId!projectName }-orm-api'
findProject(':${artifactId!projectName }-orm:${artifactId!projectName }-orm-api')?.name = '${artifactId!projectName }-orm-api'
<#list ormModuleList as om>
include '${artifactId!projectName }-orm:${artifactId!projectName }-orm-${om}'
findProject(':${artifactId!projectName }-orm:${artifactId!projectName }-orm-${om}')?.name = '${artifactId!projectName }-orm-${om}'
</#list>
</#if>

<#if prdType =="SINGLE" >
include '${artifactId!projectName }-prd'
</#if>
<#if prdType =="SEPARATION" >
include '${artifactId!projectName }-prd-client'
include '${artifactId!projectName }-prd-bms'
</#if>
