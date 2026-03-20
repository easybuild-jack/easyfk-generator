<#if rpcType?? && rpcType == 'CLOUD'>
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        group: ${moduleName?uncap_first}-prod
</#if>

<#if rpcType?? && rpcType == 'DUBBO'>
dubbo:
  application:
    name: ${moduleName?uncap_first}-api
  registry:
    address: nacos://127.0.0.1:8848
  consumer:
    timeout: 3000
    retries: 2
</#if>

#跨域配置  
easyfk:
  config:
    web:
      cors:
        open: true  
  <#if appType == 'BMS'>
  #资源初始化
    resource:
      init:
        open: false
        #group: orderManager       
   </#if>
#knife4j配置
knife4j:
  #是否启用增强设置
  enable: true
  #开启生产环境屏蔽
  production: false
