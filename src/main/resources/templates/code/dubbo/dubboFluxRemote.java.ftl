package ${remotePackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import com.mcst.easyfk.remote.dubbo.IDubboBaseFluxRemote;

/**
* <p>
* ${modelInfo.modelDesc!} Remote接口类
* </p>
*
* @author ${author}
* @since ${date}
*/
public interface I${modelInfo.modelName}Remote extends IDubboBaseFluxRemote<${modelInfo.modelName}Resp,${modelInfo.modelName}Req>{


}