rootProject.name = '${artifactId!projectName }'

dependencyResolutionManagement.versionCatalogs{
    libs {
        library("dependencies", "com.mcst:easyfk-dependencies:${frameworkVersion }")
        library('lombok', 'org.projectlombok:lombok:1.18.38')
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

<#if prdType !="NONE" >
include '${artifactId!projectName }-prd'
  <#if prdType =="SEPARATION" >
include '${artifactId!projectName }-prd:${artifactId!projectName }-prd-client'
findProject(':${artifactId!projectName }-prd:${artifactId!projectName }-prd-client')?.name = '${artifactId!projectName }-prd-client'
include '${artifactId!projectName }-prd:${artifactId!projectName }-prd-bms'
findProject(':${artifactId!projectName }-prd:${artifactId!projectName }-prd-bms')?.name = '${artifactId!projectName }-prd-bms'
  </#if>
</#if>
