package ${repositoryPackage};

import ${dtoPackage}.${modelInfo.modelName}Dto;
import com.mcst.easyfk.repository.api.IBaseRepository;
/**
* <p>
* ${modelInfo.modelDesc!} Repository接口
* </p>
*
* @author ${author}
* @since ${date}
*/
public interface I${modelInfo.modelName}Repository extends IBaseRepository<${modelInfo.modelName}Dto,${modelInfo.idType}> {

}
