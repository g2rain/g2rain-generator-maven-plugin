# AGENTS.md

评审或开发前读取 `docs/project.yaml`、`docs/architecture/deviations.md`、中央 Generator 工具登记、当前需求、模板、源码、测试和 Git Diff。

- 本仓库是数据库代码生成引擎，不是生产服务，也不采用生成项目的运行时 Profile。
- 表元数据、模板和输出路径是跨项目契约；变化需同时验证独立 Goal 和 Crafter foundry。
- 所有输出必须限制在目标项目根内，默认不得覆盖现有非空文件。
- 数据库密码不得出现在日志、示例真实值、生成文件、测试或仓库。
- 生成代码必须人工 Review、测试和构建，不能把生成器当作领域事实来源。

至少执行 `mvn test`。生成契约变化还需使用隔离数据库在临时目录生成并构建完整项目。需求仅选择 `aiCoding.activeRequirement` 或唯一 `开发中` 文档。
