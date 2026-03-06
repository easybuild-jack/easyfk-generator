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

/**
* ${modelInfo.modelDesc!}相关接口
* @author ${author}
* @since ${date}
*/
@Tag(name = "${modelInfo.modelDesc!}相关接口", description = "${modelInfo.modelDesc!}相关接口")
@RestController
@RequestMapping("/api/${moduleName?uncap_first}/${modelInfo.modelName?uncap_first}")
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
    public ResponseResult<List<${modelInfo.modelName}Resp>> queryPage(@Validated ${modelInfo.modelName}Req ${modelInfo.modelName?uncap_first}Req) {
        SearchRequest<${modelInfo.modelName}Req> requestParam = SRPBuilder.buildRequest(${modelInfo.modelName?uncap_first}Req);
        return RRBuilder.buildBodyByPageResult(this.${modelInfo.modelName?uncap_first}Api.queryByPage(requestParam));
    }

    @Operation(summary = "新增/编辑", description = "新增/编辑")
    @PostMapping("/addOrEdit")
    public ResponseResult<?> addOrEdit(@RequestBody ${modelInfo.modelName}EditReq ${modelInfo.modelName?uncap_first}EditReq) {
        ModifyRequest<${modelInfo.modelName}Req> requestParam = MRPBuilder.buildRequest(${modelInfo.modelName?uncap_first}EditReq,${modelInfo.modelName}Req.class);
        return RRBuilder.buildBodyByBaseResult(this.${modelInfo.modelName?uncap_first}Api.save(requestParam));
    }

    @Operation(summary = "批量删除", description = "批量删除")
    @PostMapping("/delete")
    public ResponseResult<?> delete(@Validated @RequestBody BatchBasicReq<${modelInfo.idType}> batchBase) {
        List<${modelInfo.idType}> idArray = SplitUtil.split(batchBase.getIds());
        ModifyRequest<${modelInfo.modelName}Req> requestParam = MRPBuilder.<${modelInfo.modelName}Req>builder().ids(idArray).build();
        return RRBuilder.buildBodyByBaseResult(this.${modelInfo.modelName?uncap_first}Api.delete(requestParam));
    }

}
