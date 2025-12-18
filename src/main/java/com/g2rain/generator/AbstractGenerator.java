package com.g2rain.generator;


import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.maven.plugin.logging.Log;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;

/**
 * 抽象的代码生成器基类，封装了基于 FreeMarker 模板引擎的通用生成逻辑。
 * <p>
 * 子类应实现 {@link #generate()} 方法以定义具体的生成行为。
 * 该类负责模板的加载、数据模型的渲染以及文件输出等基础能力。
 * </p>
 *
 * <p><b>设计原则：</b>
 * 提供统一的模板处理流程，降低代码生成子类的重复逻辑。
 * </p>
 *
 * <p><b>典型用法：</b>
 * <pre>{@code
 * public class EntityGenerator extends AbstractGenerator {
 *
 *     public EntityGenerator(Log log) {
 *         super(log, "/templates");
 *     }
 *
 *     @Override
 *     protected void generate() throws Exception {
 *         Map<String, Object> dataModel = Map.of("className", "User");
 *         Path output = Paths.get("output/User.java");
 *         processTemplate("entity.ftl", output, dataModel);
 *     }
 * }
 * }</pre>
 * </p>
 *
 * @author alpha
 * @since 2025/10/28
 */
public abstract class AbstractGenerator {

    /**
     * Maven 插件日志对象，用于输出生成过程中的日志。
     */
    protected final Log log;

    /**
     * FreeMarker 配置对象，用于模板加载与渲染。
     */
    protected final Configuration configuration;

    /**
     * 构造函数，初始化 FreeMarker 配置并设置模板加载路径。
     *
     * @param log         Maven 插件日志对象，用于记录日志。
     * @param basePackage 模板所在的基础路径（通常为类路径下的模板目录）。
     */
    protected AbstractGenerator(Log log, String basePackage) {
        this.log = log;
        this.configuration = new Configuration(Configuration.VERSION_2_3_31);
        this.configuration.setClassForTemplateLoading(getClass(), basePackage);
        this.configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    /**
     * 处理并渲染指定的 FreeMarker 模板文件到输出路径。
     * <p>
     * 该方法会自动创建缺失的父目录，并在文件已存在时进行覆盖。
     * </p>
     * <p><b>示例：</b>
     * <pre>{@code
     *  processTemplate("entity.ftl",
     *      Paths.get("src/main/java/com/example/User.java"),
     *      Map.of("className", "User")
     *  );
     * }</pre>
     * </p>
     *
     * @param templatePath 模板路径（相对模板根目录）。
     * @param outputPath   输出文件路径。
     * @param dataModel    模板渲染所需的数据模型。
     * @throws RuntimeException 当模板处理或文件写入失败时抛出。
     */
    @SuppressWarnings("java:S112")
    protected void processTemplate(String templatePath, Path outputPath, Map<String, Object> dataModel) {
        try {
            // 获取模板
            Template template = configuration.getTemplate(templatePath);
            if (Objects.isNull(template)) {
                log.error("Template not found for output file: " + outputPath.toAbsolutePath());
                return;
            }

            // 创建父目录
            Files.createDirectories(outputPath.getParent());

            // 渲染模板到文件
            try (Writer out = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                template.process(dataModel, out);
            }

            log.info("Generated file: " + outputPath.toAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to process template: " + templatePath + " -> " + outputPath.toAbsolutePath(), e);
        }
    }

    /**
     * 抽象生成方法，由具体子类实现生成逻辑。
     * <p>
     * 通常包括模板数据构建、目标文件路径确定、模板渲染等步骤。
     * </p>
     *
     * @throws Exception 子类可根据生成逻辑自行抛出异常。
     */
    @SuppressWarnings("java:S112")
    protected abstract void generate() throws Exception;
}
