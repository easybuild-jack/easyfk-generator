package ${serverApiImplPackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${apiBasePackage}.I${modelInfo.modelName}Api;
import com.mcst.easyfk.service.base.BaseApiServiceImpl;
import ${servicePackage}.I${modelInfo.modelName}Service;
<#if createSpringAnnotation>
import org.springframework.stereotype.Service;
</#if>
/**
* <p>
* ${modelInfo.modelDesc!} Api接口Server层实现
* </p>
*
* @author ${author}
*/
<#if createSpringAnnotation>
@Service
</#if>
public class ${modelInfo.modelName}ApiServerImpl extends BaseApiServiceImpl<I${modelInfo.modelName}Service, ${modelInfo.modelName}Resp,${modelInfo.idType},${modelInfo.modelName}Req> implements I${modelInfo.modelName}Api {

}
