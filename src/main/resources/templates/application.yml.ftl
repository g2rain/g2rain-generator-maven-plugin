server:
  port: $<#noparse>{SERVER_PORT:8080}</#noparse>

spring:
  threads:
    virtual:
      enabled: true
  application:
    name: <#if config.getProjectName()??>${config.getProjectName()}<#else>g2rain-demo</#if>
  # 环境切换（dev / nacos 互斥，勿同时激活或 spring.profiles.include）：
  #   SPRING_PROFILES_ACTIVE=dev   → application.yml + application-dev.yml（本地数据源）
  #   SPRING_PROFILES_ACTIVE=nacos → application.yml + application-nacos.yml（注册/配置中心，数据源来自 Nacos）
  profiles:
    active: $<#noparse>{SPRING_PROFILES_ACTIVE:dev}</#noparse>
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 20MB

mybatis:
  mapper-locations: classpath:/mybatis/mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    jdbc-type-for-null: NULL

g2rain:
  web:
    context-path: /<#if config.getProjectName()??>${config.getProjectName()}<#else>g2rain-demo</#if>
    cors:
      enabled: true
  data:
    isolation:
      enabled: true

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
