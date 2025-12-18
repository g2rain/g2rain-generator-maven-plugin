package ${config.voPackage};

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseVo;

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
<#-- 导入ConditionalJsonIgnore和AdminCompanyCondition（如果存在deleteFlagColumn字段） -->
<#if table.deleteFlagColumn??>
import com.g2rain.common.json.ConditionalJsonIgnore;
import com.g2rain.common.json.AdminCompanyCondition;
</#if>

/**
 * ${table.tableComment!''}返回VO
 * 关联表名: ${table.tableName}
 * 功能：封装接口返回数据，继承BaseVo复用基础字段逻辑，隔离数据库实体与前端展示层
 *
 * @author ${config.getAuthor()}
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ${table.entityName}Vo extends BaseVo {
    <#-- 2. 业务字段（表特有核心字段，BaseVo未包含，单独定义，排除deleteFlag和version） -->
    <#list table.columns as column>
    <#if column != table.deleteFlagColumn && column != table.versionColumn>

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
    @ConditionalJsonIgnore(adminCompany = AdminCompanyCondition.TRUE)
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