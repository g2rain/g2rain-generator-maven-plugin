package com.g2rain.generator.plugin;

import com.g2rain.generator.model.ColumnInfo;
import com.g2rain.generator.model.TableInfo;
import com.g2rain.generator.utils.ColumnUtils;
import com.g2rain.generator.utils.Strings;
import lombok.Getter;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PluginConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * MyBatis Generator 插件，用于收集表及列信息，生成表结构元数据。
 * <p>
 * 该插件会在生成模型类过程中提取表名、列名、注释、主键和基础列信息，
 * 并将其封装到 {@link TableInfo} 与 {@link ColumnInfo} 对象中，便于代码生成器或后续处理使用。
 * </p>
 *
 * <p><b>功能说明：</b></p>
 * <ul>
 *     <li>收集数据库表信息，包括表名、注释、实体名（驼峰命名）等。</li>
 *     <li>提取列信息，包括主键、基础字段（create_time、update_time、version）及普通字段。</li>
 *     <li>自动移除基础字段和主键字段，避免重复生成 Getter/Setter。</li>
 *     <li>提供静态方法 {@link #generatePluginConfiguration()} 方便在 MyBatis Generator 配置中注册插件。</li>
 * </ul>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * TableInfoPlugin.generatePluginConfiguration();
 * // 配置后执行 MyBatis Generator 时会收集表及列信息
 * }</pre>
 *
 * @author jagger
 * @since 2025/10/25
 */
public class TableInfoPlugin extends PluginAdapter {
    private static final String DELETE_FLAG_COLUMN = "delete_flag";

    private static final String DELETE_FLAG_TYPE = "Boolean";

    /**
     * 收集的表信息列表，包含所有处理过的表元数据。
     */
    @Getter
    private static final List<TableInfo> tableInfoList = new ArrayList<>();

    /**
     * 生成插件配置对象。
     * <p>
     * 每次调用会清空已有的 {@link #tableInfoList}。
     *
     * @return {@link PluginConfiguration} 用于 MyBatis Generator 注册插件
     */
    public static PluginConfiguration generatePluginConfiguration() {
        PluginConfiguration configuration = new PluginConfiguration();
        configuration.setConfigurationType(TableInfoPlugin.class.getName());
        tableInfoList.clear();
        return configuration;
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 表模型生成时回调，用于收集表元数据。
     * <p>
     * 会自动区分主键字段、基础字段与业务字段，并移除基础字段与主键字段。
     *
     * @param topLevelClass     生成的模型类
     * @param introspectedTable 表信息
     * @return {@code true} 表示允许继续生成
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 从IntrospectedTable中提取表元数据
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(introspectedTable.getTableConfiguration().getTableName());
        tableInfo.setTableComment(introspectedTable.getRemarks());
        tableInfo.setEntityName(Strings.underlineToCamel(tableInfo.getTableName().toLowerCase(), true));
        tableInfo.setEntityNameLower(Strings.underlineToCamel(tableInfo.getTableName().toLowerCase(), false));

        // 提取字段信息
        List<ColumnInfo> columnInfos = new ArrayList<>();
        List<ColumnInfo> baseColumns = new ArrayList<>();

        Set<String> removeColumnNameSet = new HashSet<>();
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            ColumnInfo columnInfo = getColumnInfo(column);

            // 记录主键
            if (columnInfo.isPrimaryKey()) {
                tableInfo.setPrimaryKey(columnInfo);
                removeColumnNameSet.add(columnInfo.getColumnName());
            } else if (ColumnUtils.isBaseColumn(columnInfo.getColumnName())) {
                baseColumns.add(columnInfo);
                removeColumnNameSet.add(columnInfo.getColumnName());
            } else if (columnInfo.isDeleteFlag()) {
                tableInfo.setDeleteFlagColumn(columnInfo);
                removeColumnNameSet.add(columnInfo.getColumnName());
            } else if (columnInfo.isVersion()) {
                tableInfo.setVersionColumn(columnInfo);
                removeColumnNameSet.add(columnInfo.getColumnName());
            } else {
                columnInfos.add(columnInfo);
            }
        }

        if (!removeColumnNameSet.isEmpty()) {
            // 使用迭代器移除元素（避免ConcurrentModificationException）
            Iterator<IntrospectedColumn> iterator = introspectedTable.getAllColumns().iterator();
            while (iterator.hasNext()) {
                IntrospectedColumn column = iterator.next();
                // 获取数据库字段名
                String columnName = column.getActualColumnName();
                // 移除字段
                if (removeColumnNameSet.contains(columnName)) {
                    iterator.remove();
                }
            }
        }

        tableInfo.setColumns(columnInfos);
        if (!baseColumns.isEmpty()) {
            tableInfo.setBaseColumns(baseColumns);
        }

        tableInfoList.add(tableInfo);
        return false; // 禁止MBG生成PO文件
    }

    /**
     * 将 {@link IntrospectedColumn} 转换为 {@link ColumnInfo} 对象。
     *
     * @param column 数据库列元数据
     * @return ColumnInfo 封装的列信息
     */
    private static ColumnInfo getColumnInfo(IntrospectedColumn column) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName(column.getActualColumnName());
        columnInfo.setColumnType(column.getJdbcTypeName());
        columnInfo.setColumnComment(column.getRemarks());
        columnInfo.setPropertyName(column.getJavaProperty());

        // 特殊处理 delete_flag 字段：将 tinyint 转换为 Boolean 类型
        String columnName = column.getActualColumnName();
        if (DELETE_FLAG_COLUMN.equalsIgnoreCase(columnName)) {
            columnInfo.setJavaType(DELETE_FLAG_TYPE);
        } else {
            columnInfo.setJavaType(column.getFullyQualifiedJavaType().getShortName());
        }

        List<IntrospectedColumn> primaryKeyColumns = column.getIntrospectedTable().getPrimaryKeyColumns();
        if (Objects.nonNull(primaryKeyColumns) && !primaryKeyColumns.isEmpty()) {
            for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
                if (primaryKeyColumn.getActualColumnName().equals(column.getActualColumnName())) {
                    columnInfo.setPrimaryKey(true);
                    break;
                }
            }
        }

        columnInfo.setAutoIncrement(column.isAutoIncrement());
        return columnInfo;
    }

    /**
     * 在生成字段时，如果该字段是基础列（如 create_time、update_time、version），则跳过生成。
     *
     * @param field              字段对象
     * @param topLevelClass      所属的类
     * @param introspectedColumn 列信息
     * @param introspectedTable  表信息
     * @param modelClassType     模型类型
     * @return {@code false} 表示跳过生成，{@code true} 表示生成
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (ColumnUtils.isBaseColumn(introspectedColumn.getActualColumnName())) {
            return false;
        }

        // 特殊处理 delete_flag 字段：将类型修改为 Boolean
        String columnName = introspectedColumn.getActualColumnName();
        if (DELETE_FLAG_COLUMN.equalsIgnoreCase(columnName)) {
            field.setType(new FullyQualifiedJavaType(DELETE_FLAG_TYPE));
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     * 在生成 Getter 方法时，如果对应字段是基础列，则跳过生成。
     *
     * @param method             Getter 方法对象
     * @param topLevelClass      所属类
     * @param introspectedColumn 列信息
     * @param introspectedTable  表信息
     * @param modelClassType     模型类型
     * @return {@code false} 表示跳过生成，{@code true} 表示生成
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (ColumnUtils.isBaseColumn(introspectedColumn.getActualColumnName())) {
            return false;
        }

        // 特殊处理 delete_flag 字段：将 getter 方法的返回类型修改为 Boolean
        String columnName = introspectedColumn.getActualColumnName();
        if (DELETE_FLAG_COLUMN.equalsIgnoreCase(columnName)) {
            method.setReturnType(new FullyQualifiedJavaType(DELETE_FLAG_TYPE));
        }

        return super.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     * 在生成 Setter 方法时，如果对应字段是基础列，则跳过生成。
     *
     * @param method             Setter 方法对象
     * @param topLevelClass      所属类
     * @param introspectedColumn 列信息
     * @param introspectedTable  表信息
     * @param modelClassType     模型类型
     * @return {@code false} 表示跳过生成，{@code true} 表示生成
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (ColumnUtils.isBaseColumn(introspectedColumn.getActualColumnName())) {
            return false;
        }

        // delete_flag 字段的 setter 方法参数类型会自动匹配 Boolean（因为字段类型已修改）
        return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     * 阻止 MyBatis Generator 自动生成 Mapper 接口文件。
     * <p>
     * 此方法返回 {@code false} 表示禁用 MBG 默认的 Mapper 接口生成逻辑，
     * 因为我们使用自定义的 Freemarker 模板来生成更符合项目规范的 Mapper 接口。
     *
     * @param ignored           MBG 生成的接口对象（在此实现中不被使用）
     * @param introspectedTable 表元数据信息，包含表结构详情
     * @return {@code false} 始终返回 false 以阻止默认的 Mapper 接口生成
     */
    @Override
    public boolean clientGenerated(Interface ignored, IntrospectedTable introspectedTable) {
        // 返回 false 阻止 MBG 生成 Mapper 接口
        return false;
    }

    /**
     * 阻止 MyBatis Generator 自动生成 Mapper XML 映射文件。
     * <p>
     * 此方法返回 {@code false} 表示禁用 MBG 默认的 XML 映射文件生成逻辑，
     * 因为我们使用自定义的 Freemarker 模板来生成更清晰、更易维护的 XML 映射文件。
     *
     * @param sqlMap            MBG 生成的 XML 映射文件对象（在此实现中不被使用）
     * @param introspectedTable 表元数据信息，包含表结构详情
     * @return {@code false} 始终返回 false 以阻止默认的 Mapper XML 生成
     */
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        // 返回 false 阻止 MBG 生成 Mapper XML
        return false;
    }
}
