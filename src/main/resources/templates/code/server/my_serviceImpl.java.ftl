package ${serviceImplPackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${dtoPackage}.${modelInfo.modelName}Dto;
import ${servicePackage}.I${modelInfo.modelName}Service;
import ${repositoryPackage}.I${modelInfo.modelName}Repository;
<#if createSpringAnnotation>
import org.springframework.stereotype.Service;
</#if>
import com.mcst.easyfk.core.dto.page.PageResult;
import com.mcst.easyfk.core.dto.request.SearchRequest;
import com.mcst.easyfk.core.dto.request.ModifyRequest;
import com.mcst.easyfk.core.dto.response.BaseResult;
import com.mcst.easyfk.repository.util.ConditionUtil;
import java.util.List;
import jakarta.annotation.Resource;
import com.mcst.easyfk.service.util.ServiceUtil;
import com.mcst.easyfk.core.utils.common.TransformUtil;
/**
* <p>
* ${modelInfo.modelDesc!} 服务实现类
* </p>
*
* @author ${author}
* @since ${date}
*/

<#if createSpringAnnotation>
@Service
</#if>
public class ${modelInfo.modelName}ServiceImpl  implements I${modelInfo.modelName}Service {
    @Resource
    private I${modelInfo.modelName}Repository ${modelInfo.modelName?uncap_first}Repository;

    @Override
    public ${modelInfo.modelName}Resp queryById(${modelInfo.idType} id, String... selectColumns) {
        return TransformUtil.transformObj(this.${modelInfo.modelName?uncap_first}Repository.queryById(id, selectColumns), ${modelInfo.modelName}Resp.class);
    }

    @Override
    public ${modelInfo.modelName}Resp queryOneByField(String field, Object value, String... selectColumns) {
        return TransformUtil.transformObj(this.${modelInfo.modelName?uncap_first}Repository.queryOneByField(field, value, selectColumns), ${modelInfo.modelName}Resp.class);
    }

    @Override
    public List<${modelInfo.modelName}Resp> queryList(SearchRequest<${modelInfo.modelName}Req> param) {
        return TransformUtil.transformListByStream(this.${modelInfo.modelName?uncap_first}Repository.queryByCondition(ConditionUtil.conditionByRequest(param)), ${modelInfo.modelName}Resp.class);
    }

    @Override
    public ${modelInfo.modelName}Resp queryOne(SearchRequest<${modelInfo.modelName}Req> param) {
        return TransformUtil.transformObj(this.${modelInfo.modelName?uncap_first}Repository.queryOneByCondition(ConditionUtil.conditionByRequest(param)), ${modelInfo.modelName}Resp.class);
    }

    @Override
    public long count(SearchRequest<${modelInfo.modelName}Req> param) {
        return this.${modelInfo.modelName?uncap_first}Repository.countByCondition(ConditionUtil.conditionByRequest(param));
    }

    @Override
    public boolean exists(SearchRequest<${modelInfo.modelName}Req> param) {
        return this.${modelInfo.modelName?uncap_first}Repository.exists(ConditionUtil.conditionByRequest(param));
    }

    @Override
    public PageResult<${modelInfo.modelName}Resp> queryByPage(SearchRequest<${modelInfo.modelName}Req> param) {
        return TransformUtil.transformPageResult(this.${modelInfo.modelName?uncap_first}Repository.queryByPage(ConditionUtil.conditionByRequest(param)),${modelInfo.modelName}Resp.class);
    }

    @Override
    public BaseResult<?> save(ModifyRequest<${modelInfo.modelName}Req> param) {
        return ServiceUtil.save(param, ${modelInfo.modelName?uncap_first}Repository, ${modelInfo.modelName}Dto.class);
    }

    @Override
    public BaseResult<?> delete(ModifyRequest<${modelInfo.modelName}Req> param) {
        return ServiceUtil.delete(param, ${modelInfo.modelName?uncap_first}Repository, ${modelInfo.modelName}Dto.class);
    }

    @Override
    public ${modelInfo.idType} insertAndReturnId(${modelInfo.modelName}Req param) {
        return ServiceUtil.insertAndReturnId(param, ${modelInfo.modelName?uncap_first}Repository, ${modelInfo.modelName}Dto.class);
    }

}
