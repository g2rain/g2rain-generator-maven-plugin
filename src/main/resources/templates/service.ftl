package ${config.getServicePackage()};

import ${config.getVoPackage()}.${table.entityName}Vo;
import ${config.getDtoPackage()}.${table.entityName}SelectDto;
import ${config.getDtoPackage()}.${table.entityName}Dto;
import com.g2rain.common.model.PageData;
import java.util.List;

/**
 * ${table.tableComment!''}服务接口
 * 表名: ${table.tableName}
 *
 * @author ${config.getAuthor()}
 */
public interface ${table.entityName}Service {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return VO对象列表
     */
    List<${table.entityName}Vo> selectList(${table.entityName}SelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页VO数据
     */
    PageData<${table.entityName}Vo> selectPage(${table.entityName}SelectDto selectDto);

    /**
     * 新增或更新数据
     *
     * @param dto 数据传输对象
     * @return 操作结果（影响行数）
     */
    ${table.primaryKey.javaType} save(${table.entityName}Dto dto);

    /**
     * 根据ID删除数据
     *
     * @param id 主键ID
     * @return 操作结果（影响行数）
     */
    int delete(${table.primaryKey.javaType} id);
}