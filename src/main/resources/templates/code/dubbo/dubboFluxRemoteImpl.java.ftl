package ${remoteImplPackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${servicePackage}.I${modelInfo.modelName}Service;
import ${remotePackage}.I${modelInfo.modelName}Remote;
import com.mcst.easyfk.remote.dubbo.DubboBaseFluxRemoteImpl;
import org.apache.dubbo.config.annotation.DubboService;

/**
* <p>
* ${modelInfo.modelDesc!} RemoteImpl
* </p>
*
* @author ${author}
*/
@DubboService
public class ${modelInfo.modelName}RemoteImpl extends DubboBaseFluxRemoteImpl<I${modelInfo.modelName}Service, ${modelInfo.modelName}Resp,${modelInfo.modelName}Req> implements
I${modelInfo.modelName}Remote {


}
