package ${repositoryImpPackage};

import ${repositoryPackage}.I${modelInfo.modelName}Repository;
import com.mcst.easyfk.orm.influxdb.repository.BaseFluxRepositoryImpl;
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
public class ${modelInfo.modelName}RepositoryImpl extends BaseFluxRepositoryImpl<${modelInfo.modelName}> implements I${modelInfo.modelName}Repository {


}