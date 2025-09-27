package ${remotePackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import com.mcst.easyfk.remote.sc.IBaseFluxRemote;
import org.springframework.cloud.openfeign.FeignClient;

/**
* <p>
* ${modelInfo.modelDesc!} Remote接口类
* </p>
*
* @author ${author}
* @since ${date}
*/
@FeignClient(value = "${scApiServiceId}", path = "${scApiBasePath}<#if moduleName??>/${moduleName}/${modelInfo.modelName?uncap_first}<#else>/${modelInfo.modelName?uncap_first}</#if>")
public interface I${modelInfo.modelName}Remote extends IBaseFluxRemote<${modelInfo.modelName}Resp,${modelInfo.modelName}Req>{


}