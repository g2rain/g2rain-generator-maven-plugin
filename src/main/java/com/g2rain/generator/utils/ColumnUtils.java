package com.g2rain.generator.utils;


import java.util.Objects;
import java.util.Set;

/**
 * 数据库列名工具类，用于判断列名是否为系统的基础字段。
 * <p>
 * 该类为工具类，提供静态方法以便在代码生成、实体映射或数据库同步过程中
 * 快速判断某个列是否为通用基础字段（如创建时间、更新时间、版本号等）。
 * <p>
 * 常见使用场景包括：
 * <ul>
 *     <li>代码生成时跳过公共字段；</li>
 *     <li>动态 SQL 组装时自动排除系统列；</li>
 *     <li>数据库同步比对时区分业务列与基础列。</li>
 * </ul>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * boolean isBase = ColumnUtils.isBaseColumn("create_time"); // true
 * boolean isCustom = ColumnUtils.isBaseColumn("user_name"); // false
 * }</pre>
 *
 * <p>命名规范：所有判断均基于小写匹配。</p>
 *
 * @author alpha
 * @since 2025/10/28
 */
public final class ColumnUtils {

    /**
     * 系统基础字段集合。
     * <p>
     * 通常在数据库表中，这些列用于通用的审计或版本控制逻辑：
     * <ul>
     *     <li>{@code create_time} —— 创建时间</li>
     *     <li>{@code update_time} —— 更新时间</li>
     *     <li>{@code version} —— 数据版本号（用于乐观锁）</li>
     * </ul>
     */
    private static final Set<String> baseColumnNames = Set.of("create_time", "update_time");

    private static final Set<String> privateKeyColumnNames = Set.of("id");

    private static final Set<String> deleteFlagColumnNames = Set.of("delete_flag");

    private static final Set<String> versionColumnNames = Set.of("version");

    private static final Set<String> notSupportUpdateColumnNames = Set.of("id", "create_time", "version");

    /**
     * 私有构造方法，防止实例化。
     * <p>
     * 工具类应仅通过静态方法调用，不应被实例化。
     */
    private ColumnUtils() {
        // 禁止实例化
    }

    /**
     * 判断给定列名是否为系统基础列。
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * ColumnUtils.isBaseColumn("CREATE_TIME"); // true
     * ColumnUtils.isBaseColumn("user_id");     // false
     * }</pre>
     *
     * @param columnName 数据库列名，区分大小写前会转换为小写
     * @return 如果列名属于基础列（create_time, update_time, version）则返回 {@code true}；
     * 若为 {@code null} 或非基础列则返回 {@code false}。
     */
    public static boolean isBaseColumn(String columnName) {
        if (Objects.isNull(columnName)) {
            return false;
        }

        return baseColumnNames.contains(columnName.toLowerCase());
    }

    public static boolean isPrivateKeyColumn(String columnName) {
        if (Objects.isNull(columnName)) {
            return false;
        }
        return privateKeyColumnNames.contains(columnName.toLowerCase());
    }

    public static boolean isDeleteFlagColumn(String columnName) {
        if (Objects.isNull(columnName)) {
            return false;
        }
        return deleteFlagColumnNames.contains(columnName.toLowerCase());
    }

    public static boolean isVersionColumn(String columnName) {
        if (Objects.isNull(columnName)) {
            return false;
        }
        return versionColumnNames.contains(columnName.toLowerCase());
    }

    public static boolean isSupportUpdate(String columnName) {
        if (Objects.isNull(columnName)) {
            return false;
        }
        return !notSupportUpdateColumnNames.contains(columnName.toLowerCase());
    }
}
