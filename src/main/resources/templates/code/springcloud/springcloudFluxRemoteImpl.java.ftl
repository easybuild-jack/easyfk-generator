package ${remoteImplPackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${servicePackage}.I${modelInfo.modelName}Service;
import ${remotePackage}.I${modelInfo.modelName}Remote;
import com.mcst.easyfk.remote.sc.BaseFluxRemoteImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* ${modelInfo.modelDesc!} RemoteImpl
* </p>
*
* @author ${author}
*/
@RestController
@RequestMapping("${scApiBasePath}<#if moduleName??>/${moduleName}/${modelInfo.modelName?uncap_first}<#else>/${modelInfo.modelName?uncap_first}</#if>")
public class ${modelInfo.modelName}RemoteImpl extends BaseFluxRemoteImpl<I${modelInfo.modelName}Service, ${modelInfo.modelName}Resp,${modelInfo.modelName}Req> implements I${modelInfo.modelName}Remote {


}
