package com.g2rain.generator.generator;


import com.g2rain.generator.AbstractGenerator;
import com.g2rain.generator.config.FoundryConfig;
import com.g2rain.generator.enums.TemplatePaths;
import com.g2rain.generator.model.TableInfo;
import com.g2rain.generator.plugin.TableInfoPlugin;
import com.g2rain.generator.utils.Strings;
import org.apache.maven.plugin.logging.Log;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Foundry 代码生成器，用于根据 {@link FoundryConfig} 配置自动生成 MyBatis PO、DAO、Service、Controller、
 * DTO/VO、API 等层级代码。
 *
 * <p>核心功能：</p>
 * <ol>
 *     <li>根据配置连接数据库，提取指定表的元数据信息</li>
 *     <li>通过 {@link TableInfoPlugin} 插件收集表信息和字段信息</li>
 *     <li>使用 MyBatis Generator 自动生成 PO（实体类）和 Mapper XML</li>
 *     <li>基于 {@link TemplatePaths} 枚举定义的模板，渲染生成 Service、Controller、DTO/VO、API 等文件</li>
 *     <li>可选择性跳过已存在文件（skipIfExists），避免覆盖手动修改的代码</li>
 * </ol>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * FoundryConfig config = new FoundryConfig(
 *     "g2rain-demo",                     // 项目名
 *     "com.g2rain.demo",                 // 基础包名
 *     "jdbc:mysql://localhost:3306/demo",// 数据库URL
 *     "com.mysql.cj.jdbc.Driver",        // 驱动
 *     "root",                             // 用户名
 *     "123456"                            // 密码
 * );
 * config.setTables("user,order");         // 指定生成的表
 * FoundryGenerator generator = new FoundryGenerator(log, config);
 * generator.generate();                   // 执行代码生成
 * }</pre>
 *
 * <p>核心流程：</p>
 * <ol>
 *     <li>构造函数初始化：
 *         <ul>
 *             <li>创建 MyBatis Generator 配置 {@link Configuration} 和上下文 {@link Context}</li>
 *             <li>设置 JDBC 连接信息、PO 生成策略、Java 类型解析配置</li>
 *             <li>添加 {@link TableInfoPlugin} 插件，用于生成表结构元数据</li>
 *         </ul>
 *     </li>
 *     <li>{@link #generate()} 方法：
 *         <ul>
 *             <li>按逗号分割表名，去重和修剪空格</li>
 *             <li>将每个表名转换为 {@link org.mybatis.generator.config.TableConfiguration} 并加入上下文</li>
 *             <li>调用 MyBatis Generator 执行 PO 和 Mapper XML 生成</li>
 *             <li>收集插件生成的 {@link TableInfo}，渲染其它层级模板：
 *                 <ul>
 *                     <li>Service、Controller、DTO/VO、API 等</li>
 *                     <li>通过 {@link TemplatePaths#getOutputPath(FoundryConfig, String)} 计算输出路径</li>
 *                     <li>若模板设置 skipIfExists 且文件已存在且非空，则跳过</li>
 *                     <li>使用 {@link AbstractGenerator#processTemplate(String, Path, Map)} 渲染模板</li>
 *                 </ul>
 *             </li>
 *             <li>输出日志，打印每张表生成的 TableInfo 信息</li>
 *         </ul>
 *     </li>
 * </ol>
 *
 * <p>模板文件结构示例（TemplatePaths 枚举对应）：</p>
 * <pre>{@code
 * templates/
 * ├─ api.ftl           -> 输出: {EntityName}Api.java
 * ├─ selectDto.ftl     -> 输出: {EntityName}SelectDto.java
 * ├─ vo.ftl            -> 输出: {EntityName}Vo.java
 * ├─ service.ftl       -> 输出: {EntityName}Service.java
 * ├─ serviceImpl.ftl   -> 输出: {EntityName}ServiceImpl.java
 * ├─ controller.ftl    -> 输出: {EntityName}Controller.java
 * └─ mapper.ftl        -> 输出: {EntityName}Mapper.xml
 * }</pre>
 *
 * <p>生成结果示例（表 user）：</p>
 * <pre>{@code
 * com/g2rain/demo/
 * ├─ api/UserApi.java
 * ├─ dto/UserSelectDto.java
 * ├─ vo/UserVo.java
 * ├─ service/UserService.java
 * ├─ service/impl/UserServiceImpl.java
 * ├─ controller/UserController.java
 * └─ mapper/UserMapper.xml
 * }</pre>
 *
 * <p>注意事项：</p>
 * <ul>
 *     <li>必须在 {@code FoundryConfig#setTables(String)} 中指定表名，否则不会生成任何代码</li>
 *     <li>PO 和 Mapper XML 生成使用 MyBatis Generator，其他层级文件使用 Freemarker 模板渲染</li>
 *     <li>模板渲染时，数据模型包含：
 *         <ul>
 *             <li>config: FoundryConfig 配置</li>
 *             <li>table: TableInfo 表信息</li>
 *             <li>random: 用于生成随机数等辅助值</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @author alpha
 * @since 2025/10/28
 */
public class FoundryGenerator extends AbstractGenerator {

    /**
     * MyBatis Generator 配置
     */
    private final Configuration config;

    /**
     * MyBatis Generator 默认上下文
     */
    private final Context defaultContext;

    /**
     * Shell 回调，用于覆盖已有文件
     */
    private final DefaultShellCallback callback;

    /**
     * Foundry 配置
     */
    private final FoundryConfig foundryConfig;

    /**
     * 构造函数
     *
     * @param log           日志对象
     * @param foundryConfig Foundry 配置
     * @throws IOException PO 生成目录创建失败时抛出
     */
    public FoundryGenerator(Log log, FoundryConfig foundryConfig) throws IOException {
        super(log, "/templates");

        this.foundryConfig = foundryConfig;
        config = new Configuration();
        defaultContext = new Context(ModelType.FLAT);
        config.addContext(defaultContext);

        // 添加自定义插件以提取表信息
        defaultContext.addPluginConfiguration(TableInfoPlugin.generatePluginConfiguration());
        defaultContext.setId("G2RAIN_GENERATOR");
        defaultContext.setTargetRuntime("MyBatis3");

        // 配置连接信息
        defaultContext.setJdbcConnectionConfiguration(foundryConfig.toJDBCConnectionConfiguration());

        // 配置Po对象生成策略
        defaultContext.setJavaModelGeneratorConfiguration(foundryConfig.toJavaModelGeneratorConfiguration());

        // 配置 Java 类型解析器，启用 JSR310 类型支持
        JavaTypeResolverConfiguration typeResolver = new JavaTypeResolverConfiguration();
        typeResolver.addProperty("useJSR310Types", "true");
        typeResolver.addProperty("forceBigDecimals", "true");
        defaultContext.setJavaTypeResolverConfiguration(typeResolver);

        // Shell 回调，允许覆盖已有文件
        callback = new DefaultShellCallback(foundryConfig.isOverwrite());
    }

    /**
     * 执行代码生成。
     *
     * <p>该方法是 FoundryGenerator 的核心方法，负责将数据库表映射为代码文件，涵盖整个生成流程：</p>
     *
     * <ol>
     *     <li>读取配置的表名：
     *         <ul>
     *             <li>通过 {@code FoundryConfig#getTables()} 获取用户指定的表名列表</li>
     *             <li>若表名为空或仅包含空格，则记录错误日志并直接返回</li>
     *         </ul>
     *     </li>
     *
     *     <li>将表名转化为 MyBatis Generator 的 TableConfiguration 并加入上下文：
     *         <ul>
     *             <li>按逗号分割表名，去除首尾空格，并去重</li>
     *             <li>调用 {@link FoundryConfig#toTableConfiguration(Context, String)} 生成 TableConfiguration</li>
     *             <li>添加到 {@link Context}，供 MyBatis Generator 使用生成 PO 和 Mapper XML</li>
     *         </ul>
     *     </li>
     *
     *     <li>执行 MyBatis Generator：
     *         <ul>
     *             <li>使用 {@link MyBatisGenerator} 根据 {@link Configuration} 生成 PO（实体类）和 Mapper XML</li>
     *             <li>使用 {@link DefaultShellCallback} 覆盖已有文件，保证最新模板生效</li>
     *             <li>捕获 {@link InterruptedException} 并打印日志，同时恢复线程中断状态</li>
     *             <li>收集生成过程中产生的警告信息，逐条记录到日志中</li>
     *         </ul>
     *     </li>
     *
     *     <li>收集表信息（TableInfo）：
     *         <ul>
     *             <li>通过 {@code TableInfoPlugin#getTableInfoList()} 获取插件生成的表信息列表</li>
     *             <li>每个 TableInfo 包含表名、注释、实体类名、主键列、普通列和基础列</li>
     *         </ul>
     *     </li>
     *
     *     <li>渲染模板文件：
     *         <ul>
     *             <li>遍历每个 TableInfo 和 {@link TemplatePaths} 枚举中的模板</li>
     *             <li>根据模板计算输出文件路径：
     *                 <ul>
     *                     <li>调用 {@link TemplatePaths#getOutputPath(FoundryConfig, String)}</li>
     *                     <li>路径规则支持模块名、包路径、文件名模板（如 {0}Api.java）</li>
     *                 </ul>
     *             </li>
     *             <li>如果模板配置了 {@code TemplatePaths#isSkipIfExists()} 并且文件已存在且非空，则跳过生成</li>
     *             <li>构建模板数据模型：
     *                 <ul>
     *                     <li>config: FoundryConfig 配置信息</li>
     *                     <li>table: TableInfo 表信息</li>
     *                     <li>random: Random 对象，用于模板中生成随机数据</li>
     *                 </ul>
     *             </li>
     *             <li>调用 {@link AbstractGenerator#processTemplate(String, Path, Map)} 渲染模板到目标文件</li>
     *         </ul>
     *     </li>
     *
     *     <li>生成完成后打印日志：
     *         <ul>
     *             <li>记录每张表生成的 TableInfo 信息，便于调试和验证生成结果</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * <p>生成效果示例：</p>
     * <pre>{@code
     * 表 user：
     * ├─ PO: com/g2rain/demo/po/UserPo.java
     * ├─ DAO: com/g2rain/demo/dao/UserDao.java
     * ├─ Mapper XML: com/g2rain/demo/mapper/UserMapper.xml
     * ├─ Service: com/g2rain/demo/service/UserService.java
     * ├─ ServiceImpl: com/g2rain/demo/service/impl/UserServiceImpl.java
     * ├─ Controller: com/g2rain/demo/controller/UserController.java
     * ├─ DTO: com/g2rain/demo/dto/UserDto.java
     * ├─ VO: com/g2rain/demo/vo/UserVo.java
     * └─ API: com/g2rain/demo/api/UserApi.java
     * }</pre>
     *
     * <p>注意事项：</p>
     * <ul>
     *     <li>必须在 {@code FoundryConfig#setTables(String)} 中指定表名，否则不会生成任何代码</li>
     *     <li>PO/Mapper 使用 MyBatis Generator，其他层级文件使用 Freemarker 模板渲染</li>
     *     <li>模板渲染支持按模块和包路径生成文件，保证目录结构合理</li>
     *     <li>skipIfExists 可防止覆盖手动修改的文件</li>
     *     <li>生成过程中出现异常会打印日志，不会中断整个生成流程</li>
     * </ul>
     *
     * @throws Exception 当数据库连接失败、PO 目录创建失败或模板渲染失败时抛出
     */
    @Override
    public void generate() throws Exception {
        String tables = foundryConfig.getTables();
        if (Strings.isBlank(tables)) {
            log.error("Table name is null or blank, tableName:" + tables);
            return;
        }

        // 将表名加入 MyBatis Generator 上下文
        Arrays.stream(tables.split(",")).map(String::strip).distinct().forEach(tableName ->
                defaultContext.addTableConfiguration(foundryConfig.toTableConfiguration(defaultContext, tableName))
        );

        // 执行 MyBatis Generator
        List<String> warnings = new ArrayList<>();
        try {
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
        } catch (InterruptedException e) {
            log.error("Code generation execution error, message: " + e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

        // 输出警告
        warnings.forEach(log::warn);

        // 获取 TableInfo 列表，用于渲染模板
        List<TableInfo> tableInfoList = TableInfoPlugin.getTableInfoList();
        if (tableInfoList.isEmpty()) {
            return;
        }

        for (TableInfo t : tableInfoList) {
            for (TemplatePaths p : TemplatePaths.values()) {
                Path outputFile = p.getOutputPath(foundryConfig, t.getEntityName());
                // 文件存在且非空 且 (skipIfExists 为 true 或者 overwrite 为 false) 则跳过
                if (Files.exists(outputFile) && Files.size(outputFile) > 0 && (p.isSkipIfExists() || !foundryConfig.isOverwrite())) {
                    continue;
                }

                // 构造数据模型, 渲染模板文件
                processTemplate(p.getTemplateName(), outputFile, Map.of("config", foundryConfig, "table", t));
            }

            log.info("tableInfo:" + t.toString());
        }
    }
}