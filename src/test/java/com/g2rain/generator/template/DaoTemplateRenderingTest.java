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

class DaoTemplateRenderingTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(FoundryGenerator.class, "/templates");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    @Test
    void tenantTableRendersIsolationMethods() throws Exception {
        String rendered = renderDao(defaultConfig(), tenantTable());

        assertTrue(rendered.contains("@DataIsolation(organIdPropertyName = \"organId\", organIdColumnName = \"organ_id\")"));
        assertTrue(rendered.contains("int insertWithoutIsolation(ArticlePo entity);"));
        assertTrue(rendered.contains("int updateWithoutIsolation(ArticlePo entity);"));
        assertTrue(rendered.contains("ArticlePo selectByIdWithoutIsolation(Long id);"));
        assertTrue(rendered.contains("List<ArticlePo> selectListWithoutIsolation(ArticleSelectDto selectDto);"));
        assertTrue(rendered.contains("import com.g2rain.data.isolation.annotations.DataIsolation;"));
        assertTrue(rendered.contains("import com.g2rain.data.isolation.annotations.IgnoreIsolation;"));
    }

    @Test
    void withIsolationFalseSkipsIsolationArtifacts() throws Exception {
        FoundryConfig config = defaultConfig();
        config.setWithIsolation(false);

        String rendered = renderDao(config, tenantTable());

        assertFalse(rendered.contains("DataIsolation"));
        assertFalse(rendered.contains("IgnoreIsolation"));
        assertFalse(rendered.contains("WithoutIsolation"));
    }

    @Test
    void excludeTablesSkipsIsolationArtifacts() throws Exception {
        FoundryConfig config = defaultConfig();
        config.setExcludeTables("article");

        String rendered = renderDao(config, tenantTable());

        assertFalse(rendered.contains("WithoutIsolation"));
    }

    private String renderDao(FoundryConfig config, TableInfo table) throws Exception {
        Template template = configuration.getTemplate("dao.ftl");
        StringWriter writer = new StringWriter();
        template.process(Map.of("config", config, "table", table), writer);
        return writer.toString();
    }

    private FoundryConfig defaultConfig() {
        return new FoundryConfig(
            "g2rain-cms",
            "com.g2rain.cms",
            "jdbc:mysql://localhost:3306/cms",
            "com.mysql.cj.jdbc.Driver",
            "root",
            "pwd"
        );
    }

    private TableInfo tenantTable() {
        TableInfo table = new TableInfo();
        table.setTableName("article");
        table.setEntityName("Article");
        table.setPrimaryKey(column("id", "id", "Long", "BIGINT"));
        table.setColumns(List.of(column("organ_id", "organId", "Long", "BIGINT")));
        return table;
    }

    private ColumnInfo column(String columnName, String propertyName, String javaType, String columnType) {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(columnName);
        column.setPropertyName(propertyName);
        column.setJavaType(javaType);
        column.setColumnType(columnType);
        return column;
    }
}
