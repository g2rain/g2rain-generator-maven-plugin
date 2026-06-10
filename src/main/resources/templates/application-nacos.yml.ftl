spring:
  cloud:
    # http://192.168.1.20:8080/index.html#/register
    nacos:
      # 服务端地址
      server-addr: $<#noparse>{NACOS_SERVER_ADDR:127.0.0.1:8848}</#noparse>
      # 注册中心
      discovery:
        username: $<#noparse>{SPRING_CLOUD_NACOS_DISCOVERY_USERNAME:nacos}</#noparse>        # 注册中心用户
        password: $<#noparse>{SPRING_CLOUD_NACOS_DISCOVERY_PASSWORD:g2rain_nacos}</#noparse> # 注册中心密码
        namespace: $<#noparse>{SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE:dev}</#noparse>        # 隔离命名空间
        group: g2rain                                                   # 服务分组，微服务实例归属类别
        service: $<#noparse>{spring.application.name}</#noparse>                             # 服务名，方便注册和发现
        heart-beat-interval: 5000                                       # 服务实例心跳间隔时间（毫秒）
        heart-beat-timeout: 15000                                       # 服务实例心跳超时时间（毫秒）
      # 配置中心
      config:
        username: $<#noparse>{SPRING_CLOUD_NACOS_CONFIG_USERNAME:nacos}</#noparse>           # 配置中心用户
        password: $<#noparse>{SPRING_CLOUD_NACOS_CONFIG_PASSWORD:g2rain_nacos}</#noparse>    # 配置中心密码
        namespace: $<#noparse>{SPRING_CLOUD_NACOS_CONFIG_NAMESPACE:dev}</#noparse>           # 隔离命名空间
        group: $<#noparse>{spring.application.name}</#noparse>                               # 配置分组
        file-extension: yaml                                            # 配置文件的后缀名
        refresh-enabled: true                                           # 是否开启配置动态刷新
        max-retry: 3                                                    # 最大重试次数
        timeout: 5000                                                   # 获取配置的超时时间（毫秒）
  config:
    import: optional:nacos:$<#noparse>{spring.application.name}</#noparse>.yaml?group=$<#noparse>{spring.application.name}</#noparse>
