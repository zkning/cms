package com.sophia.cms.rbac.ctrl;


import com.sophia.cms.framework.response.Response;
import com.sophia.cms.rbac.domain.Group;
import com.sophia.cms.rbac.model.GroupEditModel;
import com.sophia.cms.rbac.model.GroupSearchModel;
import com.sophia.cms.rbac.model.TreeNodeModel;
import com.sophia.cms.rbac.service.GroupService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('group_edit')")
    @ApiModelProperty(value = "编辑分组信息")
    @PostMapping(value = "/edit")
    public Response<Group> saveOrUpdate(@RequestBody @Valid GroupEditModel groupModel) {
        return groupService.saveOrUpdate(groupModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('group_delete')")
    @ApiModelProperty(value = "删除分组")
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return groupService.delete(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('group_getGroupTree')")
    @ApiOperation(value = "获取分组树", notes = "机构树")
    @GetMapping(value = "/getGroupTree")
    public Response<List<TreeNodeModel>> getGroupTreeModel() {
        return Response.SUCCESS(groupService.getGroupTreeModel(GroupService.TOP_NODE));
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('group_fetch')")
    @ApiOperation(value = "获取分组树根据id", notes = "获取分组树根据id")
    @GetMapping(value = "/fetch")
    public Response<GroupSearchModel> fetch(Long id) {
        return groupService.findById(id);
    }
}
