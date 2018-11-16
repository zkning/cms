package com.fast.admin.rbac.ctrl;

import com.fast.admin.framework.response.Response;
import com.fast.admin.rbac.domain.Group;
import com.fast.admin.rbac.model.RoleEditModel;
import com.fast.admin.rbac.model.RoleFetchModel;
import com.fast.admin.rbac.model.RoleSearchModel;
import com.fast.admin.rbac.model.TreeNodeModel;
import com.fast.admin.rbac.service.RoleService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('role_edit')")
    @ApiModelProperty(value = "编辑角色信息")
    @PostMapping(value = "/edit")
    public Response<Group> edit(@RequestBody @Valid RoleEditModel roleEditModel) {
        return roleService.edit(roleEditModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('role_delete')")
    @ApiModelProperty(value = "删除")
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return roleService.delete(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('role_getRoleTree')")
    @ApiOperation(value = "获取角色树", notes = "角色树")
    @GetMapping(value = "/getRoleTree")
    public Response<List<TreeNodeModel>> roleTree() {
        return Response.SUCCESS(roleService.TreeNodeModel());
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('role_fetch')")
    @ApiOperation(value = "获取角色根据id", notes = "获取角色根据id")
    @GetMapping(value = "/fetch")
    public Response<RoleFetchModel> fetch(Long id) {
        return roleService.fetch(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('role_list')")
    @ApiOperation(value = "查询列表")
    @GetMapping(value = "/list")
    public Response<List<RoleFetchModel>> list(RoleSearchModel roleSearchModel) {
        return Response.SUCCESS(roleService.list(roleSearchModel));
    }
}
