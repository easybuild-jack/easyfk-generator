package ${serviceImplPackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${servicePackage}.I${modelInfo.modelName}Service;
import ${repositoryPackage}.I${modelInfo.modelName}Repository;
import ${entityPackage}.${modelInfo.modelName};
<#if createSpringAnnotation>
import org.springframework.stereotype.Service;
</#if>
import com.mcst.easyfk.core.builders.BRBuilder;
import com.mcst.easyfk.core.dto.page.PageResult;
import com.mcst.easyfk.core.dto.page.PageSearch;
import com.mcst.easyfk.core.dto.request.ModifyRequest;
import com.mcst.easyfk.core.dto.request.SearchRequest;
import com.mcst.easyfk.core.dto.response.BaseResult;
import com.mcst.easyfk.core.exception.BusinessException;
import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.core.utils.common.TransformUtil;
import com.mcst.easyfk.orm.influxdb.search.FluxSearchCondition;
import com.mcst.easyfk.orm.influxdb.util.FluxSearchConditionUtil;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public BaseResult<?> save(ModifyRequest<${modelInfo.modelName}Req> param) {
        List<${modelInfo.modelName}Req> paramList = param.getParamList();
        if (EmptyUtil.isNotEmpty(paramList)) {
            List<${modelInfo.modelName}> influxPositions = TransformUtil.jsonTransformList(paramList, ${modelInfo.modelName}.class);
            this.${modelInfo.modelName?uncap_first}Repository.insertBatch(influxPositions, WritePrecision.MS);
        } else {
            ${modelInfo.modelName}Req paramObj = param.getParmaObj();
            if (paramObj != null) {
                ${modelInfo.modelName} influxPosition = TransformUtil.jsonTransform(paramObj, ${modelInfo.modelName}.class);
                this.${modelInfo.modelName?uncap_first}Repository.insert(influxPosition, WritePrecision.MS);
            }
        }
        return BRBuilder.successResult();
    }

    @Override
    public List<${modelInfo.modelName}Resp> queryList(SearchRequest<${modelInfo.modelName}Req> param) {
        FluxSearchCondition condition = FluxSearchConditionUtil.createSearchCondition(param, ${modelInfo.modelName}.class);
        List<${modelInfo.modelName}> influxPositions = this.${modelInfo.modelName?uncap_first}Repository.queryList(condition);
        if (EmptyUtil.isEmpty(influxPositions)) {
            return Collections.emptyList();
        }
        return TransformUtil.transformList(influxPositions, ${modelInfo.modelName}Resp.class);
    }

    @Override
    public long count(SearchRequest<${modelInfo.modelName}Req> param) {
        FluxSearchCondition condition = FluxSearchConditionUtil.createSearchCondition(param, ${modelInfo.modelName}.class);
        return this.${modelInfo.modelName?uncap_first}Repository.count(condition);
    }

    @Override
    public PageResult<${modelInfo.modelName}Resp> queryByPage(SearchRequest<${modelInfo.modelName}Req> param) {
        PageResult<${modelInfo.modelName}Resp> pageResult = new PageResult<${modelInfo.modelName}Resp>().setRows(new ArrayList<>()).setTotal(0L);
        FluxSearchCondition searchConditionBuffer = FluxSearchConditionUtil.createSearchCondition(param, ${modelInfo.modelName}.class);
        List<${modelInfo.modelName}> influxPositions = this.${modelInfo.modelName?uncap_first}Repository.queryList(searchConditionBuffer);
        if (EmptyUtil.isEmpty(influxPositions)) {
            return pageResult;
        }
        PageSearch pageSearch = param.getPageSearch();
        Boolean count = Optional.ofNullable(pageSearch).map(PageSearch::getTotalCount).orElse(false);
        if (count) {
            pageResult.setTotal(${modelInfo.modelName?uncap_first}Repository.count(searchConditionBuffer));
        }
        return pageResult;
    }

    @Override
    public BaseResult<?> delete(ModifyRequest<${modelInfo.modelName}Req> parma) {
        ${modelInfo.modelName}Req paramObj = parma.getParmaObj();
        if (EmptyUtil.isEmpty(paramObj)) {
            throw new BusinessException("具体参数不能为空");
        }
        this.${modelInfo.modelName?uncap_first}Repository.deleteByTimeRange(paramObj.getStart(), paramObj.getStop(),
        TransformUtil.transformObj(paramObj, ${modelInfo.modelName}.class));
        return BRBuilder.successResult();
    }

}
