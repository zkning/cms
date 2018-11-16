package ${packageDir}.ctrl;

import ${packageDir}.model.${entity}EditModel;
import ${packageDir}.model.${entity}SearchModel;
import ${packageDir}.model.${entity}FetchModel;
import ${packageDir}.model.Pager;
import ${packageDir}.response.Response;
import ${packageDir}.service.${entity}Service;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import javax.validation.Valid;

@RestController
@RequestMapping("/${entity}")
public class ${entity}Controller {

    @Autowired
    ${entity}Service ${entityVar}Service;

    @ApiModelProperty(value = "编辑")
    @PostMapping(value = "/edit")
    public Response edit(@RequestBody @Valid ${entity}EditModel ${entityVar}EditModel) {
        return ${entityVar}Service.edit(${entityVar}EditModel);
    }

    @ApiModelProperty(value = "删除")
    @GetMapping(value = "/delete")
    public Response delete(${idType} id) {
        return ${entityVar}Service.delete(id);
    }

    @ApiOperation(value = "查询")
    @GetMapping(value = "/fetch")
    public Response<${entity}FetchModel> fetch(${idType} id) {
        return ${entityVar}Service.fetch(id);
    }


    @ApiOperation(value = "查询列表")
    @GetMapping(value = "/list")
    public Response<Pager<${entity}FetchModel>> list(${entity}SearchModel ${entityVar}SearchModel) {
        return Response.SUCCESS(${entityVar}Service.list(${entityVar}SearchModel));
    }
}
