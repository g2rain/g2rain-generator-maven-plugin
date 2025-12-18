package com.g2rain.generator.model;

import lombok.Data;

import java.util.List;

/**
 * 数据库表元信息封装类，用于 MyBatis Generator 或代码生成器使用。
 * <p>
 * 该类包含表的基本信息、主键信息、普通字段信息以及基础字段信息。
 * 通常用于模板渲染生成对应实体类、DAO、DTO、VO 等文件。
 * </p>
 *
 * <p>示例：</p>
 * <pre>{@code
 * TableInfo tableInfo = new TableInfo();
 * tableInfo.setTableName("user");
 * tableInfo.setTableComment("用户表");
 * tableInfo.setEntityName("User");
 * tableInfo.setEntityNameLower("user");
 * tableInfo.setPrimaryKey(primaryKeyColumnInfo);
 * tableInfo.setColumns(columnList);
 * tableInfo.setBaseColumns(baseColumnList);
 * }</pre>
 *
 * @author jagger
 * @since 2025/10/25
 */
@Data
public class TableInfo {

    /**
     * 数据库表名
     * <p>例如：user、order_item 等</p>
     */
    private String tableName;

    /**
     * 表的描述或注释
     * <p>用于生成代码注释或文档</p>
     */
    private String tableComment;

    /**
     * 实体类名（首字母大写）
     * <p>用于生成 Java 类名，如 "User"、"OrderItem"</p>
     */
    private String entityName;

    /**
     * 实体类名（首字母小写）
     * <p>用于生成变量名，如 "user"、"orderItem"</p>
     */
    private String entityNameLower;

    /**
     * 普通字段列表（不包含主键和基础字段）
     * <p>每个元素为 {@link ColumnInfo} 对象，描述字段名、类型、注释等</p>
     */
    private List<ColumnInfo> columns;

    /**
     * 主键字段信息
     * <p>通常用于生成 Mapper、Service 的主键相关方法</p>
     */
    private ColumnInfo primaryKey;

    /**
     * 基础字段列表，如 create_time、update_time、version 等
     * <p>这些字段通常由框架统一处理，不会在业务实体中重复生成</p>
     */
    private List<ColumnInfo> baseColumns;

    /**
     * delete_flag字段信息（如果存在）
     */
    private ColumnInfo deleteFlagColumn;

    /**
     * version字段信息（如果存在）
     */
    private ColumnInfo versionColumn;
}