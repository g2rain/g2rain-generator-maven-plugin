package ${config.getServicePackage()}.impl;

import com.g2rain.common.exception.SystemErrorCode;
import com.g2rain.common.id.IdGenerator;
import com.g2rain.common.model.PageData;
import com.g2rain.common.utils.Asserts;
import ${config.getConverterPackage()}.${table.entityName}Converter;
import ${config.getDaoPackage()}.${table.entityName}Dao;
import ${config.getPoPackage()}.${table.entityName}Po;
import ${config.getDtoPackage()}.${table.entityName}Dto;
import ${config.getDtoPackage()}.${table.entityName}SelectDto;
import ${config.getServicePackage()}.${table.entityName}Service;
import ${config.getVoPackage()}.${table.entityName}Vo;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * ${table.tableComment!''}服务实现类
 * 表名: ${table.tableName}
 *
 * @author ${config.getAuthor()}
 */
@Service(value = "${table.entityNameLower}ServiceImpl")
public class ${table.entityName}ServiceImpl implements ${table.entityName}Service {

    @Resource(name = "${table.entityNameLower}Dao")
    private ${table.entityName}Dao ${table.entityNameLower}Dao;

    private IdGenerator idGenerator;

    @Qualifier("idGenerator")
    @Autowired(required = false)
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public List<${table.entityName}Vo> selectList(${table.entityName}SelectDto selectDto) {
        return ${table.entityNameLower}Dao.selectList(selectDto)
                .stream()
                .map(${table.entityName}Converter.INSTANCE::po2vo)
                .toList();
    }

    @Override
    public PageData<${table.entityName}Vo> selectPage(${table.entityName}SelectDto selectDto) {
        try (Page<${table.entityName}Po> page = PageMethod.startPage(selectDto.getPageNum(), selectDto.getPageSize())) {
            ${table.entityNameLower}Dao.selectList(selectDto);
            List<${table.entityName}Vo> result = page.getResult()
                    .stream()
                    .map(${table.entityName}Converter.INSTANCE::po2vo)
                    .toList();
            return PageData.of(page.getPageNum(), page.getPageSize(), page.getTotal(), result);
        }
    }

    @Override
    public ${table.primaryKey.javaType} save(${table.entityName}Dto dto) {
        // 转换DTO为PO
        ${table.entityName}Po entity = ${table.entityName}Converter.INSTANCE.dto2po(dto);

        // 判断是新增还是更新
        ${table.primaryKey.javaType} id = entity.get${table.primaryKey.propertyName?cap_first}();
        if (Objects.isNull(id) || id == 0) {
            // 新增：使用IdGenerator生成主键
            entity.set${table.primaryKey.propertyName?cap_first}(idGenerator.generateId());
            int success = ${table.entityNameLower}Dao.insert(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.CREATE_DATA_ERROR);
        } else {
            // 更新：直接更新
            int success = ${table.entityNameLower}Dao.update(entity);
            Asserts.greaterThan(success, 0, SystemErrorCode.UPDATE_DATA_ERROR, id);
        }

        return entity.get${table.primaryKey.propertyName?cap_first}();
    }

    @Override
    public int delete(${table.primaryKey.javaType} id) {
        return ${table.entityNameLower}Dao.delete(id);
    }
}