package ${config.getDtoPackage()};

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.g2rain.common.model.BaseSelectListDto;

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
 * ${table.tableComment!''}查询入参DTO
 * 用于${table.entityName}Dao.selectList方法的条件筛选
 * 表名: ${table.tableName!''}
 *
 * @author ${config.getAuthor()}
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ${table.entityName}SelectDto extends BaseSelectListDto {
    <#-- 生成表特有字段（排除父类已包含的字段，排除deleteFlag和version） -->
    <#list table.columns as column>
    <#-- 排除主键相关字段（父类已有id和ids） -->
    <#if !column.primaryKey>
    <#-- 排除父类已有的时间字段 -->
    <#if column.propertyName != "createTime" && column.propertyName != "updateTime">
    <#-- 排除deleteFlag和version字段（单独处理） -->
    <#if !column.isDeleteFlag() && !column.isVersion()>

    /**
     * ${column.columnComment!''}
     */
    private ${column.javaType} ${column.propertyName};
    </#if>
    </#if>
    </#if>
    </#list>
}