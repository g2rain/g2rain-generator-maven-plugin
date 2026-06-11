package com.g2rain.generator.template;

import com.g2rain.generator.config.FoundryConfig;
import com.g2rain.generator.generator.FoundryGenerator;
import com.g2rain.generator.model.ColumnInfo;
import com.g2rain.generator.model.TableInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceImplTemplateRenderingTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(FoundryGenerator.class, "/templates");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    @Test
    void saveMethodCallsValidateSaveOnly() throws Exception {
        String rendered = renderServiceImpl(sampleTable());

        assertTrue(rendered.contains("import com.g2rain.common.validation.Validations;"));
        assertTrue(rendered.contains("Validations.validateSave(dto);"));
        assertFalse(rendered.contains("validateDefault"));
        assertFalse(rendered.contains("validateCreate"));
    }

    @Test
    void updateAndDeleteCheckExistenceById() throws Exception {
        String rendered = renderServiceImpl(sampleTable());

        assertTrue(rendered.contains("roleDao.selectById(id);"));
        assertTrue(rendered.contains("SystemErrorCode.DATA_NOT_EXISTS"));
    }

    private String renderServiceImpl(TableInfo table) throws Exception {
        Template template = configuration.getTemplate("serviceImpl.ftl");
        StringWriter writer = new StringWriter();
        template.process(Map.of("config", defaultConfig(), "table", table), writer);
        return writer.toString();
    }

    private FoundryConfig defaultConfig() {
        return new FoundryConfig(
            "g2rain-iam",
            "com.g2rain.iam",
            "jdbc:mysql://localhost:3306/iam",
            "com.mysql.cj.jdbc.Driver",
            "root",
            "pwd"
        );
    }

    private TableInfo sampleTable() {
        TableInfo table = new TableInfo();
        table.setTableName("role");
        table.setEntityName("Role");
        table.setEntityNameLower("role");
        table.setTableComment("角色");
        table.setPrimaryKey(primaryKey());
        table.setColumns(List.of(businessColumn()));
        return table;
    }

    private ColumnInfo primaryKey() {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName("id");
        column.setPropertyName("id");
        column.setJavaType("Long");
        column.setColumnType("BIGINT");
        column.setPrimaryKey(true);
        column.setAutoIncrement(true);
        return column;
    }

    private ColumnInfo businessColumn() {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName("role_name");
        column.setPropertyName("roleName");
        column.setJavaType("String");
        column.setColumnType("VARCHAR");
        return column;
    }
}
