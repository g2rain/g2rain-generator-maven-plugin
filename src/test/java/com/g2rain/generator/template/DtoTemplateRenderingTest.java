package com.g2rain.generator.template;

import com.g2rain.generator.config.FoundryConfig;
import com.g2rain.generator.generator.FoundryGenerator;
import com.g2rain.generator.model.ColumnInfo;
import com.g2rain.generator.model.TableInfo;
import com.g2rain.generator.utils.ValidationRuleHelper;
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

class DtoTemplateRenderingTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() throws Exception {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(FoundryGenerator.class, "/templates");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setSharedVariable("validation", ValidationRuleHelper.INSTANCE);
    }

    @Test
    void notNullStringColumnRendersNotBlankWithCreateGroup() throws Exception {
        String rendered = renderDto(roleTable());

        assertTrue(rendered.contains("@NotBlank(groups = CreateGroup.class)"));
        assertTrue(rendered.contains("import com.g2rain.common.validation.CreateGroup;"));
        assertTrue(rendered.contains("import jakarta.validation.constraints.NotBlank;"));
    }

    @Test
    void notNullLongColumnRendersNotNullWithCreateGroup() throws Exception {
        String rendered = renderDto(roleTable());

        assertTrue(rendered.contains("@NotNull(groups = CreateGroup.class)"));
        assertTrue(rendered.contains("import jakarta.validation.constraints.NotNull;"));
    }

    @Test
    void nullableColumnHasNoRequiredAnnotations() throws Exception {
        String rendered = renderDto(roleTable());

        int roleTypeIndex = rendered.indexOf("private String roleType;");
        assertTrue(roleTypeIndex > 0);
        String roleTypeBlock = rendered.substring(Math.max(0, roleTypeIndex - 200), roleTypeIndex);
        assertFalse(roleTypeBlock.contains("@NotNull"));
        assertFalse(roleTypeBlock.contains("@NotBlank"));
    }

    @Test
    void varcharColumnRendersSizeWithoutGroups() throws Exception {
        String rendered = renderDto(roleTable());

        assertTrue(rendered.contains("@Size(max = 64)"));
        assertFalse(rendered.contains("@Size(max = 64, groups"));
        assertTrue(rendered.contains("import jakarta.validation.constraints.Size;"));
    }

    @Test
    void renderedDtoDoesNotContainUpdateGroup() throws Exception {
        String rendered = renderDto(roleTable());

        assertFalse(rendered.contains("UpdateGroup"));
    }

    @Test
    void schemaDescriptionMarksCreateRequiredWithoutRequiredMode() throws Exception {
        String rendered = renderDto(roleTable());

        assertTrue(rendered.contains("（新增必填）"));
        assertTrue(rendered.contains("maxLength = 64"));
        assertFalse(rendered.contains("requiredMode = Schema.RequiredMode.REQUIRED"));
    }

    private String renderDto(TableInfo table) throws Exception {
        Template template = configuration.getTemplate("dto.ftl");
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

    private TableInfo roleTable() {
        TableInfo table = new TableInfo();
        table.setTableName("role");
        table.setEntityName("Role");
        table.setTableComment("角色");
        table.setPrimaryKey(column("id", "id", "Long", "BIGINT", "主键", true, true, true, 0));
        table.setColumns(List.of(
            column("organ_id", "organId", "Long", "BIGINT", "机构ID", false, false, false, 0),
            column("role_name", "roleName", "String", "VARCHAR", "角色名称", false, false, false, 64),
            column("role_type", "roleType", "String", "VARCHAR", "角色类型", false, true, false, 32)
        ));
        return table;
    }

    private ColumnInfo column(String columnName, String propertyName, String javaType, String columnType,
                              String columnComment, boolean primaryKey, boolean nullable, boolean autoIncrement, int length) {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(columnName);
        column.setPropertyName(propertyName);
        column.setJavaType(javaType);
        column.setColumnType(columnType);
        column.setColumnComment(columnComment);
        column.setPrimaryKey(primaryKey);
        column.setNullable(nullable);
        column.setAutoIncrement(autoIncrement);
        column.setLength(length);
        return column;
    }
}
