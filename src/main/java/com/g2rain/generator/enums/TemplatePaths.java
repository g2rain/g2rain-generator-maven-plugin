package com.g2rain.generator.enums;


import com.g2rain.generator.config.FoundryConfig;
import com.g2rain.generator.utils.Constants;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;

import static com.g2rain.generator.utils.Constants.SHARED_STARTUP;

/**
 * 枚举定义了项目代码生成器中各类模板的路径和生成规则。
 *
 * <p>每个枚举实例包含以下信息：
 * <ul>
 *     <li>模块后缀 {@link #moduleSuffix}：代码生成所属模块目录的后缀，如 "-api"、"-biz"</li>
 *     <li>模板文件名 {@link #templateName}：模板文件的名称，例如 "service.ftl"</li>
 *     <li>基础文件目录 {@link #baseFileDir}：代码生成的基础目录，例如 "src/main/java" 或 "src/main/resources"</li>
 *     <li>包后缀 {@link #packageSuffix}：生成文件的包后缀，例如 ".service"</li>
 *     <li>类或文件名后缀 {@link #classSuffix}：生成文件的名称规则，例如 "{0}Service.java"，其中 {0} 表示实体名</li>
 *     <li>路径格式 {@link #pathFormat}：生成路径的格式字符串，例如 "{0}/{1}/{2}/{3}"</li>
 *     <li>文件存在时是否跳过 {@link #skipIfExists}：如果为 true，存在同名文件时跳过生成</li>
 * </ul>
 * </p>
 *
 * <p>通过该枚举，可以方便地获取：
 * <ul>
 *     <li>模块路径 {@link #getModulePath(boolean, String)}</li>
 *     <li>完整包名 {@link #getFullPackage(String)}</li>
 *     <li>生成文件的完整输出路径 {@link #getOutputPath(FoundryConfig, String)}</li>
 * </ul>
 * </p>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * // 创建 Foundry 配置对象
 * FoundryConfig config = new FoundryConfig();
 * config.setProjectName("g2rain-demo");
 * config.setBasePackage("com.g2rain.demo");
 * config.setStepIn(false);
 *
 * // 获取 SERVICE 模板的模块路径
 * String modulePath = TemplatePaths.SERVICE.getModulePath(config.isStepIn(), config.getProjectName());
 * // 返回: "g2rain-demo/g2rain-demo-biz"
 *
 * // 获取完整包名
 * String fullPackage = TemplatePaths.SERVICE.getFullPackage(config.getBasePackage());
 * // 返回: "com.g2rain.demo.service"
 *
 * // 获取输出文件路径
 * Path outputPath = TemplatePaths.SERVICE.getOutputPath(config, "User");
 * // 返回:
 * // g2rain-demo/g2rain-demo-biz/src/main/java/com/g2rain/demo/service/UserService.java
 * }</pre>
 *
 * <p>使用此枚举可以保证代码生成器在不同模块、不同模板下的一致性和规范化路径生成。</p>
 *
 * <p>注意：
 * <ul>
 *     <li>枚举中的路径和包后缀应根据项目实际结构调整</li>
 *     <li>模板文件必须存在于指定的 baseFileDir 或 resources 目录下</li>
 * </ul>
 * </p>
 *
 * @author alpha
 * @since 2025/10/28
 */
@Getter
public enum TemplatePaths {

    /**
     * API 接口模板
     * <p>
     * 功能：生成 REST API 接口类
     * 模板文件：api.ftl
     * 模块后缀："-api" -> 生成路径示例：projectName-api
     * 基础目录：Constants.JAVA_FILE_DIR (通常为 src/main/java)
     * 包后缀：".api" -> 完整包名 = basePackage + ".api"
     * 类名模板："{0}Api.java" -> {0} 替换为实体名，例如 User -> UserApi.java
     * 路径格式：Constants.PATH_FORMAT = "{0}/{1}/{2}/{3}" -> 替换 modulePath/baseFileDir/packagePath/fileName
     * skipIfExists：false -> 文件已存在仍会覆盖
     */
    API("-api", "api.ftl", Constants.JAVA_FILE_DIR, ".api", "{0}Api.java", Constants.PATH_FORMAT, false),

    /**
     * 查询 DTO 模板
     * <p>
     * 功能：生成接口查询参数对象
     * 模板文件：selectDto.ftl
     * 模块后缀："-api"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".dto"
     * 类名模板："{0}SelectDto.java" -> User -> UserSelectDto.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    SELECT_DTO("-api", "selectDto.ftl", Constants.JAVA_FILE_DIR, ".dto", "{0}SelectDto.java", Constants.PATH_FORMAT, false),

    /**
     * VO 模板
     * <p>
     * 功能：生成接口返回对象
     * 模板文件：vo.ftl
     * 模块后缀："-api"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".vo"
     * 类名模板："{0}Vo.java" -> User -> UserVo.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    VO("-api", "vo.ftl", Constants.JAVA_FILE_DIR, ".vo", "{0}Vo.java", Constants.PATH_FORMAT, false),

    /**
     * DTO 模板
     * <p>
     * 功能：业务层数据传输对象
     * 模板文件：dto.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".dto"
     * 类名模板："{0}Dto.java" -> User -> UserDto.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    DTO("-biz", "dto.ftl", Constants.JAVA_FILE_DIR, ".dto", "{0}Dto.java", Constants.PATH_FORMAT, false),

    /**
     * DAO 模板
     * <p>
     * 功能：生成 MyBatis DAO 接口
     * 模板文件：dao.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".dao"
     * 类名模板："{0}Dao.java" -> User -> UserDao.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    DAO("-biz", "dao.ftl", Constants.JAVA_FILE_DIR, ".dao", "{0}Dao.java", Constants.PATH_FORMAT, false),

    /**
     * PO 模板
     * <p>
     * 功能：生成持久化对象类
     * 模板文件：po.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".dao.po"
     * 类名模板："{0}Po.java" -> User -> UserPo.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    PO("-biz", "po.ftl", Constants.JAVA_FILE_DIR, ".dao.po", "{0}Po.java", Constants.PATH_FORMAT, false),

    /**
     * SERVICE 模板
     * <p>
     * 功能：生成业务接口类
     * 模板文件：service.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".service"
     * 类名模板："{0}Service.java" -> User -> UserService.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    SERVICE("-biz", "service.ftl", Constants.JAVA_FILE_DIR, ".service", "{0}Service.java", Constants.PATH_FORMAT, false),

    /**
     * SERVICE_IMPL 模板
     * <p>
     * 功能：生成业务实现类
     * 模板文件：serviceImpl.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".service.impl"
     * 类名模板："{0}ServiceImpl.java" -> User -> UserServiceImpl.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    SERVICE_IMPL("-biz", "serviceImpl.ftl", Constants.JAVA_FILE_DIR, ".service.impl", "{0}ServiceImpl.java", Constants.PATH_FORMAT, false),

    /**
     * CONTROLLER 模板
     * <p>
     * 功能：生成 REST 控制器类
     * 模板文件：controller.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".controller"
     * 类名模板："{0}Controller.java" -> User -> UserController.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    CONTROLLER("-biz", "controller.ftl", Constants.JAVA_FILE_DIR, ".controller", "{0}Controller.java", Constants.PATH_FORMAT, false),

    /**
     * CONVERTER 模板
     * <p>
     * 功能：生成 DTO/VO 与 PO 转换器类
     * 模板文件：converter.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.JAVA_FILE_DIR
     * 包后缀：".converter"
     * 类名模板："{0}Converter.java" -> User -> UserConverter.java
     * 路径格式：Constants.PATH_FORMAT
     * skipIfExists：false
     */
    CONVERTER("-biz", "converter.ftl", Constants.JAVA_FILE_DIR, ".converter", "{0}Converter.java", Constants.PATH_FORMAT, false),

    /**
     * MAPPER 模板
     * <p>
     * 功能：生成 MyBatis XML 映射文件
     * 模板文件：mapper.ftl
     * 模块后缀："-biz"
     * 基础目录：Constants.MAPPER_PACKAGE
     * 包后缀："" (不使用包路径)
     * 类名模板："{0}Mapper.xml" -> User -> UserMapper.xml
     * 路径格式："{0}/{1}/{3}" -> 使用 modulePath、baseFileDir、文件名，不使用 packagePath
     * skipIfExists：false
     */
    MAPPER("-biz", "mapper.ftl", Constants.MAPPER_PACKAGE, "", "{0}Mapper.xml", Constants.PATH_FORMAT_WITHOUT_PACKAGE, false),

    /**
     * application.yml 配置模板
     * <p>
     * 功能：生成 Spring Boot 配置文件
     * 模板文件：application.yml.ftl
     * 模块后缀："-startup"
     * 基础目录：Constants.RESOURCES_FILE_DIR
     * 包后缀："" (根包)
     * 类名模板："application.yml"
     * 路径格式："{0}/{1}/{3}" -> 使用 modulePath、baseFileDir、文件名
     * skipIfExists：true -> 文件存在则不覆盖
     */
    APP_YML(SHARED_STARTUP, "application.yml.ftl", Constants.RESOURCES_FILE_DIR, "", "application.yml", Constants.PATH_FORMAT_WITHOUT_PACKAGE, true),

    /**
     * application-dev.yml 配置模板
     * <p>
     * 功能：生成 Spring Boot 配置文件
     * 模板文件：application-dev.yml.ftl
     * 模块后缀："-startup"
     * 基础目录：Constants.RESOURCES_FILE_DIR
     * 包后缀："" (根包)
     * 类名模板："application-dev.yml"
     * 路径格式："{0}/{1}/{3}" -> 使用 modulePath、baseFileDir、文件名
     * skipIfExists：true -> 文件存在则不覆盖
     */
    APP_DEV_YML(SHARED_STARTUP, "application-dev.yml.ftl", Constants.RESOURCES_FILE_DIR, "", "application-dev.yml", Constants.PATH_FORMAT_WITHOUT_PACKAGE, true);

    /**
     * 模块后缀，用于构建模块目录。
     * 例如："-api"、"-biz"、"-startup"。
     * 最终模块路径 = 项目名 + moduleSuffix
     */
    private final String moduleSuffix;

    /**
     * 模板文件名。
     * 对应 resources/templates 下的 Freemarker 模板文件，如 "api.ftl"、"po.ftl" 等。
     */
    private final String templateName;

    /**
     * 基础文件目录。
     * 例如 Java 文件目录常量为 "src/main/java"，资源文件目录为 "src/main/resources"。
     * 用于生成文件的基础路径。
     */
    private final String baseFileDir;

    /**
     * 包名后缀。
     * 生成类所在的包路径后缀，如 ".dao"、".service.impl"。
     * 最终包名 = FoundryConfig.basePackage + packageSuffix
     */
    private final String packageSuffix;

    /**
     * 类名模板后缀。
     * 使用 MessageFormat.format(classSuffix, entityName) 生成最终类名。
     * 例如："{0}Dao.java" → UserDao.java
     */
    private final String classSuffix;

    /**
     * 文件路径格式。
     * 用于构建最终输出路径，例如 "{0}/{1}/{2}/{3}"。
     * 占位符分别对应：
     * {0} = 模块路径
     * {1} = 基础文件目录
     * {2} = 包路径
     * {3} = 文件名
     */
    private final String pathFormat;

    /**
     * 是否跳过已存在文件。
     * true：若目标文件已存在且非空，则不生成覆盖
     * false：强制覆盖
     */
    private final boolean skipIfExists;

    /**
     * 构造模板路径枚举实例。
     *
     * @param moduleSuffix  模块路径后缀
     * @param templateName  模板文件名
     * @param baseFileDir   基础目录
     * @param packageSuffix 包后缀
     * @param classSuffix   类或文件名后缀
     * @param pathFormat    生成路径格式
     * @param skipIfExists  文件存在时是否跳过生成
     */
    TemplatePaths(String moduleSuffix, String templateName, String baseFileDir, String packageSuffix, String classSuffix, String pathFormat, boolean skipIfExists) {
        this.moduleSuffix = moduleSuffix;
        this.templateName = templateName;
        this.baseFileDir = baseFileDir;
        this.packageSuffix = packageSuffix;
        this.classSuffix = classSuffix;
        this.pathFormat = pathFormat;
        this.skipIfExists = skipIfExists;
    }

    /**
     * 根据枚举的模块后缀和项目名称，生成模块路径。
     *
     * <p>模块路径用于代码生成时，确定生成文件所属的模块目录。
     * 如果 {@code stepIn} 为 {@code true}，表示在已有模块内部生成，
     * 则不在路径前添加项目名；否则会在路径前加上项目名作为前缀。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * TemplatePaths path = TemplatePaths.SERVICE;
     *
     * // 项目名：g2rain-demo，stepIn = false
     * String modulePath = path.getModulePath(false, "g2rain-demo");
     * // 返回 "g2rain-demo/g2rain-demo-biz"
     *
     * // stepIn = true
     * String modulePathStepIn = path.getModulePath(true, "g2rain-demo");
     * // 返回 "g2rain-demo-biz"
     * }</pre>
     *
     * @param stepIn      是否在已有模块内部生成，true 表示内部生成，不加项目名前缀
     * @param projectName 项目名称，用于路径前缀
     * @return 拼接后的模块路径，用于生成文件所在目录
     */
    public String getModulePath(boolean stepIn, String projectName) {
        return (stepIn ? "" : (projectName + "/")) + (projectName + this.moduleSuffix);
    }

    /**
     * 获取完整包名，用于生成 Java 文件的 package 声明。
     *
     * <p>该方法会将基础包名与枚举中定义的包后缀拼接，形成完整的包名。
     * 例如基础包名为 {@code com.g2rain.demo}，枚举包后缀为 {@code .service}，
     * 则返回 {@code com.g2rain.demo.service}。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * TemplatePaths path = TemplatePaths.SERVICE;
     * String fullPackage = path.getFullPackage("com.g2rain.demo");
     * // 返回 "com.g2rain.demo.service"
     * }</pre>
     *
     * @param basePackage 基础包名（通常来自项目配置）
     * @return 拼接后的完整包名
     */
    public String getFullPackage(String basePackage) {
        return basePackage + this.packageSuffix;
    }

    /**
     * 根据模板路径配置和项目配置信息，生成最终输出文件的完整路径。
     *
     * <p>该方法会综合以下信息生成文件路径：
     * <ul>
     *     <li>项目名（config.getProjectName()）</li>
     *     <li>模块后缀（TemplatePaths 枚举中的 moduleSuffix，例如 -api、-biz）</li>
     *     <li>模板基础目录（baseFileDir，例如 src/main/java 或 src/main/resources）</li>
     *     <li>包名及包后缀（config.getBasePackage() + packageSuffix，例如 com.g2rain.demo.service）</li>
     *     <li>生成类名或文件名规则（classSuffix，例如 {0}Service.java，其中 {0} 为实体名）</li>
     *     <li>路径格式（pathFormat，例如 "{0}/{1}/{2}/{3}"）</li>
     * </ul>
     * </p>
     *
     * <p>方法会将包名中的点号替换为文件分隔符，并对最终路径进行规范化处理。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * FoundryConfig config = new FoundryConfig();
     * config.setProjectName("g2rain-demo");
     * config.setBasePackage("com.g2rain.demo");
     * config.setStepIn(false);
     *
     * // 假设模板为 SERVICE("-biz", "service.ftl", "src/main/java", ".service", "{0}Service.java", Constants.PATH_FORMAT, false)
     * Path outputPath = TemplatePaths.SERVICE.getOutputPath(config, "User");
     * // 生成路径：
     * // g2rain-demo/g2rain-demo-biz/src/main/java/com/g2rain/demo/service/UserService.java
     * }</pre>
     *
     * @param config     Foundry 配置对象，包含项目名、基础包名及 stepIn 标识
     * @param entityName 实体类名，用于替换 classSuffix 中的占位符 {0}
     * @return 输出文件的绝对、规范化路径
     */
    public Path getOutputPath(FoundryConfig config, String entityName) {
        String modulePath = this.getModulePath(config.isStepIn(), config.getProjectName());
        String packagePath = this.getFullPackage(config.getBasePackage()).replace('.', File.separatorChar);
        String fileName = MessageFormat.format(this.classSuffix, entityName);
        String fullPath = MessageFormat.format(this.pathFormat, modulePath, this.baseFileDir, packagePath, fileName);
        return Path.of(fullPath).toAbsolutePath().normalize();
    }
}
