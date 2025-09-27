package ${servicePackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import com.mcst.easyfk.service.api.IBaseService;

/**
* <p>
* ${modelInfo.modelDesc!} 服务接口类
* </p>
*
* @author ${author}
* @since ${date}
*/
public interface I${modelInfo.modelName}Service extends IBaseService<${modelInfo.modelName}Resp,${modelInfo.idType},${modelInfo.modelName}Req> {


}