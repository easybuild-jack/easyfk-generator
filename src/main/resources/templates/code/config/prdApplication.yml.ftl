server:
  port: 9001
logging:
  config: file:config/${logName}.xml

spring:
  application:
    name: ${moduleName?uncap_first}-api
  profiles:
    active: dev
<#if projectType == 'MICROSERVICE' && rpcType == 'CLOUD'>
  main:
    ## @FeignClient(value = "服务名") 设置可以有多个类存在相同的FeignClient 中的value值
    allow-bean-definition-overriding: true

</#if>