server:
  port: 6001
logging:
  config: file:config/${logName}.xml

spring:
  application:
    name: ${moduleName?uncap_first}-server
  config:
    import:
      - "file:config/application-dev.yml"