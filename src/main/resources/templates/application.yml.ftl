server:
  port: $<#noparse>{SERVER_PORT:8080}</#noparse>

spring:
  threads:
    virtual:
      enabled: true
  application:
    name: <#if config.getProjectName()??>${config.getProjectName()}<#else>g2rain-demo</#if>
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

logging:
  level:
    ${config.getDaoPackage()}: debug
