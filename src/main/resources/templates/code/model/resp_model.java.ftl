package ${respPackage};

import ${dtoPackage}.${modelInfo.modelName}Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
* ${modelInfo.modelDesc!}数据响应对象
* </p>
*
* @author ${author}
* @since ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description="${modelInfo.modelDesc!}数据响应对象")
public class ${modelInfo.modelName}Resp extends ${modelInfo.modelName}Dto {


}
