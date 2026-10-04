# 架构概览

Generator 通过 `g2rain:generate` 读取 JDBC 表元数据。MyBatis Generator 生成 PO/Mapper，`TableInfoPlugin` 收集字段语义，FreeMarker 模板生成 API、DTO、VO、DAO、Service、Controller 和应用配置。

Crafter `foundry` 复用 `FoundryGenerator`；因此公开 Goal 和 Java API 都属于兼容面。
