package ${repositoryImpPackage};

import ${dtoPackage}.${modelInfo.modelName}Dto;
import ${repositoryPackage}.I${modelInfo.modelName}Repository;
<#if ormType.name() == "MYBATIS_FLEX">
import com.mcst.easyfk.service.flex.impl.BaseFlexRepositoryImpl;
<#elseif ormType.name() == "HIBERNATE">
import com.mcst.easyfk.service.hibernate.impl.BaseHibernateRepositoryImpl;
<#else>
import com.mcst.easyfk.service.mybatisplus.impl.BaseMyBatisRepositoryImpl;
</#if>
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
<#if ormType.name() == "MYBATIS_FLEX">
public class ${modelInfo.modelName}RepositoryImpl extends BaseFlexRepositoryImpl<${modelInfo.modelName}Mapper,${modelInfo.modelName}Dto, ${modelInfo.modelName},${modelInfo.idType}> implements I${modelInfo.modelName}Repository {
<#elseif ormType.name() == "HIBERNATE">
public class ${modelInfo.modelName}RepositoryImpl extends BaseHibernateRepositoryImpl<${modelInfo.modelName}Mapper,${modelInfo.modelName}Dto, ${modelInfo.modelName},${modelInfo.idType}> implements I${modelInfo.modelName}Repository {
<#else>
public class ${modelInfo.modelName}RepositoryImpl extends BaseMyBatisRepositoryImpl<${modelInfo.modelName}Mapper,${modelInfo.modelName}Dto, ${modelInfo.modelName},${modelInfo.idType}> implements I${modelInfo.modelName}Repository {
</#if>


}