# 测试策略

- 单元测试覆盖配置优先级、隔离规则、列模型、校验规则和模板渲染。
- 验证 plugin/help descriptor 与发布 Jar 内模板。
- 隔离数据库覆盖多表、类型、主键、nullable、长度、逻辑删除和租户列。
- 临时目录覆盖 overwrite、skipIfExists、非法包/表/路径和符号链接。
- 同时验证独立 `generate` Goal 与 Crafter foundry，并构建生成项目。
- 日志和生成文件不得出现数据库密码。
