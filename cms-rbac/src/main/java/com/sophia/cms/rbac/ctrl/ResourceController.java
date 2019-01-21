package com.sophia.cms.rbac.ctrl;

import com.sophia.cms.rbac.model.*;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.rbac.service.ResourcesService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by ningzuokun on 2018/3/22.
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourcesService resourceService;


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('resource_edit')")
    @ApiModelProperty(value = "编辑")
    @PostMapping(value = "/edit")
    public Response edit(@RequestBody @Valid ResourceEditModel resourceEditModel) {
        return resourceService.edit(resourceEditModel);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('resource_getRoleResTree')")
    @ApiOperation(value = "获取分组树", notes = "机构树")
    @GetMapping(value = "/getRoleResTree")
    public Response<List<TreeNodeModel>> getRoleResTree(Long roleId) {
        return Response.SUCCESS(resourceService.getRoleResTree(roleId));
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('resource_delete')")
    @ApiModelProperty(value = "删除")
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return resourceService.delete(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('resource_fetch')")
    @ApiOperation(value = "获取角色根据id", notes = "获取角色根据id")
    @GetMapping(value = "/fetch")
    public Response<ResouceFetchModel> fetch(Long id) {
        return resourceService.fetch(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('resource_saveRoleRes')")
    @ApiModelProperty(value = "关联角色资源")
    @PostMapping(value = "/saveRoleRes")
    public Response saveRoleRes(@RequestBody @Valid SaveRoleResModel saveRoleResModel) {
        return resourceService.saveRoleRes(saveRoleResModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('resource_saveRoleUser')")
    @ApiModelProperty(value = "关联角色用户")
    @PostMapping(value = "/saveRoleUser")
    public Response saveRoleUser(@RequestBody @Valid EditRoleUserModel editRoleUserModel) {
        return resourceService.saveRoleUser(editRoleUserModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('resource_removeRoleUser')")
    @ApiModelProperty(value = "移除关联角色用户")
    @PostMapping(value = "/removeRoleUser")
    public Response removeRoleUser(@RequestBody @Valid EditRoleUserModel editRoleUserModel) {
        return resourceService.removeRoleUser(editRoleUserModel);
    }

}
