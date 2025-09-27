package ${apiBasePackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import com.mcst.easyfk.service.api.IBaseFluxApi;

/**
* <p>
* ${modelInfo.modelDesc!} 服务接口类
* </p>
*
* @author ${author}
* @since ${date}
*/
public interface I${modelInfo.modelName}Api extends IBaseFluxApi<${modelInfo.modelName}Resp,${modelInfo.modelName}Req> {


}