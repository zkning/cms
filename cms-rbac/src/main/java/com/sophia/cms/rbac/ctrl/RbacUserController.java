package com.sophia.cms.rbac.ctrl;

import com.sophia.cms.framework.request.Request;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.rbac.domain.RbacUserInfo;
import com.sophia.cms.rbac.model.*;
import com.sophia.cms.rbac.utils.SessionContextHolder;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by ningzuokun on 2017/11/22.
 */
@RestController
@RequestMapping("/rbacuser")
public class RbacUserController {

    @Autowired
    private RbacUserService rbacUserService;

    @Autowired
    ResourcesService resourcesService;


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('rbacuser_edit')")
    @ApiModelProperty(value = "编辑用户")
    @PostMapping(value = "/edit")
    public Response<RbacUserInfo> edit(@RequestBody @Valid RbacUserInfoEditModel rbacUserInfoReqModel) {
        return rbacUserService.edit(rbacUserInfoReqModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('rbacuser_updateInfo')")
    @ApiModelProperty(value = "客户信息更新")
    @PostMapping(value = "/updateInfo")
    public Response updateInfo(@RequestBody @Valid RbacUserInfoEditModel rbacUserInfoReqModel) {
        return rbacUserService.editInfo(rbacUserInfoReqModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('rbacuser_updatePwd')")
    @ApiModelProperty(value = "修改密码")
    @PostMapping(value = "/updatePwd")
    public Response updatePwd(@RequestBody @Valid UpdatePwdModel updatePwdModel) {
        return rbacUserService.updatePwd(updatePwdModel);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('rbacuser_delete')")
    @ApiModelProperty(value = "删除用户")
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return rbacUserService.delete(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('rbacuser_list')")
    @ApiModelProperty(value = "查询所有的用户(包括子分组)列表")
    @GetMapping(value = "/list")
    public Response<Pager<RbacUserInfoFetchModel>> list(@Valid RbacUserInfoSearchModel rbacUserInfoQueryModel) {
        Pager<RbacUserInfoFetchModel> pager = rbacUserService.list(rbacUserInfoQueryModel);
        return Response.SUCCESS(pager);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('rbacuser_fetch')")
    @ApiOperation(value = "获取用户信息根据id", notes = "获取用户信息")
    @GetMapping(value = "/fetch")
    public Response<RbacUserInfoFetchModel> fetch(Long id) {
        return rbacUserService.fetch(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasAuthority('rbacuser_getCustomInfo')")
    @ApiOperation(value = "当前客户资料", notes = "当前客户资料")
    @GetMapping(value = "/getCustomInfo")
    public Response<CustomInfoModel> getCustomInfo() {
        return rbacUserService.getCustomInfo();
    }


    @ApiOperation(value = "加载用户凭证")
    @RequestMapping(value = "/getAccountInfo", method = RequestMethod.GET)
    public Response<AccountInfoModel> loadCredentials(Request request) {
        return Response.SUCCESS(resourcesService.getAccountInfo(request.getSessionUserId()));
    }

    @ApiOperation(value = "用户解锁")
    @PostMapping(value = "/unlock")
    public Response unlock(@RequestBody @Valid UnLockModel unLockModel) {
        return rbacUserService.findByUserNameAndPassword(SessionContextHolder.getPrincipal().getUsername(),
                unLockModel.getPassword());
    }
}