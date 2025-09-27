spring:
<#if projectType == 'MICROSERVICE' && rpcType == 'CLOUD'>
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        group: ${moduleName?uncap_first}-dev
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
</#if>


<#if ormType == 'MYBATIS'>
#自动生成表结构 配置
mybatis:
  table:
    #create 系统启动后，会将所有的表删除掉，然后根据model中配置的结构重新建表，该操作会破坏原有数据。
    #update 系统会自动判断哪些表是新建的，哪些字段要修改类型等，哪些字段要删除，哪些字段要新增，该操作不会破坏原有数据。
    #none 不做任何操作
    #add 新增表/新增字段/新增索引/新增唯一约束的功能，不做修改和删除 (只在版本1.0.9.RELEASE及以上支持)。
    auto: update
  model:
    pack: 'com.mcst.**.persistence.model' #扫描用于创建表的对象的包名，多个包用“,”隔开
  database:
    type: mysql #数据库类型 目前只支持mysql

#mybatis-plus 配置
mybatis-plus:
  mapper-locations: classpath*:com/gitee/sunchenbin/mybatis/actable/mapping/*/*.xml,/mappings/**/*Mapper.xml
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
