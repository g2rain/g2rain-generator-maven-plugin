## G2rain Generator Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/com.g2rain/g2rain-generator-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.g2rain/g2rain-generator-maven-plugin)
[![Build](https://github.com/g2rain/g2rain-generator-maven-plugin/actions/workflows/release.yml/badge.svg)](https://github.com/g2rain/g2rain-generator-maven-plugin/actions/workflows/release.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://adoptium.net/)

一个基于 **MyBatis Generator** 和 **FreeMarker** 的 Java 代码生成 Maven 插件，可根据数据库表结构，一键生成包含 **API / DTO / VO / Service / Controller / DAO / PO / Mapper XML** 在内的完整 CRUD 代码，主要面向 Spring Boot/RESTful 风格项目。我们在跨项目协作上倡导“读写分离”社区实践：**默认只输出查询类接口**，写请求（新增/更新/删除）建议由业务方通过消息、事件或其他服务独立实现，以降低项目之间的耦合和副作用。

### Maven 坐标

在业务项目（建议是父工程）的 `pom.xml` 中引入插件坐标：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.g2rain</groupId>
            <artifactId>g2rain-generator-maven-plugin</artifactId>
            <version>1.0.1</version>
        </plugin>
    </plugins>
</build>
```

### 功能特性

- **一键生成**：通过 Maven 命令自动生成整套 CRUD 代码
- **多层分层**：支持 API、Controller、Service、DAO、PO、DTO、VO 等完整分层
- **模板驱动**：基于 FreeMarker 模板，可按需定制生成内容
- **多种配置方式**：支持配置文件、命令行参数、交互式输入三种方式
- **多表支持**：支持一次生成多个表的代码（逗号分隔）
- **可控覆盖**：支持是否覆盖已存在文件（如 Mapper、ServiceImpl 等）
- **标准结构**：默认生成符合 Spring Boot 项目习惯的包和模块结构

---

## 生成代码结构

以项目名为 `demo-project`，基础包为 `com.g2rain.demo`，并启用多模块结构为例，典型生成结果如下：

```text
demo-project/
├── demo-project-api/
│   └── src/main/java/com/g2rain/demo/
│       ├── api/          # API 接口（${Entity}Api）
│       ├── dto/          # 查询 DTO（${Entity}SelectDto）等
│       └── vo/           # 视图对象（${Entity}Vo）
├── demo-project-biz/
│   └── src/main/java/com.g2rain.demo/
│       ├── controller/   # 控制器（${Entity}Controller）
│       ├── service/      # 业务接口（${Entity}Service）
│       │   └── impl/     # 业务实现（${Entity}ServiceImpl）
│       ├── dao/          # DAO 接口（${Entity}Dao）
│       │   └── po/       # 持久化对象（${Entity}Po）
│       └── converter/    # PO/DTO/VO 转换器（${Entity}Converter）
└── demo-project-biz/
    └── src/main/resources/mybatis/mapper/   # MyBatis Mapper XML（${Entity}Mapper.xml）
```

> 说明：实际模块名和路径由 `FoundryConfig` 和 `TemplatePaths` 决定，可根据项目结构进行调整。

---

## 快速开始

### 1. 在项目中引入插件

在业务项目（建议是父工程）`pom.xml` 中添加：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.g2rain</groupId>
            <artifactId>g2rain-generator-maven-plugin</artifactId>
            <version>1.0.1</version>
        </plugin>
    </plugins>
</build>
```

> 若希望使用简写命令 `mvn g2rain:generate`，请在 `~/.m2/settings.xml` 中配置 `pluginGroup` 为 `com.g2rain`，否则可使用完整坐标方式调用。

### 2. 准备配置文件（推荐）

在项目根目录创建 `codegen.properties`（或自定义文件名，通过 `-Dconfig.file` 指定），可以参考示例文件 `src/main/resources/codegen.properties.example`：

```properties
###########################################################################
# 1. 数据库连接配置（必填）
###########################################################################

# 数据库连接 URL
database.url=jdbc:mysql://localhost:3306/my_database?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# JDBC 驱动类名
database.driver=com.mysql.cj.jdbc.Driver

# 数据库用户名、密码
database.username=root
database.password=your_password

###########################################################################
# 2. 项目基础信息配置（必填）
###########################################################################

# Java 基础包名
project.basePackage=com.example.demo

###########################################################################
# 3. 表生成配置（必填）
###########################################################################

# 要生成代码的表名（支持多个表，用逗号分隔）
database.tables=user,order_info

# 是否允许覆盖已有文件（true/false，默认 false）
tables.overwrite=false
```

### 3. 执行代码生成

在包含 `pom.xml` 的目录执行（注意该插件 `requiresProject = false`，但通常仍在项目根目录执行）：

```bash
# 使用默认配置文件名 codegen.properties
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.1:generate 

# 使用默认配置文件名 codegen.properties，生成指定的表的接口
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.1:generate -Ddatabase.tables=表名

# 或者使用自定义配置文件路径
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.1:generate \
  -Dconfig.file=/path/to/your-codegen.properties
```

### 4. 命令行参数直接配置（无需配置文件）

所有关键参数都可以通过命令行传入（优先级高于配置文件）：

```bash
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.1:generate \
  -Dproject.basePackage=com.example.demo \
  -Ddatabase.url=jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC \
  -Ddatabase.driver=com.mysql.cj.jdbc.Driver \
  -Ddatabase.username=root \
  -Ddatabase.password=123456 \
  -Ddatabase.tables=user,order_info \
  -Dtables.overwrite=true
```

### 5. 交互式模式（可选）

当既没有传入完整命令行参数，又未正确指定配置文件时，插件会进入交互式模式，通过控制台引导输入：

- **Base Package**：如 `com.example.demo`
- **Database URL**：如 `jdbc:mysql://localhost:3306/my_database?...`
- **Driver / Username / Password**
- **Tables**：如 `user,order_info`
- **Overwrite**：是否覆盖已有文件（Y/N）

---

## 配置项说明

### 配置文件键（`codegen.properties`）

| 键名                     | 说明                         | 示例                                                         |
|--------------------------|------------------------------|--------------------------------------------------------------|
| `project.basePackage`    | Java 基础包名（必填）        | `com.example.demo`                                           |
| `database.url`           | 数据库连接 URL（必填）       | `jdbc:mysql://localhost:3306/my_database?useSSL=false...`    |
| `database.driver`        | JDBC 驱动类（必填）          | `com.mysql.cj.jdbc.Driver`                                   |
| `database.username`      | 数据库用户名（必填）         | `root`                                                       |
| `database.password`      | 数据库密码（可选）           | `your_password`                                              |
| `database.tables`        | 要生成代码的表名（必填）     | `user,order_info,product`                                    |
| `tables.overwrite`       | 是否覆盖已有文件（可选）     | `true` / `false`                                             |

> 注意：`database.password` 可以为空，部分数据库支持无密码访问；`tables.overwrite` 未配置时默认 **false**。

### Maven 命令行参数（与配置文件键一一对应）

| 参数名                   | 说明                         | 映射到配置键              |
|--------------------------|------------------------------|---------------------------|
| `-Dproject.basePackage`  | Java 基础包名                | `project.basePackage`     |
| `-Ddatabase.url`         | 数据库连接 URL               | `database.url`            |
| `-Ddatabase.driver`      | JDBC 驱动类                  | `database.driver`         |
| `-Ddatabase.username`    | 数据库用户名                 | `database.username`       |
| `-Ddatabase.password`    | 数据库密码                   | `database.password`       |
| `-Ddatabase.tables`      | 要生成代码的表名             | `database.tables`         |
| `-Dtables.overwrite`     | 是否覆盖已有文件             | `tables.overwrite`        |
| `-Dconfig.file`          | 配置文件路径                 | `codegen.properties` 文件 |

参数优先级：**命令行参数 > 配置文件 > 交互式输入**。

---

## 生成内容说明

插件内部通过 `FoundryGenerator` + `TemplatePaths` 组合 MyBatis Generator 与 FreeMarker 模板，典型生成内容如下（以表 `user` 为例）：

- **PO（持久化对象）**
  - 包路径：`{basePackage}.dao.po`
  - 文件：`UserPo.java`
  - 继承：`com.g2rain.common.model.BasePo`

- **DAO（数据访问层）**
  - 包路径：`{basePackage}.dao`
  - 文件：`UserDao.java`

- **Mapper XML（MyBatis 映射）**
  - 目录：`{projectName}-biz/src/main/resources/mybatis/mapper/`
  - 文件：`UserMapper.xml`

- **Service / ServiceImpl（业务层）**
  - 接口包：`{basePackage}.service`，文件：`UserService.java`
  - 实现包：`{basePackage}.service.impl`，文件：`UserServiceImpl.java`

- **DTO / SelectDto / VO**
  - DTO 包：`{basePackage}.dto`，文件：`UserDto.java`
  - SelectDto 包：`{basePackage}.dto`，文件：`UserSelectDto.java`
  - VO 包：`{basePackage}.vo`，文件：`UserVo.java`

- **API 接口**
  - 包路径：`{basePackage}.api`
  - 文件：`UserApi.java`
  - 典型方法：`selectList`、`selectPage`（返回 `Result`、`PageData`）
  - 说明：`*-api` 模块只暴露查询接口，供其它项目直接依赖使用；若需要新增/更新/删除能力，建议通过异步事件或各自服务完成功能，避免跨项目的写操作耦合。

- **Controller（Rest 控制器）**
  - 包路径：`{basePackage}.controller`
  - 文件：`UserController.java`
  - 默认请求前缀：`/@{tableName}`，实现上述 API 接口，并提供 `save`、`delete` 等基础 CRUD 能力

- **Converter（类型转换器）**
  - 包路径：`{basePackage}.converter`
  - 文件：`UserConverter.java`
  - 用于 PO / DTO / VO 之间转换（通常结合 MapStruct 使用）

此外，还支持生成启动类及 `application.yml` 等配置（位于 `*-startup` 模块，具体由 `TemplatePaths.APPLICATION/APP_YML/APP_DEV_YML` 定义），默认存在时不覆盖。

---

## 模板自定义

所有模板位于 `src/main/resources/templates/`：

- `po.ftl`：持久化对象模板
- `dao.ftl`：DAO 接口模板
- `mapper.ftl`：MyBatis 映射 XML 模板
- `dto.ftl`：业务 DTO 模板
- `selectDto.ftl`：查询条件 DTO 模板
- `vo.ftl`：视图对象模板
- `service.ftl`：Service 接口模板
- `serviceImpl.ftl`：Service 实现模板
- `controller.ftl`：REST 控制器模板
- `api.ftl`：API 接口模板
- `application.ftl`：Spring Boot 启动类模板
- `application.yml.ftl` / `application-dev.yml.ftl`：Spring Boot 配置文件模板

> 修改模板后重新执行生成，即可基于自定义风格产出代码。对于开启 `skipIfExists=true` 的模板（如启动类和配置文件），已存在文件不会被覆盖。

---

## 依赖要求

### 环境要求

- **JDK**：21+
- **Maven**：3.6+
- **数据库**：MySQL 8.0+（或其他支持的 JDBC 数据库）

### 典型业务项目依赖（示例）

下面是业务项目中常见的相关依赖（插件本身只在构建期运行，这些依赖用于运行生成后的代码）：

```xml
<dependencies>
    <!-- G2rain 通用组件（包含 Result、PageData、异常码等模型） -->
    <dependency>
        <groupId>com.g2rain</groupId>
        <artifactId>g2rain-common</artifactId>
        <version>1.0.1</version>
    </dependency>

    <!-- Spring Web，用于 REST Controller 及 API -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- MyBatis 与 PageHelper，用于持久层与分页 -->
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.13</version>
    </dependency>
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.4.7</version>
    </dependency>

    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.32</version>
    </dependency>

    <!-- （可选）MapStruct 等对象转换框架，可配合 Converter 模板使用 -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
</dependencies>
```

---

## 常见问题（FAQ）

### 1. 如何只生成某几个表的代码？

在配置文件或命令行中设置 `database.tables` 即可，例如：

```properties
database.tables=user,order_info,product
```

或：

```bash
mvn com.g2rain:g2rain-generator-maven-plugin:1.0.1:generate \
  -Dproject.basePackage=com.example.demo \
  -Ddatabase.url=... \
  -Ddatabase.driver=com.mysql.cj.jdbc.Driver \
  -Ddatabase.username=root \
  -Ddatabase.password=123456 \
  -Ddatabase.tables=user,order_info,product
```

### 2. 如何避免覆盖我已经手工修改过的代码？

- 通过 `tables.overwrite=false`（或 `-Dtables.overwrite=false`）可以控制 **MyBatis Generator / 部分模板** 是否覆盖已有文件；
- 对于标记为 `skipIfExists=true` 的模板（如启动类、`application.yml` 等），若文件已存在且非空，生成时会自动跳过。

### 3. 提示必填参数缺失或数据库连接失败？

- 确认以下必填配置是否完整：`project.basePackage`、`database.url`、`database.driver`、`database.username`、`database.tables`；
- 检查数据库地址、端口、库名是否正确，账号是否有读取表结构的权限；
- 若通过配置文件加载，确认 `-Dconfig.file` 指向的文件存在且可读。

### 4. 生成的包名/模块结构不符合我的项目？

- 可调整 `project.basePackage` 以控制包前缀；
- 可根据自身项目结构修改 `TemplatePaths` 或相关模板（如模块后缀 `-api`、`-biz` 等），然后重新打包插件使用。

---

## 项目维护

### 版本历史

- **v1.0.0**：初始版本，支持基于数据库表生成完整 CRUD 分层代码
- **v1.0.1**：修订版本，修复Mybatis Mapper时间条件查询BUG

## 🤝 贡献指南

我们欢迎所有形式的贡献！

### 贡献流程

1. **Fork** 本仓库
2. **创建特性分支**：`git checkout -b feature/your-feature-name`
3. **提交更改**：`git commit -m "Add some feature"`
4. **推送分支**：`git push origin feature/your-feature-name`
5. **提交Pull Request**

### 代码贡献要求

- 遵循Google Java代码规范
- 添加适当的单元测试
- 更新相关文档
- 确保所有测试通过
- 代码覆盖率不低于80%

## 📄 许可证

本项目基于 [Apache 2.0许可证](LICENSE) 开源。

## 📞 联系我们

- **Issues**: [GitHub Issues](https://github.com/g2rain/g2rain/issues)
- **讨论**: [GitHub Discussions](https://github.com/g2rain/g2rain/discussions)
- **邮箱**: g2rain_developer@163.com

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者们！

---

⭐ 如果这个项目对您有帮助，请给我们一个Star！