package ${apiBasePackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import com.mcst.easyfk.service.api.IBaseApi;

/**
* <p>
* ${modelInfo.modelDesc!} 服务接口类
* </p>
*
* @author ${author}
* @since ${date}
*/
public interface I${modelInfo.modelName}Api extends IBaseApi<${modelInfo.modelName}Resp,${modelInfo.idType},${modelInfo.modelName}Req> {


}