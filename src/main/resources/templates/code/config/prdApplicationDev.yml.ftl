<#if projectType == 'MICROSERVICE' && rpcType == 'CLOUD'>
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        group: ${moduleName?uncap_first}-dev
</#if>

<#if projectType == 'MICROSERVICE' && rpcType == 'DUBBO'>
dubbo:
  application:
    name: ${moduleName?uncap_first}-api
  registry:
    address: nacos://127.0.0.1:8848
  consumer:
    timeout: 3000
    retries: 2
</#if>

#开启CORS 配置
easyfk:
  config:
    web:
      cors:
        open: true
