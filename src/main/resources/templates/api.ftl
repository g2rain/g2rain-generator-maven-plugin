package ${config.getApiPackage()};

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import ${config.getDtoPackage()}.${table.entityName}SelectDto;
import ${config.getVoPackage()}.${table.entityName}Vo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * ${table.tableComment!''}API接口
 * 表名: ${table.tableName}
 *
 * @author ${config.getAuthor()}
 */
@Tag(name = "${table.tableComment!''}", description = "${table.tableName}相关接口")
public interface ${table.entityName}Api {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询${table.tableName}列表", description = "根据查询条件返回${table.tableName}列表")
    Result<List<${table.entityName}Vo>> selectList(${table.entityName}SelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询${table.tableName}列表", description = "分页查询${table.tableName}列表")
    Result<PageData<${table.entityName}Vo>> selectPage(PageSelectListDto<${table.entityName}SelectDto> selectDto);
}