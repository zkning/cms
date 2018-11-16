package com.fast.admin.rbac.service;

import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.response.Response;
import com.fast.admin.framework.util.BeanUtils;
import com.fast.admin.framework.util.MD5Utils;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.orm.repository.NamedParameterJdbcRepository;
import com.fast.admin.orm.utils.SQLHelper;
import com.fast.admin.rbac.domain.Group;
import com.fast.admin.rbac.domain.RbacUserInfo;
import com.fast.admin.rbac.domain.Role;
import com.fast.admin.rbac.model.*;
import com.fast.admin.rbac.repository.GroupRepository;
import com.fast.admin.rbac.repository.RbacUserInfoRepository;
import com.fast.admin.rbac.repository.RoleRepository;
import com.fast.admin.rbac.repository.UserRoleRelationRepository;
import com.fast.admin.rbac.security.AdditionalInfo;
import com.fast.admin.rbac.utils.RecursiveTools;
import com.fast.admin.rbac.utils.SessionContextHolder;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2017/11/10.
 */
@Slf4j
@Service
public class RbacUserService {
    private static final String pwd = "123456";
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private RbacUserInfoRepository rbacUserInfoRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRelationRepository userRoleRelationRepository;
    @Autowired
    NamedParameterJdbcRepository namedParameterJdbcRepository;
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
        RbacUserInfo userInfo = rbacUserInfoRepository.findByUserName(rbacUserInfoReqModel.getUserName());
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
        if (null != rbacUserInfoReqModel.getId()) {
            rbacUserInfo = rbacUserInfoRepository.findById(rbacUserInfoReqModel.getId());
            rbacUserInfo.setVersion(rbacUserInfoReqModel.getVersion());

            // 判断是否需要更新用户角色
            List<Long> roleIds = userRoleRelationRepository.findByUserId(rbacUserInfo.getId());
            if (!listEqual(roleIds, rbacUserInfoReqModel.getRoles())) {
                EditUserRolesModel editUserRolesModel = new EditUserRolesModel();
                editUserRolesModel.setUserId(rbacUserInfo.getId());

                // 删除用户角色
                editUserRolesModel.setRoleIds(roleIds);
                resourceService.removeUserRoles(editUserRolesModel);

                // 重新添加角色
                if (CollectionUtils.isEmpty(rbacUserInfoReqModel.getRoles())) {
                    rbacUserInfoReqModel.setRoles(Lists.newArrayList());
                }
                editUserRolesModel.setRoleIds(rbacUserInfoReqModel.getRoles());
                resourceService.saveUserRoles(editUserRolesModel);
            }
        } else {

            // 初始化密码
            rbacUserInfo.setPassword(MD5Utils.string2MD5(pwd));

            // 判断是否需要创建用户角色
            if (CollectionUtils.isNotEmpty(rbacUserInfoReqModel.getRoles())) {
                EditUserRolesModel editUserRolesModel = new EditUserRolesModel();
                editUserRolesModel.setRoleIds(rbacUserInfoReqModel.getRoles());
                editUserRolesModel.setUserId(rbacUserInfo.getId());
                resourceService.saveUserRoles(editUserRolesModel);
            }
        }
        rbacUserInfo.setEmail(rbacUserInfoReqModel.getEmail());
        rbacUserInfo.setMobile(rbacUserInfoReqModel.getMobile());
        rbacUserInfo.setGroupId(rbacUserInfoReqModel.getGroupId());
        rbacUserInfo.setName(rbacUserInfoReqModel.getName());
        rbacUserInfo.setUserName(rbacUserInfoReqModel.getUserName());
        RbacUserInfo user = rbacUserInfoRepository.save(rbacUserInfo);
        return Response.SUCCESS(user);
    }

    public Response updatePwd(UpdatePwdModel updatePwdModel) {
        AdditionalInfo additionalInfo = SessionContextHolder.me();
        RbacUserInfo userInfo = rbacUserInfoRepository.findOne(additionalInfo.getId());

        // 旧密码对比
        if (userInfo.getPassword().equals(updatePwdModel.getNewPassword())) {
            return Response.FAILURE("密码验证错误!");
        }

        if (!updatePwdModel.getNewPassword().equals(updatePwdModel.getNewPassword2())) {
            return Response.FAILURE("两次密码输入不一致!");
        }
        userInfo.setPassword(updatePwdModel.getNewPassword());
        rbacUserInfoRepository.save(userInfo);
        return Response.SUCCESS();
    }

    public Response<RbacUserInfo> editInfo(RbacUserInfoEditModel rbacUserInfoReqModel) {
        // 修改用户信息
        RbacUserInfo rbacUserInfo = new RbacUserInfo();
        if (null != rbacUserInfoReqModel.getId()) {
            rbacUserInfo = rbacUserInfoRepository.findById(rbacUserInfoReqModel.getId());
            rbacUserInfo.setVersion(rbacUserInfoReqModel.getVersion());
        }
        rbacUserInfo.setEmail(rbacUserInfoReqModel.getEmail());
        rbacUserInfo.setMobile(rbacUserInfoReqModel.getMobile());
        rbacUserInfo.setName(rbacUserInfoReqModel.getName());
        rbacUserInfo.setUserName(rbacUserInfoReqModel.getUserName());
        rbacUserInfo.setAvatar(rbacUserInfoReqModel.getAvatar());
        RbacUserInfo user = rbacUserInfoRepository.save(rbacUserInfo);
        return Response.SUCCESS(user);
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
        rbacUserInfoRepository.delete(id);
        return Response.SUCCESS();
    }

    /**
     * 根据分组ID,获取分组（包括所有下级）下所有用户
     *
     * @param rbacUserInfoSearchModel
     * @return
     */
    public Pager<RbacUserInfoFetchModel> list(RbacUserInfoSearchModel rbacUserInfoSearchModel) {
        StringBuffer sqlBuffer = new StringBuffer("select trui.id as id,trui.user_name as userName,trui.name as name,trui.mobile as mobile,trui.version," +
                "trui.create_time as createTime,trui.create_user as createUser,trg.group_name as groupName " +
                "from t_rbac_user_info trui left join t_rbac_group trg on trui.group_id = trg.id " +
                "where 1 = 1 ");

        SQLHelper sqlHelper = SQLHelper.getInstnce(sqlBuffer, new HashMap<>())
                .like("trui", "user_name", rbacUserInfoSearchModel.getUserName())
                .like("trui", "name", rbacUserInfoSearchModel.getName())
                .like("trui", "mobile", rbacUserInfoSearchModel.getMobile())
                .greaterThan("trui", "create_time", rbacUserInfoSearchModel.getCreateTime())
                .desc("trui", "create_time");

        // 机构过滤
        if (null != rbacUserInfoSearchModel.getGroupId()) {

            //查询该组的所有下属组
            List<Long> groupIds = RecursiveTools.forEachItem(rbacUserInfoSearchModel.getGroupId(), (Long groupId) -> {
                return groupRepository.findIdsByPid(groupId);
            });
            groupIds.add(rbacUserInfoSearchModel.getGroupId());
            sqlHelper.in("trui", "group_id", groupIds);
        }

        // 角色过滤
        if (null != rbacUserInfoSearchModel.getRoleId()) {

            // 查询角色下的所有用户
            List<Long> userIds = userRoleRelationRepository.findUserIdsByRoleId(rbacUserInfoSearchModel.getRoleId());
            sqlHelper.in("trui", "id", userIds);
        }

        // 排除的角色
        if (null != rbacUserInfoSearchModel.getNotInRoleId()) {

            // 查询角色下的所有用户
            List<Long> userIds = userRoleRelationRepository.findUserIdsByRoleId(rbacUserInfoSearchModel.getNotInRoleId());
            if (CollectionUtils.isNotEmpty(userIds)) {
                sqlHelper.notIn("trui", "id", userIds);
            }
        }

        SQLHelper.ConditionModel conditionModel = sqlHelper.getCondition();
        Pager<RbacUserInfoFetchModel> pager = namedParameterJdbcRepository.findAll(conditionModel.getSqlCondition().toString(), RbacUserInfoFetchModel.class,
                conditionModel.getParams(), rbacUserInfoSearchModel.getPageNo(), rbacUserInfoSearchModel.getPageSize());
        return pager;
    }

    /**
     * 根据分组ID，获取分组下用户
     *
     * @param groupId 组别id
     * @return
     */
    public List<RbacUserInfo> findByGroupId(Long groupId) {
        List<RbacUserInfo> users = rbacUserInfoRepository.findByGroupId(groupId);
        return users;
    }

    /**
     * 根据角色编码查询拥有该角色的人员
     *
     * @param roleCode 角色编码
     * @return
     */
    public List<RbacUserInfo> findByRoleCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode);
        if (BeanUtils.isNotEmpty(role)) {
            List<Long> userIds = userRoleRelationRepository.findUserIdsByRoleId(role.getId());
            if (BeanUtils.isNotEmpty(userIds)) {
                return rbacUserInfoRepository.findByIdIn(userIds);
            }
        }
        return null;
    }


    public Response<CustomInfoModel> getCustomInfo() {
        AdditionalInfo additionalInfo = SessionContextHolder.me();
        RbacUserInfo userInfo = rbacUserInfoRepository.findOne(additionalInfo.getId());
        if (null == userInfo) {
            return Response.FAILURE("记录不存在");
        }
        userInfo.setGroupName(additionalInfo.getGroupName());
        CustomInfoModel model = new CustomInfoModel();
        new ModelMapper().map(userInfo, model);
        return Response.SUCCESS(model);
    }

    public Response<RbacUserInfoFetchModel> fetch(Long userId) {
        RbacUserInfo userInfo = rbacUserInfoRepository.findOne(userId);
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
        List<Long> roleIds = userRoleRelationRepository.findByUserId(userId);
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
        return rbacUserInfoRepository.findByUserName(userName);
    }

    public Response findByUserNameAndPassword(String userName, String password) {
        //查询用户
        RbacUserInfo userInfo = rbacUserInfoRepository.findByUserName(userName);
        if (null == userInfo) {
            return Response.FAILURE();
        }

        //Sha256Hash pwd
//        String pwd = MD5Utils.string2MD5(password);

        //密码校验
        userInfo = rbacUserInfoRepository.findByUserNameAndPassword(userName, password);
        if (null == userInfo) {
            return Response.FAILURE();
        }
        userInfo.setAvatar(userInfo.getAvatar());

        //获取用户所在部门
        Group dept = groupRepository.findOne(userInfo.getGroupId());
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
