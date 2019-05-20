package com.sophia.cms.rbac.service;

import com.alibaba.fastjson.JSONObject;
import com.august.website.utils.Md5Util;
import com.august.rbac.domain.Group;
import com.august.rbac.domain.Role;
import com.august.rbac.domain.UserInfo;
import com.august.rbac.dto.*;
import com.august.rbac.mapper.GroupMapper;
import com.august.rbac.mapper.RoleMapper;
import com.august.rbac.mapper.UserInfoMapper;
import com.august.rbac.mapper.UserRoleRelationMapper;
import com.august.rbac.security.OAuth2Principal;
import com.august.rbac.utils.OAuth2ContextHolder;
import com.august.website.utils.RecursiveTools;
import com.august.website.utils.Pager;
import com.august.website.utils.Resp;
import com.baomidou.mybatisplus.plugins.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import com.google.common.collect.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Created by lenovo on 2017/11/10.
 */
@Slf4j
@Service
public class UserService {
    private static final String pwd = "123456";

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleRelationMapper userRoleRelationMapper;

    @Autowired
    ResService resService;

    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * 保存或更新用户
     *
     * @param model
     * @return
     */
    @Transactional
    public Resp<UserInfo> edit(UserInfoEditDTO model) {

        // 用户名不能重复
        UserInfo userInfo = userInfoMapper.findByUserName(model.getUserName());
        if (null != userInfo && null == model.getId()) {
            return Resp.FAILURE("用户名称已存在!");
        } else if (null != userInfo &&
                null != model.getId() &&
                !userInfo.getId().equals(model.getId()) &&
                userInfo.getUserName().equals(model.getUserName())) {
            return Resp.FAILURE("用户名称已存在!");
        }

        // 修改用户信息
        UserInfo u = new UserInfo();
        u.setEmail(model.getEmail());
        u.setMobile(model.getMobile());
        u.setGroupId(model.getGroupId());
        u.setName(model.getName());
        u.setUserName(model.getUserName());

        // 新增
        if (null != model.getId()) {
            u = userInfoMapper.selectById(model.getId());
            u.setVersion(model.getVersion());

            // 判断是否需要更新用户角色
            List<Long> roleIds = userRoleRelationMapper.findByUserId(u.getId());
            if (!listEqual(roleIds, model.getRoles())) {
                EditUserRolesDTO userRolesModel = new EditUserRolesDTO();
                userRolesModel.setUserId(u.getId());

                // 删除用户角色
                userRolesModel.setRoleIds(roleIds);
                resService.removeUserRoles(userRolesModel);

                // 重新添加角色
                if (CollectionUtils.isEmpty(model.getRoles())) {
                    model.setRoles(Lists.newArrayList());
                }
                userRolesModel.setRoleIds(model.getRoles());
                resService.saveUserRoles(userRolesModel);
            }
            userInfoMapper.insert(u);
            return Resp.SUCCESS();
        }

        // 初始化密码
        u.setPassword(Md5Util.md5(pwd));
        userInfoMapper.updateById(u);

        // 判断是否需要创建用户角色
        if (CollectionUtils.isNotEmpty(model.getRoles())) {
            EditUserRolesDTO userRolesModel = new EditUserRolesDTO();
            userRolesModel.setRoleIds(model.getRoles());
            userRolesModel.setUserId(u.getId());
            resService.saveUserRoles(userRolesModel);
        }
        return Resp.SUCCESS(u);
    }

    public Resp updatePwd(UpdatePwdDTO updatePwdModel) {
        OAuth2Principal additionalInfo = OAuth2ContextHolder.getPrincipal();
        UserInfo userInfo = userInfoMapper.selectById(additionalInfo.getId());

        // 旧密码对比
        if (userInfo.getPassword().equals(updatePwdModel.getNewPassword())) {
            return Resp.FAILURE("密码验证错误!");
        }

        if (!updatePwdModel.getNewPassword().equals(updatePwdModel.getNewPassword2())) {
            return Resp.FAILURE("两次密码输入不一致!");
        }
        userInfo.setPassword(updatePwdModel.getNewPassword());
        userInfoMapper.updateById(userInfo);
        return Resp.SUCCESS();
    }

    public Resp<UserInfo> editInfo(UserInfoEditDTO userInfoEditModel) {
        // 修改用户信息
        UserInfo userInfo = new UserInfo();
        Boolean isEdit = null != userInfoEditModel.getId();
        if (isEdit) {
            userInfo = userInfoMapper.selectById(userInfoEditModel.getId());
            userInfo.setVersion(userInfoEditModel.getVersion());
        }
        userInfo.setEmail(userInfoEditModel.getEmail());
        userInfo.setMobile(userInfoEditModel.getMobile());
        userInfo.setName(userInfoEditModel.getName());
        userInfo.setUserName(userInfoEditModel.getUserName());
        userInfo.setAvatar(userInfoEditModel.getAvatar());
        if (isEdit) {
            userInfoMapper.updateById(userInfo);
        } else {
            userInfoMapper.insert(userInfo);
        }
        return Resp.SUCCESS(userInfo);
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
    public Resp delete(Long id) {
        userInfoMapper.deleteById(id);
        return Resp.SUCCESS();
    }

    /**
     * 根据分组ID,获取分组（包括所有下级）下所有用户
     *
     * @param userInfoSearchModel
     * @return
     */
    public Pager<UserInfoFetchDTO> list(UserInfoSearchDTO userInfoSearchModel) {

        // 机构过滤
        if (null != userInfoSearchModel.getGroupId()) {

            //查询该组的所有下属组
            List<Long> groupIds = RecursiveTools.forEachItem(userInfoSearchModel.getGroupId(), (Long groupId) -> {
                return groupMapper.findIdsByPid(groupId);
            });
            groupIds.add(userInfoSearchModel.getGroupId());
            userInfoSearchModel.setGroupIds(groupIds);
        }

        // 角色过滤
        if (null != userInfoSearchModel.getRoleId()) {

            // 查询角色下的所有用户
            List<Long> userIds = userRoleRelationMapper.findUserIdsByRoleId(userInfoSearchModel.getRoleId());
            userInfoSearchModel.setUserIds(userIds);
        }

        // 排除的角色
        if (null != userInfoSearchModel.getNotInRoleId()) {

            // 查询角色下的所有用户
            List<Long> userIds = userRoleRelationMapper.findUserIdsByRoleId(userInfoSearchModel.getNotInRoleId());
            if (CollectionUtils.isNotEmpty(userIds)) {
                userInfoSearchModel.setNotInUserIds(userIds);
            }
        }
        Page<UserInfoFetchDTO> page = new Page(userInfoSearchModel.getPageNo(), userInfoSearchModel.getPageSize());
        List<UserInfoFetchDTO> list = userInfoMapper.list(page, userInfoSearchModel);

        // page ret
        Pager<UserInfoFetchDTO> pager = new Pager<>();
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
    public List<UserInfo> findByGroupId(Long groupId) {
        return userInfoMapper.findByGroupId(groupId);
    }

    /**
     * 根据角色编码查询拥有该角色的人员
     *
     * @param roleCode 角色编码
     * @return
     */
    public List<UserInfo> findByRoleCode(String roleCode) {
        Role role = roleMapper.findByRoleCode(roleCode);
        if (ObjectUtils.isNotEmpty(role)) {
            List<Long> userIds = userRoleRelationMapper.findUserIdsByRoleId(role.getId());
            if (CollectionUtils.isNotEmpty(userIds)) {
                return userInfoMapper.findByIdIn(userIds);
            }
        }
        return null;
    }


    public Resp<UserInfoFetchDTO> getCustomInfo() {
        OAuth2Principal auth2Principal = OAuth2ContextHolder.getPrincipal();
        UserInfo userInfo = userInfoMapper.selectById(auth2Principal.getId());
        if (null == userInfo) {
            return Resp.FAILURE("记录不存在");
        }
        userInfo.setGroupName(auth2Principal.getGroupName());
        UserInfoFetchDTO model = new UserInfoFetchDTO();
        new ModelMapper().map(userInfo, model);
        return Resp.SUCCESS(model);
    }

    public Resp<UserInfoFetchDTO> fetch(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (null == userInfo) {
            return Resp.FAILURE("记录不存在");
        }
        UserInfoFetchDTO userInfoFetchModel = UserInfoFetchDTO.builder()
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
            userInfoFetchModel.setRoles(roles);
        }
        return Resp.SUCCESS(userInfoFetchModel);
    }

    public UserInfo findByUserName(String userName) {
        return userInfoMapper.findByUserName(userName);
    }

    public Resp findByUserNameAndPassword(String userName, String password) {
        //查询用户
        UserInfo userInfo = userInfoMapper.findByUserName(userName);
        if (null == userInfo) {
            return Resp.FAILURE();
        }

        //Sha256Hash pwd
//        String pwd = MD5Utils.string2MD5(password);

        //密码校验
        userInfo = userInfoMapper.findByUserNameAndPassword(userName, password);
        if (null == userInfo) {
            return Resp.FAILURE();
        }
        userInfo.setAvatar(userInfo.getAvatar());

        //获取用户所在部门
        Group dept = groupMapper.selectById(userInfo.getGroupId());
        if (null != dept) {
            userInfo.setGroupId(dept.getId());
            userInfo.setGroupName(dept.getGroupName());
        }
        return Resp.SUCCESS(userInfo);
    }
}
