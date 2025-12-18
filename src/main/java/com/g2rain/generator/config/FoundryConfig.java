package com.g2rain.generator.config;


import com.g2rain.generator.enums.TemplatePaths;
import com.g2rain.generator.utils.Constants;
import com.g2rain.generator.utils.Strings;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Foundry 配置类，用于管理代码生成器（Foundry）所需的项目与数据库配置信息。
 *
 * <p>该类封装了项目基础信息、数据库连接信息、生成选项（如 stepIn）以及表相关配置，
 * 并提供了一系列方法将配置转换为 MyBatis Generator 使用的各类 Configuration 对象。</p>
 *
 * <p>主要功能包括：
 * <ul>
 *     <li>解析 JDBC URL，提取 host、port、database</li>
 *     <li>提供各类包名获取方法（API、Controller、Service、Dao 等）</li>
 *     <li>生成 MyBatis Generator 所需的 TableConfiguration、JDBCConnectionConfiguration、JavaModelGeneratorConfiguration</li>
 * </ul>
 * </p>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * FoundryConfig config = new FoundryConfig(
 *     "g2rain-demo",
 *     "com.g2rain.demo",
 *     "jdbc:mysql://localhost:3306/test?useSSL=false",
 *     "com.mysql.cj.jdbc.Driver",
 *     "root",
 *     "password"
 * );
 *
 * // 设置生成选项
 * config.setStepIn(false);
 * config.setTables("user,product");
 *
 * // 获取 PO 包名
 * String poPackage = config.getPoPackage();
 * // 返回: "com.g2rain.demo.dao.po"
 *
 * // 生成 TableConfiguration
 * TableConfiguration tableConfig = config.toTableConfiguration(context, "user");
 *
 * // 生成 JDBC 配置
 * JDBCConnectionConfiguration jdbcConfig = config.toJDBCConnectionConfiguration();
 * }</pre>
 *
 * <p>注意：
 * <ul>
 *     <li>如果 URL 为空或解析失败，将使用默认的 {@link Constants#DB_HOST}、{@link Constants#DB_PORT}、{@link Constants#DB_NAME}</li>
 *     <li>toJavaModelGeneratorConfiguration 方法会自动创建 PO 文件输出目录</li>
 * </ul>
 * </p>
 *
 * @author alpha
 * @since 2025/10/28
 */
@Getter
public class FoundryConfig extends GeneratorConfig {

    /**
     * JDBC 连接 URL
     */
    private final String url;

    /**
     * 数据库驱动类名
     */
    private final String driver;

    /**
     * 数据库用户名
     */
    private final String username;

    /**
     * 数据库密码
     */
    private final String password;

    /**
     * 是否在已有模块内部生成，true 表示内部生成，不加项目名前缀
     */
    @Setter
    private boolean stepIn;

    /**
     * 表名列表，逗号分隔
     */
    @Setter
    private String tables;

    /**
     * 数据库表是否覆盖
     */
    @Setter
    private boolean overwrite;

    /**
     * 数据库主机
     */
    private final String host;

    /**
     * 数据库端口
     */
    private final String port;

    /**
     * 数据库名称
     */
    private final String database;

    /**
     * 构造 Foundry 配置对象。
     *
     * <p>根据提供的 JDBC URL 解析 host、port、database，如果解析失败则使用默认值。</p>
     *
     * @param projectName 项目名称
     * @param basePackage 基础包名
     * @param url         JDBC URL，例如 "jdbc:mysql://localhost:3306/test?useSSL=false"
     * @param driver      数据库驱动类名
     * @param username    数据库用户名
     * @param password    数据库密码
     */
    public FoundryConfig(String projectName, String basePackage, String url, String driver, String username, String password) {
        super(projectName, basePackage); // 调用父类构造函数，初始化项目名和基础包名

        // 保存数据库连接信息
        this.url = url;
        this.driver = driver;
        this.username = username;
        this.password = password;

        // 如果 url 为空或空字符串，则使用默认常量
        if (Strings.isBlank(url)) {
            this.host = Constants.DB_HOST;     // 默认主机 localhost
            this.port = Constants.DB_PORT;     // 默认端口 3306
            this.database = Constants.DB_NAME; // 默认数据库名 test
            return; // 直接返回，后续解析 URI 的逻辑不执行
        }

        // 初始化本地变量为默认值，便于后续解析失败时回退
        String h = Constants.DB_HOST;
        String p = Constants.DB_PORT;
        String n = Constants.DB_NAME;

        try {
            // 去掉协议部分（例如 "jdbc:mysql:"），只保留后续 URI 部分
            URI uri = new URI(url.substring(url.indexOf(":") + 1));

            // 提取主机，如果解析失败使用默认值
            h = Objects.toString(uri.getHost(), Constants.DB_HOST);

            // 提取端口，如果未指定端口则使用默认值
            int uriPort = uri.getPort();
            p = uriPort == -1 ? Constants.DB_PORT : Integer.toString(uriPort);

            // 提取数据库名，从 URI 路径中获取
            String path = uri.getPath();
            // 如果路径为空或长度小于等于 1，则使用默认数据库名，否则去掉前导斜杠
            n = (Objects.isNull(path) || path.length() <= 1) ? Constants.DB_NAME : path.substring(1);

            // 去掉 URL 参数，例如 "?useSSL=false"，只保留纯数据库名
            int paramIndex = n.indexOf("?");
            if (paramIndex != -1) {
                n = n.substring(0, paramIndex);
            }
        } catch (Exception ignored) {
            // URI 解析失败时忽略异常，使用默认值
        }

        // 将解析结果赋值给实例变量
        this.host = h;
        this.port = p;
        this.database = n;
    }

    /**
     * 将数据库表名转换为 MyBatis Generator 的 {@link TableConfiguration} 对象。
     *
     * <p>该方法用于配置生成的实体类（PO），自动将表名转换为 Java 驼峰命名的类名，并关闭
     * 不必要的示例语句（deleteByExample、countByExample、updateByExample）。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * TableConfiguration tableConfig = config.toTableConfiguration(context, "user_info");
     * // domainObjectName 会被设置为 "UserInfoPo"
     * tableConfig.getTableName(); // "user_info"
     * tableConfig.getDomainObjectName(); // "UserInfoPo"
     * tableConfig.isDeleteByExampleStatementEnabled(); // false
     * }</pre>
     *
     * @param context   MyBatis Generator 上下文对象，用于初始化 TableConfiguration
     * @param tableName 数据库表名，例如 "user_info"
     * @return 配置好的 {@link TableConfiguration} 对象，用于生成对应的实体类
     */
    public TableConfiguration toTableConfiguration(Context context, String tableName) {
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setCatalog(this.database);
        tableConfiguration.setSchema(this.database);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByExampleStatementEnabled(false);
        return tableConfiguration;
    }

    /**
     * 将当前数据库连接配置转换为 MyBatis Generator 的 {@link JDBCConnectionConfiguration} 对象。
     *
     * <p>此方法将 FoundryConfig 中的 {@code driver}、{@code url}、{@code username}、
     * {@code password} 直接映射到 JDBCConnectionConfiguration，用于 MyBatis Generator
     * 连接数据库。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * JDBCConnectionConfiguration jdbcConfig = config.toJDBCConnectionConfiguration();
     * jdbcConfig.getConnectionURL(); // 返回配置的 JDBC URL
     * jdbcConfig.getDriverClass();   // 返回数据库驱动类名
     * jdbcConfig.getUserId();        // 返回用户名
     * jdbcConfig.getPassword();      // 返回密码
     * }</pre>
     *
     * @return 配置好的 {@link JDBCConnectionConfiguration} 对象
     */
    public JDBCConnectionConfiguration toJDBCConnectionConfiguration() {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass(this.driver);
        jdbcConnectionConfiguration.setConnectionURL(this.url);
        jdbcConnectionConfiguration.setUserId(this.username);
        jdbcConnectionConfiguration.setPassword(this.password);
        return jdbcConnectionConfiguration;
    }

    /**
     * 生成用于 PO 实体类的 {@link JavaModelGeneratorConfiguration} 配置。
     *
     * <p>该方法会自动创建 PO 文件输出目录，并设置生成的目标包和根类（BasePo）。
     * 适用于 MyBatis Generator 生成 PO 文件。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * JavaModelGeneratorConfiguration modelConfig = config.toJavaModelGeneratorConfiguration();
     * modelConfig.getTargetProject(); // 返回输出目录，例如 /project/g2rain-demo/src/main/java
     * modelConfig.getTargetPackage(); // 返回包名，例如 com.g2rain.demo.dao.po
     * modelConfig.getProperty("rootClass"); // "com.g2rain.common.model.BasePo"
     * }</pre>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *     <li>方法会自动创建 PO 输出目录，如果创建失败会抛出 {@link IOException}</li>
     *     <li>生成的 PO 类会继承 BasePo</li>
     * </ul>
     *
     * @return 配置好的 {@link JavaModelGeneratorConfiguration} 对象
     * @throws IOException 当创建 PO 输出目录失败时抛出
     */
    public JavaModelGeneratorConfiguration toJavaModelGeneratorConfiguration() throws IOException {
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();

        // 创建目录
        Path outputDir = Paths.get(TemplatePaths.PO.getModulePath(stepIn, getProjectName())).resolve(Constants.JAVA_FILE_DIR);
        Files.createDirectories(outputDir);

        javaModelGeneratorConfiguration.setTargetProject(outputDir.toString());
        javaModelGeneratorConfiguration.setTargetPackage(TemplatePaths.PO.getFullPackage(this.getBasePackage()));
        return javaModelGeneratorConfiguration;
    }

    /**
     * 获取 API 层包名
     */
    public String getApiPackage() {
        return TemplatePaths.API.getFullPackage(getBasePackage());
    }

    /**
     * 获取 Controller 层包名
     */
    public String getControllerPackage() {
        return TemplatePaths.CONTROLLER.getFullPackage(getBasePackage());
    }

    /**
     * 获取 Service 层包名
     */
    public String getServicePackage() {
        return TemplatePaths.SERVICE.getFullPackage(getBasePackage());
    }

    /**
     * 获取 ServiceImpl 层包名
     */
    public String getServiceImplPackage() {
        return TemplatePaths.SERVICE_IMPL.getFullPackage(getBasePackage());
    }

    /**
     * 获取 Converter 层包名
     */
    public String getConverterPackage() {
        return TemplatePaths.CONVERTER.getFullPackage(getBasePackage());
    }

    /**
     * 获取 DAO 层包名
     */
    public String getDaoPackage() {
        return TemplatePaths.DAO.getFullPackage(getBasePackage());
    }

    /**
     * 获取 PO 层包名
     */
    public String getPoPackage() {
        return TemplatePaths.PO.getFullPackage(getBasePackage());
    }

    /**
     * 获取 DTO 层包名
     */
    public String getDtoPackage() {
        return TemplatePaths.DTO.getFullPackage(getBasePackage());
    }

    /**
     * 获取 VO 层包名
     */
    public String getVoPackage() {
        return TemplatePaths.VO.getFullPackage(getBasePackage());
    }

    /**
     * 获取代码生成作者信息
     */
    public String getAuthor() {
        return "G2rain Generator";
    }
}
