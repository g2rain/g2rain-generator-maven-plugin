package com.g2rain.generator.config;


import lombok.Getter;
import lombok.Setter;

/**
 * 基础生成器配置类，用于封装代码生成器的通用配置信息。
 *
 * <p>该类主要保存项目名和基础包名，供派生的生成器配置类（如 {@link FoundryConfig}）使用。
 * 所有生成器相关配置都可以继承该类，保证统一的基础信息管理。</p>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * GeneratorConfig config = new GeneratorConfig("g2rain-demo", "com.g2rain.demo");
 * String projectName = config.getProjectName(); // "g2rain-demo"
 * String basePackage = config.getBasePackage(); // "com.g2rain.demo"
 * }</pre>
 *
 * @author alpha
 * @since 2025/10/28
 */
@Setter
@Getter
public class GeneratorConfig {

    /**
     * 项目名称，例如 "g2rain-demo"，用于生成目录或模块名
     */
    private String projectName;

    /**
     * 基础包名，例如 "com.g2rain.demo"，用于生成类的包名
     */
    private String basePackage;

    /**
     * 构造函数，初始化项目名和基础包名。
     *
     * @param projectName 项目名称
     * @param basePackage 基础包名
     */
    public GeneratorConfig(String projectName, String basePackage) {
        this.projectName = projectName; // 保存项目名
        this.basePackage = basePackage; // 保存基础包名
    }
}
