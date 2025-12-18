package ${config.getDtoPackage()};

import com.g2rain.common.model.BaseDto;
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
 * ${table.tableComment!''}查询DTO
 * 表名: ${table.tableName}
 *
 * @author ${config.getAuthor()}
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ${table.entityName}Dto extends BaseDto {
    <#-- 生成表特有字段（排除主键和基础类已包含的字段） -->
    <#list table.columns as column>
    <#-- 排除主键（基础类已包含id和ids） -->
    <#if !column.primaryKey>
    <#-- 排除基础类已有的时间字段 -->
    <#if column.propertyName != "createTime" && column.propertyName != "updateTime">
    <#-- 排除deleteFlag和version字段（单独处理） -->
    <#if column != table.deleteFlagColumn && column != table.versionColumn>

    /**
     * ${column.columnComment!''}
     */
    private ${column.javaType} ${column.propertyName};
    </#if>
    </#if>
    </#if>
    </#list>
    <#-- deleteFlag字段（如果存在） -->
    <#if table.deleteFlagColumn??>

    /**
     * ${table.deleteFlagColumn.columnComment!''}
     */
    private ${table.deleteFlagColumn.javaType} ${table.deleteFlagColumn.propertyName};
    </#if>
    <#-- version字段（如果存在） -->
    <#if table.versionColumn??>

    /**
     * ${table.versionColumn.columnComment!''}
     */
    private ${table.versionColumn.javaType} ${table.versionColumn.propertyName};
    </#if>
}