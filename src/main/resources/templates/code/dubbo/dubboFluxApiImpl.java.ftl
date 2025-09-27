package ${dubboApiImplPackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${apiBasePackage}.I${modelInfo.modelName}Api;
import com.mcst.easyfk.remote.dubbo.DubboBaseFluxApiRemoteImpl;
import ${remotePackage}.I${modelInfo.modelName}Remote;
<#if createSpringAnnotation>
import org.springframework.stereotype.Service;
</#if>
import jakarta.annotation.Resource;
/**
* <p>
* ${modelInfo.modelDesc!} Api接口Dubbo层实现
* </p>
*
* @author ${author}
* @since ${date}
*/
<#if createSpringAnnotation>
@Service
</#if>
public class ${modelInfo.modelName}ApiScImpl extends DubboBaseFluxApiRemoteImpl<I${modelInfo.modelName}Remote, ${modelInfo.modelName}Resp,${modelInfo.modelName}Req> implements
I${modelInfo.modelName}Api {

}
