package ${config.getDtoPackage()};

import com.g2rain.common.model.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

<#-- 按需导入字段类型依赖 -->
<#assign importedLocalDateTime = false>
<#assign importedLocalDate = false>
<#assign importedLocalTime = false>
<#assign importedZonedDateTime = false>
<#assign importedOffsetDateTime = false>
<#assign importedBigDecimal = false>
<#assign importCreateGroup = false>
<#assign importNotNull = false>
<#assign importNotBlank = false>
<#assign importSize = false>
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
<#if validation.needsCreateGroupImport(column) && !importCreateGroup>
<#assign importCreateGroup = true>
</#if>
<#if validation.shouldValidateRequired(column) && "String" == column.javaType && !importNotBlank>
<#assign importNotBlank = true>
</#if>
<#if validation.shouldValidateRequired(column) && "String" != column.javaType && !importNotNull>
<#assign importNotNull = true>
</#if>
<#if validation.needsSizeImport(column) && !importSize>
<#assign importSize = true>
</#if>
</#list>
<#if importCreateGroup>
import com.g2rain.common.validation.CreateGroup;
</#if>
<#if importNotNull>
import jakarta.validation.constraints.NotNull;
</#if>
<#if importNotBlank>
import jakarta.validation.constraints.NotBlank;
</#if>
<#if importSize>
import jakarta.validation.constraints.Size;
</#if>

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
@Schema(description = "${table.tableComment!''} DTO")
public class ${table.entityName}Dto extends BaseDto {
    <#list table.columns as column>
    <#if !column.primaryKey>
    <#if column.propertyName != "createTime" && column.propertyName != "updateTime">
    <#if !column.isDeleteFlag() && !column.isVersion()>
    <#assign requiredAnn = validation.requiredAnnotation(column)>
    <#assign sizeAnn = validation.sizeAnnotation(column)>

    /**
     * ${column.columnComment!''}
     */
    <#if requiredAnn?has_content>
    ${requiredAnn}
    </#if>
    <#if sizeAnn?has_content>
    ${sizeAnn}
    </#if>
    @Schema(description = "${validation.schemaDescription(column)}"<#if validation.schemaMaxLength(column) gt 0>, maxLength = ${validation.schemaMaxLength(column)}</#if>)
    private ${column.javaType} ${column.propertyName};
    </#if>
    </#if>
    </#if>
    </#list>
}
