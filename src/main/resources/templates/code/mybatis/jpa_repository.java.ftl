package ${mapperPackage};

import ${package}.${modelInfo.modelName};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <p>
 * ${modelInfo.modelDesc!} JPA Repository 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${modelInfo.modelName}Mapper extends JpaRepository<${modelInfo.modelName}, ${modelInfo.idType}>, JpaSpecificationExecutor<${modelInfo.modelName}> {

}
