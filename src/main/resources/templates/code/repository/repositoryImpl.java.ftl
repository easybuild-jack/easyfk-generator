package ${repositoryImpPackage};

import ${dtoPackage}.${modelInfo.modelName}Dto;
import ${repositoryPackage}.I${modelInfo.modelName}Repository;
import com.mcst.easyfk.service.mybatisplus.impl.BaseMyBatisRepositoryImpl;
import ${mapperPackage}.${modelInfo.modelName}Mapper;
import ${entityPackage}.${modelInfo.modelName};
<#if createSpringAnnotation == true>
import org.springframework.stereotype.Repository;
</#if>
/**
* <p>
* ${modelInfo.modelDesc!} Repository实现类
* </p>
*
* @author ${author}
* @since ${date}
*/
<#if createSpringAnnotation == true>
@Repository
</#if>
public class ${modelInfo.modelName}RepositoryImpl extends BaseMyBatisRepositoryImpl<${modelInfo.modelName}Mapper,${modelInfo.modelName}Dto, ${modelInfo.modelName},${modelInfo.idType}> implements I${modelInfo.modelName}Repository {


}