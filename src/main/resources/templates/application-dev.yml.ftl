# profile=dev 时加载；与 nacos 互斥，勿同时激活
spring:
  cloud:
    nacos:
      config:
        enabled: false
        import-check:
          enabled: false
      discovery:
        enabled: false
  datasource:
    host: ${config.getHost()}
    port: ${config.getPort()}
    database: ${config.getDatabase()}
    username: ${config.getUsername()}
    password: ${config.getPassword()}
    url: jdbc:mysql://$<#noparse>{spring.datasource.host}</#noparse>:$<#noparse>{spring.datasource.port}</#noparse>/$<#noparse>{spring.datasource.database}</#noparse>?useUnicode=true&characterEncoding=utf-8&useSSL=true&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-timeout: 30000
      validation-timeout: 3000
      idle-timeout: 30000
      max-lifetime: 300000
      minimum-idle: 1
      maximum-pool-size: 10
      connection-test-query: select 1

logging:
  level:
    ${config.getDaoPackage()}: debug
