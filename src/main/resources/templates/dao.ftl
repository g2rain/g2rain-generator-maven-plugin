package ${config.getDaoPackage()};

import ${config.getPoPackage()}.${table.entityName}Po;
import ${config.getDtoPackage()}.${table.entityName}SelectDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ${table.tableComment!''}数据访问接口
 * 表名: ${table.tableName}
 *
 * @author ${config.getAuthor()}
 */
@Mapper
public interface ${table.entityName}Dao {

    /**
     * 插入单条记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int insert(${table.entityName}Po entity);

    /**
     * 批量插入记录
     *
     * @param list 实体对象列表
     * @return 影响行数
     */
    int insertMultiple(List<${table.entityName}Po> list);

    /**
     * 根据ID更新记录
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    int update(${table.entityName}Po entity);

    /**
     * 根据ID删除记录
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(${table.primaryKey.javaType} id);
    <#if table.versionColumn??>

    /**
     * 根据ID和Version更新记录（乐观锁更新）
     *
     * @param entity 实体对象（必须包含version字段）
     * @return 影响行数
     */
    int updateByVersion(${table.entityName}Po entity);
    </#if>

    /**
     * 根据ID查询记录
     *
     * @param id 主键ID
     * @return 实体对象
     */
    ${table.entityName}Po selectById(${table.primaryKey.javaType} id);

    /**
     * 根据查询入参DTO筛选列表
     *
     * @param selectDto 查询条件DTO
     * @return 实体对象列表
     */
    List<${table.entityName}Po> selectList(${table.entityName}SelectDto selectDto);
}