<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${config.getDaoPackage()}.${table.entityName}Dao">
    <!-- 通用结果集映射（包含所有字段：主键+基础字段+业务字段） -->
    <resultMap id="BaseResultMap" type="${config.getPoPackage()}.${table.entityName}Po">
        <!-- 主键字段 -->
        <id column="${table.primaryKey.columnName}" property="${table.primaryKey.propertyName}" jdbcType="${table.primaryKey.columnType}"/>
        <!-- 基础字段（createTime、updateTime） -->
        <#list table.baseColumns as column>
        <result column="${column.columnName}" property="${column.propertyName}" jdbcType="${column.columnType}"/>
        </#list>
        <!-- 业务字段（其他自定义字段） -->
        <#list table.columns as column>
        <result column="${column.columnName}" property="${column.propertyName}" jdbcType="${column.columnType}"/>
        </#list>
        <#if table.deleteFlagColumn??>
        <!-- deleteFlag字段（如果存在） -->
        <result column="${table.deleteFlagColumn.columnName}" property="${table.deleteFlagColumn.propertyName}" jdbcType="${table.deleteFlagColumn.columnType}"/>
        </#if>
        <#if table.versionColumn??>
        <!-- version字段（如果存在） -->
        <result column="${table.versionColumn.columnName}" property="${table.versionColumn.propertyName}" jdbcType="${table.versionColumn.columnType}"/>
        </#if>
    </resultMap>

    <!-- 定义可复用排序方向片段 -->
    <sql id="directionClause">
        <choose>
            <when test="item.direction == 'DESC'">DESC</when>
            <otherwise>ASC</otherwise>
        </choose>
    </sql>

    <!-- 可复用的动态排序 SQL 片段 -->
    <sql id="dynamicOrderBy">
        <trim prefix="ORDER BY" suffixOverrides=",">
            <foreach collection="safeSorts" item="item" separator=",">
                <choose>
                    <#if table.primaryKey??>
                    <when test="item.column == '${table.primaryKey.propertyName}'">
                        ${table.primaryKey.columnName}
                        <include refid="directionClause"/>
                    </when>
                    </#if>
                    <#list table.baseColumns as column>
                    <when test="item.column == '${column.propertyName}'">
                        ${column.columnName}
                        <include refid="directionClause"/>
                    </when>
                    </#list>
                    <#list table.columns as column>
                    <when test="item.column == '${column.propertyName}'">
                        ${column.columnName}
                        <include refid="directionClause"/>
                    </when>
                    </#list>
                </choose>
            </foreach>
        </trim>
    </sql>

    <!-- 插入单条记录 -->
    <insert id="insert" parameterType="${config.getPoPackage()}.${table.entityName}Po">
        INSERT INTO ${table.tableName} (
        <#if !table.primaryKey.autoIncrement>
        <!-- 主键字段（自增主键不包含在插入语句中） -->
        ${table.primaryKey.columnName}<#if (table.baseColumns?size > 0 || table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        </#if>
        <!-- 基础字段 -->
        ${table.baseColumns?map(column -> column.columnName)?join(", ")}<#if (table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <!-- 业务字段 -->
        ${table.columns?map(column -> column.columnName)?join(", ")}<#if (table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <#if table.deleteFlagColumn??>
        <!-- deleteFlag字段（如果存在） -->
        ${table.deleteFlagColumn.columnName}<#if table.versionColumn??>,</#if>
        </#if>
        <#if table.versionColumn??>
        <!-- version字段（如果存在） -->
        ${table.versionColumn.columnName}
        </#if>
        ) VALUES (
        <#if !table.primaryKey.autoIncrement>
        <!-- 主键值（自增主键不包含） -->
        <#noparse>#{</#noparse>${table.primaryKey.propertyName}, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse><#if (table.baseColumns?size > 0 || table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        </#if>
        <!-- 基础字段值 -->
        ${table.baseColumns?map(column -> "#{"+column.propertyName+", jdbcType="+column.columnType+"}")?join(", ")}<#if (table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <!-- 业务字段值 -->
        ${table.columns?map(column -> "#{"+column.propertyName+", jdbcType="+column.columnType+"}")?join(", ")}<#if (table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <#if table.deleteFlagColumn??>
        <!-- deleteFlag字段值（如果存在，设置默认值为0） -->
        0<#if table.versionColumn??>,</#if>
        </#if>
        <#if table.versionColumn??>
        <!-- version字段值（如果存在，设置默认值为0） -->
        0
        </#if>
        )
    </insert>

    <!-- 批量插入记录 -->
    <insert id="insertMultiple" parameterType="java.util.List">
        INSERT INTO ${table.tableName} (
        <#if !table.primaryKey.autoIncrement>
        <!-- 主键字段（自增主键不包含） -->
        ${table.primaryKey.columnName}<#if (table.baseColumns?size > 0 || table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        </#if>
        <!-- 基础字段 -->
        ${table.baseColumns?map(column -> column.columnName)?join(", ")}<#if (table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <!-- 业务字段 -->
        ${table.columns?map(column -> column.columnName)?join(", ")}<#if (table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <#if table.deleteFlagColumn??>
        <!-- deleteFlag字段（如果存在） -->
        ${table.deleteFlagColumn.columnName}<#if table.versionColumn??>,</#if>
        </#if>
        <#if table.versionColumn??>
        <!-- version字段（如果存在） -->
        ${table.versionColumn.columnName}
        </#if>
        ) VALUES
        <foreach collection="list" item="item" separator=",">
            (
            <#if !table.primaryKey.autoIncrement>
            <!-- 主键值（自增主键不包含） -->
            <#noparse>#{</#noparse>item.${table.primaryKey.propertyName}, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse><#if (table.baseColumns?size > 0 || table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
            </#if>
            <!-- 基础字段值 -->
            ${table.baseColumns?map(column -> "#{"+"item."+column.propertyName+", jdbcType="+column.columnType+"}")?join(", ")}<#if (table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
            <!-- 业务字段值 -->
            ${table.columns?map(column -> "#{"+"item."+column.propertyName+", jdbcType="+column.columnType+"}")?join(", ")}<#if (table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
            <#if table.deleteFlagColumn??>
            <!-- deleteFlag字段值（如果存在，设置默认值为0） -->
            0<#if table.versionColumn??>,</#if>
            </#if>
            <#if table.versionColumn??>
            <!-- version字段值（如果存在，设置默认值为0） -->
            0
            </#if>
            )
        </foreach>
    </insert>

    <!-- 根据ID更新记录（包含基础字段和业务字段） -->
    <update id="update" parameterType="${config.getPoPackage()}.${table.entityName}Po">
        UPDATE ${table.tableName}
        <set>
            <!-- 基础字段更新（排除version，version单独处理） -->
            <#list table.baseColumns as column>
            <#if column.supportUpdate>
            <if test="${column.propertyName} != null">
                ${column.columnName} = <#noparse>#{</#noparse>${column.propertyName}, jdbcType=${column.columnType}<#noparse>}</#noparse>,
            </if>
            </#if>
            </#list>
            <!-- 业务字段更新 -->
            <#list table.columns as column>
            <if test="${column.propertyName} != null">
                ${column.columnName} = <#noparse>#{</#noparse>${column.propertyName}, jdbcType=${column.columnType}<#noparse>}</#noparse>,
            </if>
            </#list>
            <#if table.versionColumn??>
            <!-- version字段更新（如果存在，版本号自增） -->
            ${table.versionColumn.columnName} = ${table.versionColumn.columnName} + 1
            </#if>
        </set>
        WHERE ${table.primaryKey.columnName} = <#noparse>#{</#noparse>${table.primaryKey.propertyName}, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse>
    </update>
    <#if table.versionColumn??>

    <!-- 根据ID和Version更新记录（乐观锁更新，包含基础字段和业务字段） -->
    <update id="updateByVersion" parameterType="${config.getPoPackage()}.${table.entityName}Po">
        UPDATE ${table.tableName}
        <set>
            <!-- 基础字段更新（排除version，version单独处理） -->
            <#list table.baseColumns as column>
            <#if column.supportUpdate>
            <if test="${column.propertyName} != null">
                ${column.columnName} = <#noparse>#{</#noparse>${column.propertyName}, jdbcType=${column.columnType}<#noparse>}</#noparse>,
            </if>
            </#if>
            </#list>
            <!-- 业务字段更新 -->
            <#list table.columns as column>
            <if test="${column.propertyName} != null">
                ${column.columnName} = <#noparse>#{</#noparse>${column.propertyName}, jdbcType=${column.columnType}<#noparse>}</#noparse>,
            </if>
            </#list>
            <!-- version字段更新（乐观锁版本号自增） -->
            ${table.versionColumn.columnName} = ${table.versionColumn.columnName} + 1
        </set>
        WHERE ${table.primaryKey.columnName} = <#noparse>#{</#noparse>${table.primaryKey.propertyName}, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse>
        AND ${table.versionColumn.columnName} = <#noparse>#{</#noparse>${table.versionColumn.propertyName}, jdbcType=${table.versionColumn.columnType}<#noparse>}</#noparse>
    </update>
    </#if>

    <#if table.deleteFlagColumn??>
    <!-- 根据ID删除记录（逻辑删除：将delete_flag设置为true） -->
    <update id="delete" parameterType="${table.primaryKey.javaType}">
        UPDATE ${table.tableName}
        <set>
            ${table.deleteFlagColumn.columnName} = 1
        </set>
        WHERE ${table.primaryKey.columnName} = <#noparse>#{</#noparse>id, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse>
        <!-- 只删除未删除的记录 -->
        AND (${table.deleteFlagColumn.columnName} = 0 OR ${table.deleteFlagColumn.columnName} IS NULL)
    </update>
    <#else>
    <!-- 根据ID删除记录（物理删除记录） -->
    <delete id="delete" parameterType="${table.primaryKey.javaType}">
        DELETE FROM ${table.tableName}
        WHERE ${table.primaryKey.columnName} = <#noparse>#{</#noparse>id, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse>
    </delete>
    </#if>

    <!-- 根据ID查询记录（查询所有字段） -->
    <select id="selectById" parameterType="${table.primaryKey.javaType}" resultMap="BaseResultMap">
        SELECT
        <!-- 主键字段 -->
        ${table.primaryKey.columnName}<#if (table.baseColumns?size > 0 || table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <!-- 基础字段 -->
        ${table.baseColumns?map(column -> column.columnName)?join(", ")}<#if (table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <!-- 业务字段 -->
        ${table.columns?map(column -> column.columnName)?join(", ")}<#if (table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <#if table.deleteFlagColumn??>
        <!-- deleteFlag字段（如果存在） -->
        ${table.deleteFlagColumn.columnName}<#if table.versionColumn??>,</#if>
        </#if>
        <#if table.versionColumn??>
        <!-- version字段（如果存在） -->
        ${table.versionColumn.columnName}
        </#if>
        FROM ${table.tableName}
        WHERE ${table.primaryKey.columnName} = <#noparse>#{</#noparse>id, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse>
        <#if table.deleteFlagColumn??>
        <!-- 过滤已删除的记录 -->
        AND (${table.deleteFlagColumn.columnName} = 0 OR ${table.deleteFlagColumn.columnName} IS NULL)
        </#if>
    </select>

    <!-- 根据SelectDto条件查询列表 -->
    <select id="selectList" parameterType="${config.getBasePackage()}.dto.${table.entityName}SelectDto" resultMap="BaseResultMap">
        SELECT
        <!-- 主键字段 -->
        ${table.primaryKey.columnName}<#if (table.baseColumns?size > 0 || table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <!-- 基础字段 -->
        ${table.baseColumns?map(column -> column.columnName)?join(", ")}<#if (table.columns?size > 0 || table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <!-- 业务字段 -->
        ${table.columns?map(column -> column.columnName)?join(", ")}<#if (table.deleteFlagColumn?? || table.versionColumn??)>,</#if>
        <#if table.deleteFlagColumn??>
        <!-- deleteFlag字段（如果存在） -->
        ${table.deleteFlagColumn.columnName}<#if table.versionColumn??>,</#if>
        </#if>
        <#if table.versionColumn??>
        <!-- version字段（如果存在） -->
        ${table.versionColumn.columnName}
        </#if>
        FROM ${table.tableName}
        <where>
            <!-- 主键筛选 -->
            <if test="id != null">
                AND ${table.primaryKey.columnName} = <#noparse>#{</#noparse>id, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse>
            </if>
            <if test="ids != null and ids.size() > 0">
                AND ${table.primaryKey.columnName} IN
                <foreach collection="ids" item="item" open="(" separator="," close=")">
                    <#noparse>#{</#noparse>item, jdbcType=${table.primaryKey.columnType}<#noparse>}</#noparse>
                </foreach>
            </if>
            <!-- 基础字段筛选（createTime和updateTime时间筛选） -->
            <#list table.baseColumns as column>
            <#if column.propertyName == "createTime">
            <if test="${column.propertyName} != null">
                <if test="${column.propertyName}[0] != '' and (${column.propertyName}.size() lt 2 or ${column.propertyName}[1] == '')">
                    AND ${column.columnName} >= <#noparse>#{</#noparse>${column.propertyName}[0], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                </if>
                <if test="${column.propertyName}.size() >= 2 and ${column.propertyName}[0] == '' and ${column.propertyName}[1] != ''">
                    AND ${column.columnName} &lt;= <#noparse>#{</#noparse>${column.propertyName}[1], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                </if>
                <if test="${column.propertyName}.size() >= 2 and ${column.propertyName}[0] != '' and ${column.propertyName}[1] != ''">
                    AND ${column.columnName} BETWEEN <#noparse>#{</#noparse>${column.propertyName}[0], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                    AND <#noparse>#{</#noparse>${column.propertyName}[1], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                </if>
            </if>
            </#if>
            <#if column.propertyName == "updateTime">
            <if test="${column.propertyName} != null">
                <if test="${column.propertyName}[0] != '' and (${column.propertyName}.size() lt 2 or ${column.propertyName}[1] == '')">
                    AND ${column.columnName} >= <#noparse>#{</#noparse>${column.propertyName}[0], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                </if>
                <if test="${column.propertyName}.size() >= 2 and ${column.propertyName}[0] == '' and ${column.propertyName}[1] != ''">
                    AND ${column.columnName} &lt;= <#noparse>#{</#noparse>${column.propertyName}[1], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                </if>
                <if test="${column.propertyName}.size() >= 2 and ${column.propertyName}[0] != '' and ${column.propertyName}[1] != ''">
                    AND ${column.columnName} BETWEEN <#noparse>#{</#noparse>${column.propertyName}[0], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                    AND <#noparse>#{</#noparse>${column.propertyName}[1], jdbcType=TIMESTAMP<#noparse>}</#noparse>
                </if>
            </if>
            </#if>
            </#list>
            <!-- 业务字段筛选 -->
            <#list table.columns as column>
            <if test="${column.propertyName} != null">
                AND ${column.columnName} = <#noparse>#{</#noparse>${column.propertyName}, jdbcType=${column.columnType}<#noparse>}</#noparse>
            </if>
            </#list>
            <#if table.deleteFlagColumn??>
            <!-- 过滤已删除的记录 -->
            AND (${table.deleteFlagColumn.columnName} = 0 OR ${table.deleteFlagColumn.columnName} IS NULL)
            </#if>
        </where>
        <include refid="dynamicOrderBy"/>
    </select>
</mapper>
