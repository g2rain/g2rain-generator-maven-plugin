package ${config.getControllerPackage()};

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.Result;
import ${config.getApiPackage()}.${table.entityName}Api;
import ${config.getDtoPackage()}.${table.entityName}Dto;
import ${config.getDtoPackage()}.${table.entityName}SelectDto;
import ${config.getServicePackage()}.${table.entityName}Service;
import ${config.getVoPackage()}.${table.entityName}Vo;
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
    public Result<PageData<${table.entityName}Vo>> selectPage(${table.entityName}SelectDto selectDto) {
        return Result.successPage(${table.entityNameLower}Service.selectPage(selectDto));
    }

    @PostMapping("/save")
    public Result<${table.primaryKey.javaType}> save(@RequestBody ${table.entityName}Dto dto) {
        return Result.success(${table.entityNameLower}Service.save(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable ${table.primaryKey.javaType} id) {
        return Result.success(${table.entityNameLower}Service.delete(id));
    }
}