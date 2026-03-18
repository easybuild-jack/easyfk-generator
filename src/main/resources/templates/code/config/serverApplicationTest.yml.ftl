spring:
<#if projectType == 'MICROSERVICE' && rpcType == 'CLOUD'>
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        group: ${moduleName?uncap_first}-test
</#if>
<#if ormType?? && ormType != 'NONE'>
  datasource:
    url: ${dbUrl}
    username: ${dbUser}
    password: ${dbPwd}
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: ${driverName}
</#if>
<#if projectType == 'MICROSERVICE' && rpcType == 'DUBBO'>
dubbo:
  application:
    name: ${moduleName?uncap_first}-server
    version: 1.0.0
  registry:
    address: nacos://127.0.0.1:8848
  protocol:
    name: dubbo
    port: 20880
  scan:
    base-packages: ${groupId }.**.service.impl
</#if>
<#if ormType == 'MYBATIS' || ormType == 'MYBATIS_FLEX'>
#mybatis-plus 配置
mybatis-plus:
  mapper-locations: classpath*:/mappings/**/*Mapper.xml
  global-config:
    # 数据库相关配置
    db-config:
    #主键类型  AUTO:"数据库ID自增", INPUT:"用户输入ID",ID_WORKER:"全局唯一ID (数字类型唯一ID)", UUID:"全局唯一ID UUID";
      id-type: auto
      #逻辑删除配置
      logic-delete-value: 1
      logic-not-delete-value: 0
  # 原生配置
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    #调试sql语句设置
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
</#if>

<#if projectType == 'SINGLE' && appType == 'BMS'>
easyfk:
  config:
    #跨域配置  
    web:
      cors:
        open: true  

    #系统用户初始化
    auth:
      init: false
    #资源初始化
    resource:
      init:
        open: false
        #group: orderManager

  
#knife4j配置
knife4j:
  #是否启用增强设置
  enable: true
  #开启生产环境屏蔽
  production: false
</#if>