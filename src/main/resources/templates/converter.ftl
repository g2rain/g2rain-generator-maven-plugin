package ${config.getConverterPackage()};

import com.g2rain.common.converter.CommonConverter;
import ${config.getPoPackage()}.${table.entityName}Po;
import ${config.getDtoPackage()}.${table.entityName}Dto;
import ${config.getVoPackage()}.${table.entityName}Vo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * ${table.tableComment!''}转换器
 * 用于Po、Vo、Dto之间的相互转换
 * 表名: ${table.tableName}
 *
 * @author ${config.getAuthor()}
 */
@Mapper(uses = CommonConverter.class)
public interface ${table.entityName}Converter {

    /**
     * 单例实例，通过 {@link Mappers#getMapper(Class)} 获取 MapStruct 自动生成的实现。
     */
    ${table.entityName}Converter INSTANCE = Mappers.getMapper(${table.entityName}Converter.class);

    /**
     * Po -> Vo
     * 自动将 createTime 和 updateTime 从 {@link LocalDateTime} 转换为 {@link String}
     */
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "localDateTimeToString")
    ${table.entityName}Vo po2vo(${table.entityName}Po po);

    /**
     * Dto -> Po
     * 自动将 createTime 和 updateTime 从 {@link String} 转换为 {@link LocalDateTime}
     * 忽略 version 字段
     */
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateTime", source = "updateTime", qualifiedByName = "stringToLocalDateTime")
    ${table.entityName}Po dto2po(${table.entityName}Dto dto);
}
