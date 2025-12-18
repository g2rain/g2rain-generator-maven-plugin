package com.g2rain.generator.model;



import com.g2rain.generator.utils.ColumnUtils;

import lombok.Data;

/**
 * 数据库表字段元信息封装类，用于 MyBatis Generator 或代码生成器使用。
 * <p>
 * 该类描述单个表字段的基本信息，包括字段名、类型、注释、对应 Java 属性及类型、
 * 是否为主键以及是否自增等。通常与 {@link TableInfo} 配合使用，用于模板渲染生成实体类、
 * Mapper、DTO、VO 等文件。
 * </p>
 *
 * <p>示例：</p>
 * <pre>{@code
 * ColumnInfo columnInfo = new ColumnInfo();
 * columnInfo.setColumnName("id");
 * columnInfo.setColumnType("BIGINT");
 * columnInfo.setColumnComment("用户ID");
 * columnInfo.setPropertyName("id");
 * columnInfo.setJavaType("Long");
 * columnInfo.setPrimaryKey(true);
 * columnInfo.setAutoIncrement(true);
 * }</pre>
 *
 * @author jagger
 * @since 2025/10/25
 */
@Data
public class ColumnInfo {

    /**
     * 数据库字段名
     * <p>例如：id、user_name、create_time</p>
     */
    private String columnName;

    /**
     * 数据库字段类型
     * <p>例如：VARCHAR、BIGINT、DECIMAL 等，用于生成对应 Java 类型</p>
     */
    private String columnType;

    /**
     * 数据库字段注释
     * <p>用于生成实体类字段注释或文档</p>
     */
    private String columnComment;

    /**
     * 对应的 Java 属性名
     * <p>通常是 {@link #columnName} 转换为驼峰命名的结果</p>
     * <p>例如：user_name -> userName</p>
     */
    private String propertyName;

    /**
     * 对应的 Java 类型名称
     * <p>例如：String、Long、BigDecimal 等</p>
     */
    private String javaType;

    /**
     * 是否为主键字段
     * <p>用于生成 Mapper、Service 的主键相关方法</p>
     */
    private boolean primaryKey;

    /**
     * 是否自增字段
     * <p>通常用于生成数据库插入时忽略或特殊处理</p>
     */
    private boolean autoIncrement;

    public boolean isPrivateKey() {
        return primaryKey || ColumnUtils.isPrivateKeyColumn(columnName);
    }

    public boolean isDeleteFlag() {
        return ColumnUtils.isDeleteFlagColumn(columnName);
    }

    public boolean isVersion() {
        return ColumnUtils.isVersionColumn(columnName);
    }

    public boolean isBaseColumn() {
        return ColumnUtils.isBaseColumn(columnName);
    }

    public boolean isSupportUpdate() { return ColumnUtils.isSupportUpdate(columnName);}
}