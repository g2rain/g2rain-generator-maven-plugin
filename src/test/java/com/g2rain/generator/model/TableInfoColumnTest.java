package com.g2rain.generator.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableInfoColumnTest {

    @Test
    void hasColumnIsCaseInsensitive() {
        TableInfo table = sampleTable();

        assertTrue(table.hasColumn("organ_id"));
        assertTrue(table.hasColumn("ORGAN_ID"));
        assertFalse(table.hasColumn("tenant_id"));
    }

    @Test
    void getColumnReturnsMatchingColumn() {
        TableInfo table = sampleTable();

        ColumnInfo column = table.getColumn("Organ_Id");

        assertNotNull(column);
        assertEquals("organId", column.getPropertyName());
    }

    @Test
    void getAllColumnsDeduplicatesByColumnName() {
        TableInfo table = sampleTable();
        table.setDeleteFlagColumn(column("delete_flag", "deleteFlag"));
        table.setVersionColumn(column("version", "version"));

        List<ColumnInfo> allColumns = table.getAllColumns();

        assertEquals(5, allColumns.size());
        assertTrue(allColumns.stream().anyMatch(column -> "organ_id".equals(column.getColumnName())));
    }

    private TableInfo sampleTable() {
        TableInfo table = new TableInfo();
        table.setTableName("article");
        table.setEntityName("Article");
        table.setPrimaryKey(column("id", "id"));
        table.setBaseColumns(List.of(column("create_time", "createTime")));
        table.setColumns(List.of(column("organ_id", "organId")));
        return table;
    }

    private ColumnInfo column(String columnName, String propertyName) {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(columnName);
        column.setPropertyName(propertyName);
        column.setColumnType("BIGINT");
        column.setJavaType("Long");
        return column;
    }
}
