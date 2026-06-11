package com.g2rain.generator.utils;

import com.g2rain.generator.model.ColumnInfo;

/**
 * 根据列元数据推导 Bean Validation 注解文本，供 FreeMarker 模板渲染 DTO。
 */
public final class ValidationRuleHelper {

    public static final ValidationRuleHelper INSTANCE = new ValidationRuleHelper();

    private ValidationRuleHelper() {
    }

    public boolean skipColumn(ColumnInfo column) {
        return column.isPrimaryKey()
            || column.isBaseColumn()
            || column.isDeleteFlag()
            || column.isVersion();
    }

    public boolean shouldValidateRequired(ColumnInfo column) {
        return !column.isNullable() && !skipColumn(column);
    }

    public String requiredAnnotation(ColumnInfo column) {
        if (!shouldValidateRequired(column)) {
            return "";
        }
        if ("String".equals(column.getJavaType())) {
            return "@NotBlank(groups = CreateGroup.class)";
        }
        return "@NotNull(groups = CreateGroup.class)";
    }

    public String sizeAnnotation(ColumnInfo column) {
        if (!"String".equals(column.getJavaType()) || column.getLength() <= 0) {
            return "";
        }
        return "@Size(max = " + column.getLength() + ")";
    }

    public boolean needsValidationImports(ColumnInfo column) {
        return shouldValidateRequired(column)
            || (("String".equals(column.getJavaType()) && column.getLength() > 0));
    }

    public boolean needsCreateGroupImport(ColumnInfo column) {
        return shouldValidateRequired(column);
    }

    public boolean needsSizeImport(ColumnInfo column) {
        return "String".equals(column.getJavaType()) && column.getLength() > 0;
    }

    public String schemaDescription(ColumnInfo column) {
        String comment = column.getColumnComment();
        if (comment == null) {
            comment = "";
        }
        if (shouldValidateRequired(column)) {
            return comment + "（新增必填）";
        }
        return comment;
    }

    public int schemaMaxLength(ColumnInfo column) {
        if ("String".equals(column.getJavaType()) && column.getLength() > 0) {
            return column.getLength();
        }
        return 0;
    }
}
