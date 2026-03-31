package ${remoteImplPackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${servicePackage}.I${modelInfo.modelName}Service;
import ${remotePackage}.I${modelInfo.modelName}Remote;
import com.mcst.easyfk.remote.dubbo.DubboBaseRemoteImpl;

/**
* <p>
* ${modelInfo.modelDesc!} RemoteImpl
* </p>
*
* @author ${author}
*/
public class ${modelInfo.modelName}RemoteImpl extends DubboBaseRemoteImpl<I${modelInfo.modelName}Service, ${modelInfo.modelName}Resp,${modelInfo.idType},${modelInfo.modelName}Req> implements I${modelInfo.modelName}Remote {


}
