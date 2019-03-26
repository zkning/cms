package com.sophia.cms.rbac.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.framework.util.BeanUtils;
import com.sophia.cms.framework.util.MD5Utils;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.rbac.domain.Group;
import com.sophia.cms.rbac.domain.RbacUserInfo;
import com.sophia.cms.rbac.domain.Role;
import com.sophia.cms.rbac.mapper.GroupMapper;
import com.sophia.cms.rbac.mapper.RbacUserInfoMapper;
import com.sophia.cms.rbac.mapper.RoleMapper;
import com.sophia.cms.rbac.mapper.UserRoleRelationMapper;
import com.sophia.cms.rbac.model.*;
import com.sophia.cms.rbac.security.OAuth2Principal;
import com.sophia.cms.rbac.utils.RecursiveTools;
import com.sophia.cms.rbac.utils.SessionContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.assertj.core.util.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017/11/10.
 */
@Slf4j
@Service
public class RbacUserService {
    private static final String pwd = "123456";

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RbacUserInfoMapper rbacUserInfoMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleRelationMapper userRoleRelationMapper;

    @Autowired
    ResourcesService resourceService;

    /**
     * 保存或更新用户
     *
     * @param rbacUserInfoReqModel
     * @return
     */
    @Transactional
    public Response<RbacUserInfo> edit(RbacUserInfoEditModel rbacUserInfoReqModel) {

        // 用户名不能重复
        RbacUserInfo userInfo = rbacUserInfoMapper.findByUserName(rbacUserInfoReqModel.getUserName());
        if (null != userInfo && null == rbacUserInfoReqModel.getId()) {
            return Response.FAILURE("用户名称已存在!");
        } else if (null != userInfo &&
                null != rbacUserInfoReqModel.getId() &&
                !userInfo.getId().equals(rbacUserInfoReqModel.getId()) &&
                userInfo.getUserName().equals(rbacUserInfoReqModel.getUserName())) {
            return Response.FAILURE("用户名称已存在!");
        }

        // 修改用户信息
        RbacUserInfo rbacUserInfo = new RbacUserInfo();
        Boolean flag = null != rbacUserInfoReqModel.getId();
        if (flag) {
            rbacUserInfo = rbacUserInfoMapper.selectById(rbacUserInfoReqModel.getId());
        }
        rbacUserInfo.setEmail(rbacUserInfoReqModel.getEmail());
        rbacUserInfo.setMobile(rbacUserInfoReqModel.getMobile());
        rbacUserInfo.setGroupId(rbacUserInfoReqModel.getGroupId());
        rbacUserInfo.setName(rbacUserInfoReqModel.getName());
        rbacUserInfo.setUserName(rbacUserInfoReqModel.getUserName());
        rbacUserInfo.setLastUpdateTime(new Date());
        if (flag) {
            rbacUserInfo.setVersion(rbacUserInfoReqModel.getVersion());

            // 判断是否需要更新用户角色
            List<Long> roleIds = userRoleRelationMapper.findByUserId(rbacUserInfo.getId());
            if (!listEqual(roleIds, rbacUserInfoReqModel.getRoles())) {
                EditUserRolesModel editUserRolesModel = new EditUserRolesModel();
                editUserRolesModel.setUserId(rbacUserInfo.getId());

                // 删除用户角色
                editUserRolesModel.setRoleIds(roleIds);
                if (CollectionUtils.isNotEmpty(roleIds)) {
                    resourceService.removeUserRoles(editUserRolesModel);
                }

                // 重新添加角色
                if (CollectionUtils.isEmpty(rbacUserInfoReqModel.getRoles())) {
                    rbacUserInfoReqModel.setRoles(Lists.newArrayList());
                }
                editUserRolesModel.setRoleIds(rbacUserInfoReqModel.getRoles());
                resourceService.saveUserRoles(editUserRolesModel);
            }
            rbacUserInfoMapper.updateById(rbacUserInfo);
            return Response.SUCCESS();
        }
        // 初始化密码
        rbacUserInfo.setPassword(MD5Utils.string2MD5(pwd));
        rbacUserInfo.setCreateTime(new Date());
        rbacUserInfoMapper.insert(rbacUserInfo);

        // 判断是否需要创建用户角色
        if (CollectionUtils.isNotEmpty(rbacUserInfoReqModel.getRoles())) {
            EditUserRolesModel editUserRolesModel = new EditUserRolesModel();
            editUserRolesModel.setRoleIds(rbacUserInfoReqModel.getRoles());
            editUserRolesModel.setUserId(rbacUserInfo.getId());
            resourceService.saveUserRoles(editUserRolesModel);
        }
        return Response.SUCCESS(rbacUserInfo);
    }

    public Response updatePwd(UpdatePwdModel updatePwdModel) {
        OAuth2Principal additionalInfo = SessionContextHolder.getPrincipal();
        RbacUserInfo userInfo = rbacUserInfoMapper.selectById(additionalInfo.getId());

        // 旧密码对比
        if (userInfo.getPassword().equals(updatePwdModel.getNewPassword())) {
            return Response.FAILURE("密码验证错误!");
        }

        if (!updatePwdModel.getNewPassword().equals(updatePwdModel.getNewPassword2())) {
            return Response.FAILURE("两次密码输入不一致!");
        }
        userInfo.setLastUpdateTime(new Date());
        userInfo.setPassword(updatePwdModel.getNewPassword());
        rbacUserInfoMapper.updateById(userInfo);
        return Response.SUCCESS();
    }

    public Response<RbacUserInfo> editInfo(RbacUserInfoEditModel rbacUserInfoReqModel) {
        // 修改用户信息
        RbacUserInfo rbacUserInfo = new RbacUserInfo();
        if (null != rbacUserInfoReqModel.getId()) {
            rbacUserInfo = rbacUserInfoMapper.selectById(rbacUserInfoReqModel.getId());
            rbacUserInfo.setVersion(rbacUserInfoReqModel.getVersion());
        }
        rbacUserInfo.setEmail(rbacUserInfoReqModel.getEmail());
        rbacUserInfo.setMobile(rbacUserInfoReqModel.getMobile());
        rbacUserInfo.setName(rbacUserInfoReqModel.getName());
        rbacUserInfo.setUserName(rbacUserInfoReqModel.getUserName());
        rbacUserInfo.setAvatar(rbacUserInfoReqModel.getAvatar());
        rbacUserInfoMapper.updateById(rbacUserInfo);
        return Response.SUCCESS(rbacUserInfo);
    }

    private boolean listEqual(List<Long> roleIds, List<Long> roles) {
        if (roleIds == roles) {
            return true;
        }
        if (null != roleIds) {
            Collections.sort(roleIds);
        }
        if (null != roles) {
            Collections.sort(roles);
        }
        return JSONObject.toJSONString(roleIds).equals(JSONObject.toJSONString(roles));
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    public Response delete(Long id) {
        rbacUserInfoMapper.deleteById(id);
        return Response.SUCCESS();
    }

    /**
     * 根据分组ID,获取分组（包括所有下级）下所有用户
     *
     * @param rbacUserInfoSearchModel
     * @return
     */
    public Pager<RbacUserInfoFetchModel> list(RbacUserInfoSearchModel rbacUserInfoSearchModel) {

        // 机构过滤
        if (null != rbacUserInfoSearchModel.getGroupId()) {

            //查询该组的所有下属组
            List<Long> groupIds = RecursiveTools.forEachItem(rbacUserInfoSearchModel.getGroupId(), (Long groupId) -> {
                return groupMapper.findIdsByPid(groupId);
            });
            groupIds.add(rbacUserInfoSearchModel.getGroupId());
            rbacUserInfoSearchModel.setGroupIds(groupIds);
        }

        // 角色过滤
        if (null != rbacUserInfoSearchModel.getRoleId()) {

            // 查询角色下的所有用户
            List<Long> userIds = userRoleRelationMapper.findUserIdsByRoleId(rbacUserInfoSearchModel.getRoleId());
            rbacUserInfoSearchModel.setUserIds(userIds);
        }

        // 排除的角色
        if (null != rbacUserInfoSearchModel.getNotInRoleId()) {

            // 查询角色下的所有用户
            List<Long> userIds = userRoleRelationMapper.findUserIdsByRoleId(rbacUserInfoSearchModel.getNotInRoleId());
            if (CollectionUtils.isNotEmpty(userIds)) {
                rbacUserInfoSearchModel.setNotInUserIds(userIds);
            }
        }
        Page<RbacUserInfoFetchModel> page = new Page(rbacUserInfoSearchModel.getPageNo(), rbacUserInfoSearchModel.getPageSize());
        List<RbacUserInfoFetchModel> list = rbacUserInfoMapper.list(page, rbacUserInfoSearchModel);
        Pager<RbacUserInfoFetchModel> pager = new Pager<>();
        pager.setPageNo(page.getCurrent());
        pager.setPageSize(page.getSize());
        pager.setTotalElements(page.getTotal());
        pager.setContent(list);
        return pager;
    }

    /**
     * 根据分组ID，获取分组下用户
     *
     * @param groupId 组别id
     * @return
     */
    public List<RbacUserInfo> findByGroupId(Long groupId) {
        List<RbacUserInfo> users = rbacUserInfoMapper.findByGroupId(groupId);
        return users;
    }

    /**
     * 根据角色编码查询拥有该角色的人员
     *
     * @param roleCode 角色编码
     * @return
     */
    public List<RbacUserInfo> findByRoleCode(String roleCode) {
        Role role = roleMapper.findByRoleCode(roleCode);
        if (BeanUtils.isNotEmpty(role)) {
            List<Long> userIds = userRoleRelationMapper.findUserIdsByRoleId(role.getId());
            if (BeanUtils.isNotEmpty(userIds)) {
                return rbacUserInfoMapper.findByIdIn(userIds);
            }
        }
        return null;
    }


    public Response<CustomInfoModel> getCustomInfo() {
        OAuth2Principal auth2Principal = SessionContextHolder.getPrincipal();
        RbacUserInfo userInfo = rbacUserInfoMapper.selectById(auth2Principal.getId());
        if (null == userInfo) {
            return Response.FAILURE("记录不存在");
        }
        userInfo.setGroupName(auth2Principal.getGroupName());
        CustomInfoModel model = new CustomInfoModel();
        new ModelMapper().map(userInfo, model);
        return Response.SUCCESS(model);
    }

    public Response<RbacUserInfoFetchModel> fetch(Long userId) {
        RbacUserInfo userInfo = rbacUserInfoMapper.selectById(userId);
        if (null == userInfo) {
            return Response.FAILURE("记录不存在");
        }
        RbacUserInfoFetchModel rbacUserInfoFetchModel = RbacUserInfoFetchModel.builder()
                .id(userInfo.getId())
                .userName(userInfo.getUserName())
                .name(userInfo.getName())
                .groupId(userInfo.getGroupId())
                .mobile(userInfo.getMobile())
                .version(userInfo.getVersion())
                .email(userInfo.getEmail()).build();
        List<Long> roleIds = userRoleRelationMapper.findByUserId(userId);
        List<String> roles = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roles.add(String.valueOf(roleId));
            }
            rbacUserInfoFetchModel.setRoles(roles);
        }
        return Response.SUCCESS(rbacUserInfoFetchModel);
    }

    public RbacUserInfo findByUserName(String userName) {
        return rbacUserInfoMapper.findByUserName(userName);
    }

    public Response findByUserNameAndPassword(String userName, String password) {
        //查询用户
        RbacUserInfo userInfo = rbacUserInfoMapper.findByUserName(userName);
        if (null == userInfo) {
            return Response.FAILURE();
        }

        //Sha256Hash pwd
//        String pwd = MD5Utils.string2MD5(password);

        //密码校验
        userInfo = rbacUserInfoMapper.findByUserNameAndPassword(userName, password);
        if (null == userInfo) {
            return Response.FAILURE();
        }
        userInfo.setAvatar(userInfo.getAvatar());

        //获取用户所在部门
        Group dept = groupMapper.selectById(userInfo.getGroupId());
        if (null != dept) {
            userInfo.setGroupId(dept.getId());
            userInfo.setGroupName(dept.getGroupName());
        }
        return Response.SUCCESS(userInfo);
    }

    public Response<CredentialsModel> login(AccountCredentials accountCredentials) {
        if (StringUtils.isNotBlank(accountCredentials.getCode())) {
            return Response.FAILURE();
        }
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(accountCredentials.getUsername(), accountCredentials.getPassword());
        Subject subject = SecurityUtils.getSubject();
        subject.login(usernamePasswordToken);
        if (subject.isAuthenticated()) {
            CredentialsModel credentialsModel = new CredentialsModel();
            credentialsModel.setToken((String) subject.getSession().getId());
            credentialsModel.setUser((RbacUserInfo) subject.getPrincipals().getPrimaryPrincipal());
            return Response.SUCCESS(credentialsModel);
        }
        return Response.FAILURE("账户或密码错误!");
    }
}
