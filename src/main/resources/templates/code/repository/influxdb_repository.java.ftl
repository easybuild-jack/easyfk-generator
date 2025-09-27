package ${repositoryPackage};

import com.mcst.easyfk.orm.influxdb.repository.IBaseFluxRepository;
import ${entityPackage}.${modelInfo.modelName};
/**
* <p>
* ${modelInfo.modelDesc!} Repository接口
* </p>
*
* @author ${author}
* @since ${date}
*/
public interface I${modelInfo.modelName}Repository extends IBaseFluxRepository<${modelInfo.modelName}> {

}
