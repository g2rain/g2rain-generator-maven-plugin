package com.g2rain.generator.utils;


/**
 * 全局常量定义类，用于统一存放系统中的固定字符串与格式化模板。
 * <p>
 * 本类为工具类，不可实例化。主要提供系统通用常量，如路径格式、日志输出模板、数据库默认配置等。
 * <p>
 * 设计目的：
 * <ul>
 *     <li>集中管理系统中的常量，避免硬编码。</li>
 *     <li>保证代码风格统一与易维护性。</li>
 *     <li>遵循开源项目和企业级项目中常见的常量类规范。</li>
 * </ul>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * String path = MessageFormat.format(Constants.PATH_FORMAT, "com", "g2rain", "demo", "dao");
 * System.out.println(Constants.HORIZONTAL_LINE);
 * System.out.printf(Constants.LOG_FORMAT, "DB Name", Constants.DB_NAME);
 * }</pre>
 *
 * @author alpha
 * @since 2025/10/28
 */
public class Constants {

    /**
     * 私有构造函数，防止实例化。
     * <p>
     * 工具类仅包含静态常量与方法，不应被实例化。
     */
    private Constants() {
        // 禁止实例化
    }

    /**
     * 控制台输出时的分隔线。
     * <p>
     * 用于日志或控制台输出时分隔不同模块内容，提升可读性。
     * <p><b>示例：</b></p>
     * <pre>{@code
     * System.out.println(Constants.HORIZONTAL_LINE);
     * }</pre>
     */
    public static final String HORIZONTAL_LINE = "=========================================";

    /**
     * 通用路径格式模板。
     * <p>
     * 可使用 {@link java.text.MessageFormat#format(String, Object...)} 进行占位符替换。
     * 格式为 {@code {0}/{1}/{2}/{3}}。
     * <p><b>示例：</b></p>
     * <pre>{@code
     * String path = MessageFormat.format(Constants.PATH_FORMAT, "src", "main", "java", "com", "TestPo.java");
     * }</pre>
     */
    public static final String PATH_FORMAT = "{0}/{1}/{2}/{3}";

    /**
     * 路径格式模板 - 不包含包路径。
     * <p>
     * 可使用 {@link java.text.MessageFormat#format(String, Object...)} 进行占位符替换。
     * 格式为 {@code {0}/{1}/{2}/{3}}。
     * <p><b>示例：</b></p>
     * <pre>{@code
     * String path = MessageFormat.format(Constants.PATH_FORMAT, "src", "main", "java", "", "Application.java");
     * }</pre>
     */
    public static final String PATH_FORMAT_WITHOUT_PACKAGE = "{0}/{1}/{3}";

    /**
     * 日志格式模板。
     * <p>
     * 用于统一日志输出格式，第一个参数左对齐15个字符宽度，第二个参数为内容。
     * <p><b>示例：</b></p>
     * <pre>{@code
     * System.out.printf(Constants.LOG_FORMAT + "%n", "DB_HOST", Constants.DB_HOST);
     * }</pre>
     */
    public static final String LOG_FORMAT = "%-15s: %s";

    /**
     * 数据库主机名的默认值。
     * <p>
     * 常用于开发或测试环境的默认数据库连接配置。
     */
    public static final String DB_HOST = "localhost";

    /**
     * 数据库默认端口号。
     * <p>
     * 对应 MySQL 的标准端口 {@code 3306}。
     */
    public static final String DB_PORT = "3306";

    /**
     * 默认数据库名称。
     * <p>
     * 在无特定配置时，使用该数据库进行连接。
     */
    public static final String DB_NAME = "test";

    /**
     * Java 源文件的默认目录路径。
     * <p>
     * 通常用于代码生成器指定 Java 文件输出位置。
     * <p><b>示例：</b></p>
     * <pre>{@code
     * Path javaDir = Paths.get(Constants.JAVA_FILE_DIR);
     * }</pre>
     */
    public static final String JAVA_FILE_DIR = "src/main/java";

    /**
     * 资源文件的默认目录路径。
     * <p>
     * 用于存放配置文件、模板文件或 MyBatis 映射文件等。
     */
    public static final String RESOURCES_FILE_DIR = "src/main/resources";

    /**
     * MyBatis Mapper XML 文件所在路径。
     * <p>
     * 默认路径为 {@code src/main/resources/mybatis/mapper}。
     * <p><b>示例：</b></p>
     * <pre>{@code
     * File mapperDir = new File(Constants.MAPPER_PACKAGE);
     * }</pre>
     */
    public static final String MAPPER_PACKAGE = RESOURCES_FILE_DIR + "/mybatis/mapper";

    public static final String SHARED_STARTUP = "-startup";
}
