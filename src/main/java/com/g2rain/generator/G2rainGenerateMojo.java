package com.g2rain.generator;

import com.g2rain.generator.config.FoundryConfig;
import com.g2rain.generator.generator.FoundryGenerator;
import com.g2rain.generator.utils.Constants;
import com.g2rain.generator.utils.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * 代码生成器Maven插件
 * 用于通过Maven命令调用代码生成器生成CRUD代码
 *
 * @author jagger
 */
@Getter
@Mojo(name = "generate", requiresProject = false)
public class G2rainGenerateMojo extends AbstractMojo {

    /**
     * Maven 对象
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * Java 基础包名（必填）
     */
    @Setter
    @Parameter(property = "project.basePackage")
    private String basePackage;

    /**
     * 数据库连接 URL
     */
    @Setter
    @Parameter(property = "database.url")
    private String url;

    /**
     * 数据库驱动类
     */
    @Setter
    @Parameter(property = "database.driver")
    private String driver;

    /**
     * 数据库用户名
     */
    @Setter
    @Parameter(property = "database.username")
    private String username;

    /**
     * 数据库密码
     */
    @Setter
    @Parameter(property = "database.password")
    private String password;

    /**
     * 待生成的数据库表名（可多表，以逗号分隔）
     */
    @Setter
    @Parameter(property = "database.tables")
    private String tables;

    /**
     * 待生成的数据库表名是否允许覆盖, 默认是不覆盖
     */
    @Setter
    @Parameter(property = "tables.overwrite")
    private Boolean overwrite;

    /**
     * foundry 配置文件路径
     */
    @Setter
    @Parameter(property = "config.file")
    private File configFile;

    /**
     * 控制台输入扫描器，用于交互式参数输入
     */
    private Scanner scanner;

    /**
     * 获取或初始化输入扫描器。
     *
     * @return {@link Scanner} 实例
     */
    private Scanner getScanner() {
        if (Objects.isNull(scanner)) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }

    /**
     * 插件执行主逻辑。
     * <ul>
     *     <li>参数收集与验证；</li>
     *     <li>配置展示；</li>
     *     <li>业务代码生成。</li>
     * </ul>
     *
     * @throws MojoExecutionException 当任意生成过程失败时抛出
     */
    @Override
    @SuppressWarnings("java:S2142")
    public void execute() throws MojoExecutionException {
        if (Objects.isNull(project.getFile()) || !project.getFile().exists()) {
            throw new MojoExecutionException("[ERROR] No valid POM file found in the current directory. Please ensure you are running Maven from the project’s root directory.");
        }

        try {
            prepareFoundryConfig();

            getLog().info("====== Code Generation Configuration =====");
            getLog().info(String.format(Constants.LOG_FORMAT, "Artifact ID", project.getArtifactId()));
            getLog().info(String.format(Constants.LOG_FORMAT, "Base Package", basePackage));
            getLog().info(String.format(Constants.LOG_FORMAT, "Database URL", url));
            getLog().info(String.format(Constants.LOG_FORMAT, "Driver Class", driver));
            getLog().info(String.format(Constants.LOG_FORMAT, "Database User", username));
            getLog().info(String.format(Constants.LOG_FORMAT, "Table Names", tables));
            getLog().info(String.format(Constants.LOG_FORMAT, "Overwrite Files", Boolean.TRUE.equals(this.overwrite)));
            getLog().info(Constants.HORIZONTAL_LINE);
            getLog().info("");

            getLog().info(">>> Starting Code Generation...");
            FoundryConfig config = new FoundryConfig(
                    project.getArtifactId(),
                    basePackage,
                    url,
                    driver,
                    username,
                    password
            );

            config.setStepIn(Boolean.TRUE);
            config.setTables(this.tables);
            config.setOverwrite(Boolean.TRUE.equals(this.getOverwrite()));
            new FoundryGenerator(getLog(), config).generate();
            getLog().info(">>> Code Generation Completed.");
        } catch (Exception e) {
            getLog().info("  G2Rain Generator - Execution failed: " + e.getMessage(), e);
            throw new MojoExecutionException("Generation failed", e);
        }
    }

    /**
     * 准备代码生成阶段的配置参数。
     * <p>
     * 支持多种配置来源，按优先级处理：
     * <ol>
     *     <li><b>配置文件</b> - 优先从 codegen.properties 加载配置</li>
     *     <li><b>非交互式环境</b> - 验证命令行参数完整性</li>
     *     <li><b>交互式环境</b> - 通过控制台提示用户输入</li>
     * </ol>
     *
     * <p><b>执行流程：</b></p>
     * <ol>
     *     <li>尝试加载配置文件（如果配置了 configFile）</li>
     *     <li>如果配置文件加载成功或处于非交互式环境：验证参数完整性</li>
     *     <li>否则：进入交互式参数输入流程</li>
     * </ol>
     *
     * @throws MojoExecutionException 当必填参数缺失时抛出
     * @throws IOException            当配置文件读取失败时抛出
     */
    private void prepareFoundryConfig() throws MojoExecutionException, IOException {
        if (loadFoundryConfigFile() || Objects.isNull(System.console())) {
            // 加载配置文件, 如果设置文件路径, 需要校验参数; 非交互式也校验参数
            validateFoundryConfig();
        } else {
            // 交互式获取参数
            promptForFoundryParameters();
        }
    }

    /**
     * 加载 Foundry 阶段的配置文件。
     * <p>
     * 从指定的配置文件中读取参数值，仅当命令行参数未设置时才使用配置文件中的值。
     * 配置文件的优先级低于命令行参数，高于交互式输入。
     *
     * <p><b>配置文件参数映射：</b></p>
     * <ul>
     *     <li>{@code archetype.artifactId} → {@code projectName}</li>
     *     <li>{@code archetype.package} → {@code basePackage}</li>
     *     <li>{@code database.url} → {@code url}</li>
     *     <li>{@code database.driver} → {@code driver}</li>
     *     <li>{@code database.username} → {@code username}</li>
     *     <li>{@code database.password} → {@code password}</li>
     *     <li>{@code database.tables} → {@code tables}</li>
     *     <li>{@code database.overwrite} → {@code overwrite}</li>
     * </ul>
     *
     * @return {@code true} 如果配置文件存在且成功加载，{@code false} 如果未配置配置文件路径，
     * 如果配置文件不存在返回 {@code true} 但跳过加载
     * @throws IOException 当配置文件读取失败时抛出
     */
    private boolean loadFoundryConfigFile() throws IOException {
        if (Objects.isNull(configFile) || !configFile.exists() || !configFile.isFile()) {
            return false;
        }

        getLog().info("Load config: " + configFile.getAbsolutePath());

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);

            if (Strings.isBlank(this.basePackage)) {
                this.basePackage = props.getProperty("project.basePackage");
            }

            if (Strings.isBlank(this.url)) {
                this.url = props.getProperty("database.url");
            }

            if (Strings.isBlank(this.driver)) {
                this.driver = props.getProperty("database.driver");
            }

            if (Strings.isBlank(this.username)) {
                this.username = props.getProperty("database.username");
            }

            if (Strings.isBlank(this.password)) {
                this.password = props.getProperty("database.password");
            }

            if (Strings.isBlank(this.tables)) {
                this.tables = props.getProperty("database.tables");
            }

            if (Objects.isNull(this.overwrite)) {
                String ow = props.getProperty("tables.overwrite");
                this.overwrite = "true".equalsIgnoreCase(ow);
            }

            return true;
        }
    }

    /**
     * 验证代码生成所需的配置参数。
     * <p>
     * 检查以下必填参数是否已配置：
     * <ul>
     *     <li>Artifact ID - 项目名称标识符</li>
     *     <li>Base Package - Java 基础包名</li>
     *     <li>Database URL - 数据库连接地址</li>
     *     <li>Database Driver - 数据库驱动类</li>
     *     <li>Database Username - 数据库用户名</li>
     *     <li>Database Tables - 待生成的表名列表</li>
     * </ul>
     *
     * <p><b>注意：</b>数据库密码为可选参数，某些数据库可能不需要密码。</p>
     *
     * @throws MojoExecutionException 当任何必填参数未配置时抛出，包含具体的错误信息
     */
    private void validateFoundryConfig() throws MojoExecutionException {
        if (Strings.isBlank(this.basePackage)) {
            throw new MojoExecutionException("The base package name is not configured. Please check the configuration file or command-line parameters");
        }

        if (Strings.isBlank(this.url)) {
            throw new MojoExecutionException("The database host address has not been configured. Please check the configuration file or command-line parameters");
        }

        if (Strings.isBlank(this.driver)) {
            throw new MojoExecutionException("The database driver is not configured. Please check the configuration file or command-line parameters");
        }

        if (Strings.isBlank(this.username)) {
            throw new MojoExecutionException("The database username has not been configured. Please check the configuration file or command-line parameters");
        }

        if (Strings.isBlank(this.tables)) {
            throw new MojoExecutionException("The database tables has not been configured. Please check the configuration file or command-line parameters");
        }
    }

    /**
     * 控制台交互式收集数据库及代码生成相关参数。
     * 若参数均已提供，则直接返回。
     */
    private void promptForFoundryParameters() {
        if (Stream.of(basePackage, url, driver, username, password, tables)
                .allMatch(Strings::isNotBlank)) {
            return;
        }

        this.basePackage = getNonBlankInput(
                "Base Package [required]: ",
                this.basePackage
        );
        this.url = getNonBlankInput(
                "Database URL [required]: ",
                this.url
        );
        this.driver = getNonBlankInput(
                "Driver Class [required]: ",
                this.driver
        );
        this.username = getNonBlankInput(
                "Username [required]: ",
                this.username
        );
        this.password = getOptionalInput(
                "Password [optional]: ",
                this.password,
                null
        );
        this.tables = getNonBlankInput(
                "Table Names [required]: ",
                this.tables
        );
        this.overwrite = getBooleanInput(
                "Overwrite existing files? (y/N, default N): ",
                this.overwrite,
                false
        );
    }

    /**
     * 控制台输入：读取非空字符串。
     * <p>若当前值已存在则直接返回；否则提示用户输入直到非空。</p>
     *
     * @param prompt       输入提示
     * @param currentValue 当前值（可为空）
     * @return 用户输入的非空字符串
     */
    @SuppressWarnings("java:S106")
    private String getNonBlankInput(String prompt, String currentValue) {
        if (Strings.isNotBlank(currentValue)) {
            return currentValue;
        }

        String input = null;
        while (Strings.isBlank(input)) {
            System.out.print(prompt);
            System.out.flush();
            input = getScanner().nextLine().trim();
        }

        return input;
    }

    /**
     * 控制台输入：读取可选字符串，若为空则返回默认值。
     *
     * @param prompt       输入提示
     * @param currentValue 当前值（可为空）
     * @param defaultValue 默认值
     * @return 用户输入值或默认值
     */
    @SuppressWarnings({"java:S106", "SameParameterValue"})
    private String getOptionalInput(String prompt, String currentValue, String defaultValue) {
        if (Strings.isNotBlank(currentValue)) {
            return currentValue;
        }

        System.out.print(prompt);
        System.out.flush();

        String input = getScanner().nextLine().trim();
        return Strings.isBlank(input) ? defaultValue : input;
    }

    /**
     * 控制台输入：读取布尔值，支持多种输入格式。
     * <p>
     * 输入处理规则：
     * <ul>
     *     <li>命令行已设置有效值：直接使用命令行值</li>
     *     <li>空输入（直接回车）：返回默认值</li>
     *     <li>有效输入：返回对应布尔值</li>
     *     <li>无效输入：自动重新提示输入</li>
     * </ul>
     *
     * <p><b>支持的输入格式：</b></p>
     * <ul>
     *     <li>true 值：y, yes, true, 1</li>
     *     <li>false 值：n, no, false, 0</li>
     * </ul>
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * // 命令行已设置，直接使用
     * boolean result1 = getBooleanInput("Overwrite? [y/N]: ", true, false); // returns true
     *
     * // 交互式输入
     * // 用户输入 "y" -> returns true
     * // 用户输入 "no" -> returns false
     * // 用户直接回车 -> returns false (默认值)
     * // 用户输入 "abc" -> 重新提示输入
     * }</pre>
     *
     * @param prompt       输入提示信息，如 "Overwrite files? [y/N]: "
     * @param currentValue 命令行传入的当前值，可为 null
     * @param defaultValue 默认值，当输入为空时使用
     * @return 解析后的布尔值
     */
    @SuppressWarnings({"java:S106", "SameParameterValue"})
    private boolean getBooleanInput(String prompt, Boolean currentValue, boolean defaultValue) {
        // 如果命令行设置了有效值，直接使用
        if (Objects.nonNull(currentValue)) {
            return currentValue;
        }

        while (true) {
            System.out.print(prompt);
            System.out.flush();

            String input = getScanner().nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                return defaultValue;
            }

            switch (input) {
                case "y", "yes", "true", "1" -> {
                    return true;
                }
                case "n", "no", "false", "0" -> {
                    return false;
                }
                default -> {
                    // default 什么都不做，自动继续循环
                }
            }
        }
    }
}