package com.fast.admin.rbac.ctrl;


import com.fast.admin.framework.response.Response;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.rbac.domain.Group;
import com.fast.admin.rbac.model.DictEditModel;
import com.fast.admin.rbac.model.DictFetchModel;
import com.fast.admin.rbac.model.DictSearchModel;
import com.fast.admin.rbac.model.TreeNodeModel;
import com.fast.admin.rbac.service.DictService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    DictService dictService;


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('dict_edit')")
    @ApiModelProperty(value = "编辑")
    @PostMapping(value = "/edit")
    public Response<Group> edit(@RequestBody @Valid DictEditModel dictEditModel) {
        return dictService.edit(dictEditModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('dict_delete')")
    @ApiModelProperty(value = "删除")
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return dictService.delete(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('dict_fetch')")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/fetch")
    public Response<DictFetchModel> fetch(Long id) {
        return dictService.fetch(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('dict_list')")
    @ApiOperation(value = "查询列表")
    @GetMapping(value = "/list")
    public Response<Pager<DictFetchModel>> list(DictSearchModel dictSearchModel) {
        return Response.SUCCESS(dictService.list(dictSearchModel));
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('dict_tree')")
    @ApiOperation(value = "字典树")
    @GetMapping(value = "/tree")
    public Response<List<TreeNodeModel>> tree() {
        return Response.SUCCESS(dictService.treeNodes());
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('dict_findByPValue')")
    @ApiOperation(value = "根据上级值查询所有子项")
    @GetMapping(value = "/findByPValue")
    public Response<List<DictFetchModel>> findByPValue(String value) {
        return Response.SUCCESS(dictService.findByPValue(value));
    }
}
