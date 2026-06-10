package com.g2rain.generator.utils;

import com.g2rain.generator.model.ColumnInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationRuleHelperTest {

    private final ValidationRuleHelper helper = ValidationRuleHelper.INSTANCE;

    @Test
    void skipColumnForPrimaryKeyAndBaseColumns() {
        assertTrue(helper.skipColumn(column("id", "id", "Long", true, true, 0)));
        assertTrue(helper.skipColumn(column("create_time", "createTime", "LocalDateTime", false, true, 0)));
        assertTrue(helper.skipColumn(column("delete_flag", "deleteFlag", "Integer", false, true, 0)));
        assertTrue(helper.skipColumn(column("version", "version", "Integer", false, true, 0)));
    }

    @Test
    void shouldValidateRequiredWhenNotNullableAndNotSkipped() {
        ColumnInfo column = column("role_name", "roleName", "String", false, false, 64);

        assertTrue(helper.shouldValidateRequired(column));
    }

    @Test
    void shouldNotValidateRequiredWhenNullable() {
        ColumnInfo column = column("role_type", "roleType", "String", false, true, 32);

        assertFalse(helper.shouldValidateRequired(column));
    }

    @Test
    void requiredAnnotationUsesNotBlankForString() {
        ColumnInfo column = column("role_name", "roleName", "String", false, false, 64);

        assertEquals("@NotBlank(groups = CreateGroup.class)", helper.requiredAnnotation(column));
    }

    @Test
    void requiredAnnotationUsesNotNullForNonString() {
        ColumnInfo column = column("organ_id", "organId", "Long", false, false, 0);

        assertEquals("@NotNull(groups = CreateGroup.class)", helper.requiredAnnotation(column));
    }

    @Test
    void sizeAnnotationOnlyForStringWithLength() {
        ColumnInfo withLength = column("role_name", "roleName", "String", false, true, 64);
        ColumnInfo withoutLength = column("role_name", "roleName", "String", false, true, 0);
        ColumnInfo nonString = column("organ_id", "organId", "Long", false, true, 64);

        assertEquals("@Size(max = 64)", helper.sizeAnnotation(withLength));
        assertEquals("", helper.sizeAnnotation(withoutLength));
        assertEquals("", helper.sizeAnnotation(nonString));
    }

    @Test
    void importFlagsReflectColumnMetadata() {
        ColumnInfo requiredString = column("role_name", "roleName", "String", false, false, 64);
        ColumnInfo nullableString = column("role_type", "roleType", "String", false, true, 32);

        assertTrue(helper.needsCreateGroupImport(requiredString));
        assertTrue(helper.needsSizeImport(requiredString));
        assertTrue(helper.needsValidationImports(requiredString));

        assertFalse(helper.needsCreateGroupImport(nullableString));
        assertTrue(helper.needsSizeImport(nullableString));
        assertTrue(helper.needsValidationImports(nullableString));
    }

    @Test
    void schemaDescriptionAppendsCreateRequiredSuffix() {
        ColumnInfo required = column("role_name", "roleName", "String", false, false, 64);
        required.setColumnComment("角色名称");
        ColumnInfo nullable = column("role_type", "roleType", "String", false, true, 32);
        nullable.setColumnComment("角色类型");

        assertEquals("角色名称（新增必填）", helper.schemaDescription(required));
        assertEquals("角色类型", helper.schemaDescription(nullable));
    }

    @Test
    void schemaMaxLengthOnlyForStringWithLength() {
        ColumnInfo stringWithLength = column("role_name", "roleName", "String", false, true, 64);
        ColumnInfo longColumn = column("organ_id", "organId", "Long", false, false, 0);

        assertEquals(64, helper.schemaMaxLength(stringWithLength));
        assertEquals(0, helper.schemaMaxLength(longColumn));
    }

    private ColumnInfo column(String columnName, String propertyName, String javaType,
                              boolean primaryKey, boolean nullable, int length) {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(columnName);
        column.setPropertyName(propertyName);
        column.setJavaType(javaType);
        column.setPrimaryKey(primaryKey);
        column.setNullable(nullable);
        column.setLength(length);
        return column;
    }
}
