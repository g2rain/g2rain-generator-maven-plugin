package ${config.poPackage};

import com.g2rain.common.model.BasePo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

<#-- 按需导入字段类型依赖 -->
<#-- 初始化标志变量 -->
<#assign importedLocalDateTime = false>
<#assign importedLocalDate = false>
<#assign importedLocalTime = false>
<#assign importedZonedDateTime = false>
<#assign importedOffsetDateTime = false>
<#assign importedBigDecimal = false>
<#list table.columns as column>
<#if column.javaType?contains("LocalDateTime") && !importedLocalDateTime>
import java.time.LocalDateTime;
<#assign importedLocalDateTime = true>
</#if>
<#if column.javaType?contains("LocalDate") && !importedLocalDate>
import java.time.LocalDate;
<#assign importedLocalDate = true>
</#if>
<#if column.javaType?contains("LocalTime") && !importedLocalTime>
import java.time.LocalTime;
<#assign importedLocalTime = true>
</#if>
<#if column.javaType?contains("ZonedDateTime") && !importedZonedDateTime>
import java.time.ZonedDateTime;
<#assign importedZonedDateTime = true>
</#if>
<#if column.javaType?contains("OffsetDateTime") && !importedOffsetDateTime>
import java.time.OffsetDateTime;
<#assign importedOffsetDateTime = true>
</#if>
<#if column.javaType?contains("BigDecimal") && !importedBigDecimal>
import java.math.BigDecimal;
<#assign importedBigDecimal = true>
</#if>
</#list>

/**
 * ${table.tableComment!''}返回Po
 * 关联表名: ${table.tableName}
 * 功能：封装实体数据，继承BasePo复用基础字段逻辑
 *
 * @author ${config.getAuthor()}
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ${table.entityName}Po extends BasePo {
    <#-- 2. 业务字段（表特有核心字段，BasePo未包含，单独定义，排除deleteFlag和version） -->
    <#list table.columns as column>
    <#if !column.isDeleteFlag() && !column.isVersion()>

    /**
     * ${column.columnComment!''}
     */
    private ${column.javaType} ${column.propertyName};
    </#if>
    </#list>
    <#-- deleteFlag字段（如果存在） -->
    <#if table.deleteFlagColumn??>

    /**
     * ${table.deleteFlagColumn.columnComment!''}
     */
    private ${table.deleteFlagColumn.javaType} ${table.deleteFlagColumn.propertyName};
    </#if>
}