package ${mapperPackage};

import ${package}.${modelInfo.modelName};
<#if ormType.name() == "MYBATIS_FLEX">
import com.mybatisflex.core.BaseMapper;
<#else>
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
</#if>

/**
 * <p>
 * ${modelInfo.modelDesc!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${modelInfo.modelName}Mapper extends BaseMapper<${modelInfo.modelName}> {

}

