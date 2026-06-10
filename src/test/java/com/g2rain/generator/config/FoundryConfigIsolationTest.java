package com.g2rain.generator.config;

import com.g2rain.generator.model.ColumnInfo;
import com.g2rain.generator.model.TableInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoundryConfigIsolationTest {

    private final FoundryConfig config = new FoundryConfig(
        "demo",
        "com.g2rain.demo",
        "jdbc:mysql://localhost:3306/demo",
        "com.mysql.cj.jdbc.Driver",
        "root",
        "pwd"
    );

    @Test
    void defaultsEnableIsolationForTenantTable() {
        TableInfo table = tenantTable("article");

        assertTrue(config.isIsolationEnabledFor(table));
        assertEquals("organ_id", config.getTenantColumn(table).getColumnName());
        assertEquals("organId", config.getTenantColumn(table).getPropertyName());
        assertTrue(config.getTenantColumnSet().contains("organ_id"));
    }

    @Test
    void withIsolationFalseDisablesCodegen() {
        config.setWithIsolation(false);
        TableInfo table = tenantTable("article");

        assertFalse(config.isIsolationEnabledFor(table));
        assertNotNull(config.getTenantColumn(table));
    }

    @Test
    void excludeTablesSkipsMatchedTable() {
        config.setExcludeTables("article,dict_type");
        TableInfo table = tenantTable("article");

        assertFalse(config.isIsolationEnabledFor(table));
    }

    @Test
    void tenantColumnsFollowConfiguredOrder() {
        config.setTenantColumns("tenant_id,organ_id");
        TableInfo table = new TableInfo();
        table.setTableName("article");
        table.setEntityName("Article");
        table.setPrimaryKey(column("id", "id", "BIGINT"));
        table.setColumns(List.of(
            column("tenant_id", "tenantId", "BIGINT"),
            column("organ_id", "organId", "BIGINT")
        ));

        ColumnInfo tenantColumn = config.getTenantColumn(table);

        assertNotNull(tenantColumn);
        assertEquals("tenant_id", tenantColumn.getColumnName());
    }

    @Test
    void tableWithoutTenantColumnIsNotEnabled() {
        TableInfo table = new TableInfo();
        table.setTableName("dict_type");
        table.setEntityName("DictType");
        table.setPrimaryKey(column("id", "id", "BIGINT"));
        table.setColumns(List.of(column("type_code", "typeCode", "VARCHAR")));

        assertFalse(config.isIsolationEnabledFor(table));
    }

    private TableInfo tenantTable(String tableName) {
        TableInfo table = new TableInfo();
        table.setTableName(tableName);
        table.setEntityName("Article");
        table.setPrimaryKey(column("id", "id", "BIGINT"));
        table.setColumns(List.of(column("organ_id", "organId", "BIGINT")));
        return table;
    }

    private ColumnInfo column(String columnName, String propertyName, String columnType) {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(columnName);
        column.setPropertyName(propertyName);
        column.setColumnType(columnType);
        column.setJavaType("Long");
        return column;
    }
}
