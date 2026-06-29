# g2rain-generator-maven-plugin

[![Maven Central](https://img.shields.io/maven-central/v/com.g2rain/g2rain-generator-maven-plugin.svg)](https://search.maven.org/artifact/com.g2rain/g2rain-generator-maven-plugin)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-25+-orange.svg)](https://openjdk.java.net/)
[![Build Status](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://github.com/g2rain/g2rain-generator-maven-plugin)

## 1. 徽标与状态标识
- 当前版本通过 `Maven Central` 发布
- 当前运行时要求 `Java 25+`
- 当前构建方式以 `Maven` 为准
- 当前开源许可证为 `Apache 2.0`

## 2. 项目简介
`g2rain-generator-maven-plugin` 是 G2rain 平台面向 Java 后端项目的 Maven 代码生成插件。它基于 MyBatis Generator 与 FreeMarker，将数据库表结构转为符合平台工程习惯的 API、DTO、VO、DAO、PO、Service、Controller、Mapper XML 以及启动配置模板，帮助团队快速建立统一的后端模块骨架。

## 3. 平台定位

`g2rain-generator-maven-plugin` 位于 G2rain 平台工程化能力层，是平台 Java 后端项目初始化与 CRUD 骨架生成的重要工具。  
它主要服务于需要快速搭建标准分层结构的新模块和新项目。  
它不承担运行时业务逻辑，而是通过模板固化平台规范，降低重复搭建成本。

## 4. 核心能力

- Maven Goal 驱动生成：提供 `generate` 目标作为统一入口
- 多源配置解析：支持命令行、配置文件与交互输入
- MBG + FreeMarker 混合生成：先采集表元数据，再渲染平台模板
- 多模块输出：将代码路由到 `-api`、`-biz`、`-startup` 模块
- 隔离代码生成：支持基于租户列输出隔离相关接口与注解
- 可控覆盖策略：支持已存在文件跳过或覆盖

## 5. 技术栈

- 语言与运行时：`Java 25`
- 构建工具：`Maven`
- 构件类型：`maven-plugin`
- 核心依赖：`Maven Plugin API`、`MyBatis Generator`、`FreeMarker`、`MySQL Connector/J`
- 测试框架：`JUnit Jupiter`、`Mockito`
- 发布工具：`maven-plugin-plugin`、`GPG`、`Central Publishing`

## 6. 快速开始
### 环境要求

- `JDK 25`
- `Maven 3.9+`
- 可访问目标数据库

### Maven 插件接入

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.g2rain</groupId>
            <artifactId>g2rain-generator-maven-plugin</artifactId>
            <version>1.0.6</version>
        </plugin>
    </plugins>
</build>
```

### 配置文件示例

可在项目根目录准备 `codegen.properties`：

```properties
project.basePackage=com.example.demo

database.url=jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
database.driver=com.mysql.cj.jdbc.Driver
database.username=root
database.password=your_password

database.tables=user,order_info
tables.overwrite=false

data.isolation.withIsolation=true
data.isolation.tenantColumns=organ_id
data.isolation.excludeTables=dict_type,config
```

### 执行生成

```bash
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.6:generate
```

指定表：

```bash
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.6:generate -Ddatabase.tables=user
```

指定配置文件：

```bash
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.6:generate -Dconfig.file=./codegen.properties
```

### 参数优先级

- 命令行参数
- 配置文件
- 交互输入

### 本地构建

```bash
mvn clean install
```

### 本地测试

```bash
mvn test
```

### 发布说明

- 正式版通过 `release.yml` 发布
- 发布流程包含 source、javadoc、flatten、GPG 签名与 Central Publishing

## 7. 项目结构

```text
g2rain-generator-maven-plugin/
├── src/main/java/com/g2rain/generator/
│   ├── config/
│   ├── enums/
│   ├── generator/
│   ├── model/
│   ├── plugin/
│   └── utils/
├── src/main/resources/
│   ├── templates/
│   └── codegen.properties.example
├── src/test/java/com/g2rain/generator/
├── .github/workflows/
│   └── release.yml
└── pom.xml
```

### 核心能力结构说明

#### 1. `G2rainGenerateMojo`：插件入口与配置汇总
- 解决问题：把分散的生成参数统一收敛到 Maven Goal 中，减少工具使用门槛
- 核心逻辑：
  - 解析 `project.basePackage`、数据库连接、表名、覆盖开关、隔离开关等参数
  - 优先读取命令行，再补配置文件，最后在交互模式中提示输入
  - 校验必填项后构建 `FoundryConfig`
- 典型用法：在目标项目中直接调用 `mvn ...:generate`

典型写法：
```bash
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.6:generate \
  -Dproject.basePackage=com.example.demo \
  -Ddatabase.url=jdbc:mysql://localhost:3306/demo \
  -Ddatabase.driver=com.mysql.cj.jdbc.Driver \
  -Ddatabase.username=root \
  -Ddatabase.password=123456 \
  -Ddatabase.tables=user,order_info
```

#### 2. `FoundryConfig`：生成上下文与输出规则承载
- 解决问题：统一保存项目名、数据库连接、包路径和隔离策略，避免模板和生成器各自拼装规则
- 核心逻辑：
  - 保存项目名、基础包、JDBC 配置和表列表
  - 负责 API、DAO、PO、Service 等包路径计算
  - 负责 `withIsolation`、`tenantColumns`、`excludeTables` 的解析
- 典型用法：作为模板渲染的数据源与生成流程的统一上下文

#### 3. `FoundryGenerator`：生成链路编排器
- 解决问题：把“表结构采集”和“模板输出”串成可重复执行的统一流程
- 核心逻辑：
  - 初始化 MBG 配置
  - 注入 `TableInfoPlugin`
  - 执行表结构 introspect
  - 遍历 `TemplatePaths` 渲染 API、DTO、VO、DAO、PO、Service、Controller、Mapper 和共享配置模板
- 典型用法：`G2rainGenerateMojo` 完成配置后直接调用 `new FoundryGenerator(...).generate()`

#### 4. `TemplatePaths`：模板到多模块输出路由
- 解决问题：让不同模板稳定输出到固定模块与目录，避免生成结果结构漂移
- 核心逻辑：
  - 把模板绑定到 `-api`、`-biz`、`-startup`
  - 统一文件命名与输出路径格式
  - 控制共享模板和 `skipIfExists` 策略
- 典型用法：新增模板类型时，优先在这里补充路由规则，而不是把路径散落在生成流程中

#### 5. `TableInfoPlugin`：表元数据收集与平台模板适配
- 解决问题：默认 MBG 输出不完全匹配平台模板渲染需要，需先抽象成 `TableInfo`
- 核心逻辑：
  - 提取表、列、主键、基础字段、版本字段、逻辑删除字段信息
  - 拦截默认 Mapper 接口和 XML 输出
  - 为模板提供统一表结构模型
- 典型用法：模板中直接消费 `table` 模型，而不是直接依赖 MBG 内部对象

### 接入建议与边界
- 适合用于新模块初始化、标准 CRUD 骨架生成和平台规范落地
- 不建议把生成结果直接当成最终业务代码，应继续补充领域逻辑
- 模板是该仓库的重要资产，修改模板前应同步审视输出结构与测试

## 8. 常用命令

```bash
mvn clean install
mvn test
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.6:help
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.6:generate -Dconfig.file=./codegen.properties
```

## 9. 质量与测试
- 当前扫描到主源码文件 `13` 个，测试文件 `8` 个，模板文件 `14` 个
- 已覆盖配置优先级、隔离配置逻辑与模板渲染测试
- 插件发布链路包含 `maven-plugin-plugin`、`source`、`javadoc`、`flatten`、`GPG` 与 `Central Publishing`
- 后续如新增模板，建议同步补模板渲染测试

## 10. 相关仓库

- `g2rain-common`
- `g2rain-spring-boot-starter`
- `g2rain-crafter`
- `g2rain-app-cli`
- `g2rain-app-template`

## 11. 使用建议

- 适合作为平台 Java 项目初始化和表到代码骨架生成工具
- 适合先生成统一结构，再由研发补充领域逻辑
- 如涉及数据隔离能力，请结合 `withIsolation`、`tenantColumns` 与上层运行时组件一起使用
- 使用前建议先固定好目标项目的模块命名和基础包

## 12. 贡献指南

欢迎通过文档改进、Issue 反馈、测试补充、模板优化、功能增强等形式参与贡献。  
建议流程：
1. Fork 本仓库
2. 创建特性分支
3. 提交修改
4. 推送分支
5. 提交 Pull Request

提交前请尽量确保：
- 遵循现有技术栈与代码规范
- 更新相关文档
- 补充必要测试

## 13. 许可证

本项目基于 [Apache 2.0许可证](LICENSE) 开源。

## 14. 联系我们

- **站点**: https://www.g2rain.com/
- **Issues**: [GitHub Issues](https://github.com/g2rain/g2rain/issues)
- **讨论**: [GitHub Discussions](https://github.com/g2rain/g2rain/discussions)
- **邮箱**: g2rain_developer@163.com

## 15. 致谢

感谢所有为这个项目做出贡献的开发者们。  
如果这个项目对您有帮助，欢迎 Star 支持。
