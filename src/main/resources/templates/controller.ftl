package ${config.getControllerPackage()};

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import ${config.getApiPackage()}.${table.entityName}Api;
import ${config.getDtoPackage()}.${table.entityName}Dto;
import ${config.getDtoPackage()}.${table.entityName}SelectDto;
import ${config.getServicePackage()}.${table.entityName}Service;
import ${config.getVoPackage()}.${table.entityName}Vo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ${table.tableComment!''}控制器
 * 表名: ${table.tableName}
 *
 * @author ${config.getAuthor()}
 */
@RestController
@RequestMapping("/${table.tableName}")
public class ${table.entityName}Controller implements ${table.entityName}Api {

    @Resource(name = "${table.entityNameLower}ServiceImpl")
    private ${table.entityName}Service ${table.entityNameLower}Service;

    @Override
    public Result<List<${table.entityName}Vo>> selectList(${table.entityName}SelectDto selectDto) {
        return Result.success(${table.entityNameLower}Service.selectList(selectDto));
    }

    @Override
    public Result<PageData<${table.entityName}Vo>> selectPage(PageSelectListDto<${table.entityName}SelectDto> selectDto) {
        return Result.successPage(${table.entityNameLower}Service.selectPage(selectDto));
    }

    @PostMapping("/save")
    @Operation(summary = "新增或更新${table.tableName}信息", description = "新增或更新${table.tableName}基础信息")
    public Result<${table.primaryKey.javaType}> save(@RequestBody ${table.entityName}Dto dto) {
        return Result.success(${table.entityNameLower}Service.save(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${table.tableName}记录", description = "根据主键删除${table.tableName}记录")
    public Result<Integer> delete(@Parameter(description = "${table.tableName}标识") @PathVariable ${table.primaryKey.javaType} id) {
        return Result.success(${table.entityNameLower}Service.delete(id));
    }
}