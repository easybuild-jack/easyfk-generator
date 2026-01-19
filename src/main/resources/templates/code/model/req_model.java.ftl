package ${reqPackage};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ${paramPackage}.${modelInfo.modelName}Param;

/**
* <p>
* ${modelInfo.modelDesc!}数据请求对象
* </p>
*
* @author ${author}
* @since ${date}
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description="${modelInfo.modelDesc!}数据请求对象")
@SuperBuilder
@NoArgsConstructor
public class ${modelInfo.modelName}Req extends ${modelInfo.modelName}Param {


}
