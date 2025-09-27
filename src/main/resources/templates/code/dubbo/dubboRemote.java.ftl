package ${remotePackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import com.mcst.easyfk.remote.dubbo.IDubboBaseRemote;

/**
* <p>
* ${modelInfo.modelDesc!} Remote接口类
* </p>
*
* @author ${author}
* @since ${date}
*/
public interface I${modelInfo.modelName}Remote extends IDubboBaseRemote<${modelInfo.modelName}Resp,${modelInfo.idType},${modelInfo.modelName}Req>{


}