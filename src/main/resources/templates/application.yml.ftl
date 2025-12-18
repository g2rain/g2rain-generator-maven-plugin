server:
  port: 8080
spring:
  threads:
    virtual:
      enabled: true
  application:
    name: <#if config.getProjectName()??>${config.getProjectName()}<#else>g2rain-demo</#if>
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://$<#noparse>{spring.datasource.host}</#noparse>:$<#noparse>{spring.datasource.port}</#noparse>/$<#noparse>{spring.datasource.database}</#noparse>?useUnicode=true&characterEncoding=utf-8&useSSL=true&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8
    username: $<#noparse>{spring.datasource.username}</#noparse>
    password: $<#noparse>{spring.datasource.password}</#noparse>
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-timeout: 30000
      validation-timeout: 3000
      idle-timeout: 30000
      max-lifetime: 300000
      minimum-idle: 1
      maximum-pool-size: 10
      connection-test-query: select 1

## Mybatis
mybatis:
  mapper-locations: classpath:/mybatis/mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    jdbc-type-for-null: NULL
