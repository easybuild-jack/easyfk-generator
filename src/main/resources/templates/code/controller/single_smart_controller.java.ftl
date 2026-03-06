package ${controllerBasePackage};

import ${respPackage}.${modelInfo.modelName}Resp;
import ${reqPackage}.${modelInfo.modelName}Req;
import ${reqPackage}.${modelInfo.modelName}EditReq;
import ${apiBasePackage}.I${modelInfo.modelName}Api;
import com.mcst.easyfk.core.dto.request.ModifyRequest;
import com.mcst.easyfk.core.builders.MRPBuilder;
import com.mcst.easyfk.core.dto.request.SearchRequest;
import com.mcst.easyfk.core.builders.SRPBuilder;
import com.mcst.easyfk.core.dto.response.ResponseResult;
import com.mcst.easyfk.core.builders.RRBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.mcst.easyfk.core.dto.request.BatchBasicReq;
import com.mcst.easyfk.core.utils.common.SplitUtil;
import jakarta.annotation.Resource;
import java.util.List;
<#if createResourceAnnotation>
import com.mcst.easyfk.authority.annotation.AuthResource;
import com.mcst.easyfk.authority.annotation.ResourceController;
import com.mcst.easyfk.authority.enums.ResourceCategory;
</#if>
<#if modelInfo.forbiddenFiled??>
import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.core.dto.request.DisableFieldReq;
import com.mcst.easyfk.core.utils.common.DisableUtil;
import com.mcst.easyfk.core.dto.request.DisableActionParam;
</#if>

/**
 * ${modelInfo.modelDesc!}相关接口
 * @author ${author}
 * @since ${date}
 */
@Tag(name = "${modelInfo.modelDesc!}相关接口", description = "${modelInfo.modelDesc!}相关接口")
@RestController
@RequestMapping("/api/${moduleName?uncap_first}/${modelInfo.modelName?uncap_first}")
<#if createResourceAnnotation>
<#assign resourceSort = modelInfo.resourceSort>
<#assign modelSort = modelInfo.modelResourceSort>
<#assign editSort = modelSort + 1>
<#assign deleteSort = modelSort + 2>
<#assign enableSort = modelSort + 3>
@ResourceController(group = "${modelInfo.resourceGroup}", id = "${modelInfo.resourceId}", name = "${modelInfo.resourceName}", sort = ${resourceSort?c}, path = "${modelInfo.resourcePath}")
</#if>
public class ${modelInfo.modelName}Controller {
    @Resource
    private I${modelInfo.modelName}Api ${modelInfo.modelName?uncap_first}Api;


    @Operation(summary = "详情")
    @GetMapping("/detail")
    public ResponseResult<${modelInfo.modelName}Resp> detail(${modelInfo.idType} id) {
        return RRBuilder.buildSuccessBody(this.${modelInfo.modelName?uncap_first}Api.queryById(id));
    }

    @Operation(summary = "分页查询", description = "分页查询")
    @GetMapping("/queryPage")
    <#if createResourceAnnotation>
    @AuthResource(id = "${modelInfo.modelName?uncap_first}Manage", name = "${modelInfo.modelDesc!}管理", pId = "${modelInfo.resourceId}", pName = "${modelInfo.resourceName}", sort= ${modelSort?c}, path = "${modelInfo.resourcePath}/${modelInfo.modelName?uncap_first}",
        category = ResourceCategory.menu)
    </#if>
    public ResponseResult<List<${modelInfo.modelName}Resp>> queryPage(@Validated ${modelInfo.modelName}Req ${modelInfo.modelName?uncap_first}Req) {
        SRPBuilder<${modelInfo.modelName}Req> builder = SRPBuilder.<${modelInfo.modelName}Req>builder().example(${modelInfo.modelName?uncap_first}Req);
        return RRBuilder.buildBodyByPageResult(this.${modelInfo.modelName?uncap_first}Api.queryByPage(builder.build()));
    }

    @Operation(summary = "新增/编辑", description = "新增/编辑")
    @PostMapping("/addOrEdit")
    <#if createResourceAnnotation>
    @AuthResource(id = "${modelInfo.modelName?uncap_first}AddOrEdit", name = "新增/编辑", title = "新增/编辑", pId = "${modelInfo.modelName?uncap_first}Manage", pName = "${modelInfo.modelDesc!}管理", sort = ${editSort?c}, category = ResourceCategory.action,
        actionCode = "addOrEdit")
    </#if>
    public ResponseResult<?> addOrEdit(@RequestBody ${modelInfo.modelName}EditReq ${modelInfo.modelName?uncap_first}EditReq) {
        ModifyRequest<${modelInfo.modelName}Req> requestParam = MRPBuilder.buildRequest(${modelInfo.modelName?uncap_first}EditReq, ${modelInfo.modelName}Req.class);
        return RRBuilder.buildBodyByBaseResult(this.${modelInfo.modelName?uncap_first}Api.save(requestParam));
    }

    @Operation(summary = "批量删除", description = "批量删除")
    @PostMapping("/delete")
    <#if createResourceAnnotation>
    @AuthResource(id = "${modelInfo.modelName?uncap_first}Delete", name = "删除", title = "删除", pId = "${modelInfo.modelName?uncap_first}Manage", pName = "${modelInfo.modelDesc!}管理", sort = ${deleteSort?c}, category = ResourceCategory.action,
        actionCode = "delete")
    </#if>
    public ResponseResult<?> delete(@Validated @RequestBody BatchBasicReq<${modelInfo.idType}> batchBase) {
        List<${modelInfo.idType}> idArray = SplitUtil.split(batchBase.getIds());
        ModifyRequest<${modelInfo.modelName}Req> requestParam = MRPBuilder.<${modelInfo.modelName}Req>builder().ids(idArray).build();
        return RRBuilder.buildBodyByBaseResult(this.${modelInfo.modelName?uncap_first}Api.delete(requestParam));
    }

    <#if modelInfo.forbiddenFiled??>
    @Operation(summary = "启用/禁用")
    @PostMapping("/disable")
    <#if createResourceAnnotation>
    @AuthResource(id = "${modelInfo.modelName?uncap_first}Disable", name = "启用/禁用", title = "启用/禁用", pId = "${modelInfo.modelName?uncap_first}Manage", pName = "${modelInfo.modelDesc!}管理", sort = ${enableSort?c}, category = ResourceCategory.action,
       actionCode = "disable")
    </#if>
    public ResponseResult<?> disable(@RequestBody @Validated DisableFieldReq disableFieldVO) {
       DisableActionParam<${modelInfo.modelName}Req> disableActionParam =
            new DisableActionParam<${modelInfo.modelName}Req>(disableFieldVO).setIdField(${modelInfo.modelName}Req::get${modelInfo.modelName}Id).setValueField(${modelInfo.modelName}Req::get${modelInfo.forbiddenFiled?cap_first}).setTClass(${modelInfo.modelName}Req.class);
        List<${modelInfo.modelName}Req> list = DisableUtil.createObjListByDisableAction(disableActionParam);
        if (EmptyUtil.isEmpty(list)) {
          return RRBuilder.buildFailedBody("参数错误");
        }
        ModifyRequest<${modelInfo.modelName}Req> requestParam = MRPBuilder.<${modelInfo.modelName}Req>builder().paramList(list).build();
        return RRBuilder.buildBodyByBaseResult(this.${modelInfo.modelName?uncap_first}Api.save(requestParam));
    }
    </#if>
}
