# 发布

执行单元测试和打包；使用隔离数据库验证已打包插件；通过 Crafter 生成并构建完整项目；检查模板、descriptor、sources、javadoc、POM 和签名；发布后用 Maven Central 坐标执行 help/generate 冒烟。

GPG、Central 和数据库凭据只从 CI Secret 注入。模板或 Generator 行为不能在同一已发布版本下静默变化。
