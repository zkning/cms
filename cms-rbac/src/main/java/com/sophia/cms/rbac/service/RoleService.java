package com.sophia.cms.rbac.service;

import com.sophia.cms.framework.response.Response;
import com.sophia.cms.framework.util.BeanUtils;
import com.sophia.cms.rbac.domain.Dict;
import com.sophia.cms.rbac.domain.Role;
import com.sophia.cms.rbac.mapper.RoleMapper;
import com.sophia.cms.rbac.model.RoleEditModel;
import com.sophia.cms.rbac.model.RoleFetchModel;
import com.sophia.cms.rbac.model.RoleSearchModel;
import com.sophia.cms.rbac.model.TreeNodeModel;
import com.sophia.cms.rbac.repository.DictRepository;
import com.sophia.cms.rbac.repository.RoleRepository;
import com.sophia.cms.rbac.utils.RecursiveTools;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lenovo on 2017/11/11.
 */
@Service
public class RoleService {

    private static final String ROLE_TYPE_CODE = "JUESEFENLEI";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    DictRepository dictRepository;

    @Autowired
    RoleMapper roleMapper;


    /**
     * 根据用户id获取所有角色
     *
     * @param userId 用户ID
     * @return
     */
    public List<Role> findAllRoleByUserId(Long userId) {
        return roleRepository.findAllRoleByUserId(userId);
    }

    /**
     * 根据用户id,角色编码判断是否拥有该角色
     *
     * @param userId   用户id
     * @param roleCode 角色编码
     * @return
     */
    public boolean hasRoleOrNotByUserIdAndRoleCode(Long userId, String roleCode) {
        Role role = roleRepository.findRoleByUserIdAndRoleType(userId, roleCode);
        if (BeanUtils.isNotEmpty(role)) {
            return true;
        }
        return false;
    }

    public Response edit(RoleEditModel roleEditModel) {
        Role role = roleRepository.findByRoleCode(roleEditModel.getRoleCode());

        // 创建
        if (null != role && null == roleEditModel.getId()) {
            return Response.FAILURE("角色编号已存在!");
        } else if (null != role &&
                null != roleEditModel.getId() &&
                !role.getId().equals(roleEditModel.getId()) &&
                role.getRoleCode().equals(roleEditModel.getRoleCode())) {
            return Response.FAILURE("角色编号已存在!");
        }
        Role roleModel = new Role();
        if (null != roleEditModel.getId()) {
            roleModel = roleRepository.findOne(roleEditModel.getId());
            roleModel.setVersion(roleEditModel.getVersion());
        }
        roleModel.setRoleName(roleEditModel.getRoleName());
        roleModel.setRoleCode(roleEditModel.getRoleCode());
        roleModel.setRemark(roleEditModel.getRemark());
        roleModel.setGroupId(roleEditModel.getGroupId());
        roleModel.setRoleType(roleEditModel.getRoleType());
        roleModel.setRemark(roleEditModel.getRemark());
        roleRepository.save(roleModel);
        return Response.SUCCESS(roleModel);
    }

    public Response delete(Long id) {
        roleRepository.delete(id);
        return Response.SUCCESS();
    }

    public Response<RoleFetchModel> fetch(Long id) {
        Role role = roleRepository.findOne(id);
        if (null == role) {
            return Response.FAILURE("记录不存在");
        }
        RoleFetchModel roleFetchModel = RoleFetchModel.builder()
                .roleCode(role.getRoleCode())
                .groupId(role.getGroupId())
                .roleName(role.getRoleName())
                .roleType(role.getRoleType() + "")
                .version(role.getVersion())
                .remark(role.getRemark())
                .id(role.getId()).build();
        return Response.SUCCESS(roleFetchModel);
    }

    /**
     * 获取分组树
     */
    public List<TreeNodeModel> TreeNodeModel() {

        //从数据字典获取分类
        Dict dict = dictRepository.findByValue(ROLE_TYPE_CODE);

        List<Dict> dicts = dictRepository.findByPidOrderBySortDesc(dict.getId());
        if (CollectionUtils.isEmpty(dicts)) {
            return Lists.newArrayList();
        }
        List<TreeNodeModel> treeNodeModels = Lists.newArrayList();
        dicts.forEach(item -> {
            treeNodeModels.add(TreeNodeModel.builder()
                    .key(item.getValue() + "")
                    .pid(item.getPid() + "")
                    .selectable(false)
                    .title(item.getText())
                    .build());
        });
        return RecursiveTools.forEachTreeItems(treeNodeModels, (TreeNodeModel item) -> {
            List<Role> roleList = roleRepository.findByRoleType(Integer.valueOf(item.getKey()));
            if (CollectionUtils.isEmpty(roleList)) {
                item.setLeaf(true);
                return null;
            }
            List<TreeNodeModel> list = Lists.newArrayList();
            roleList.forEach(role -> {
                list.add(TreeNodeModel.builder()
                        .key(role.getId() + "")
                        .selectable(true)
                        .title(role.getRoleName())
                        .pid(item.getKey())
                        .build());
            });
            item.setChildren(list);
            return item.getChildren();
        });
    }

    public List<RoleFetchModel> list(RoleSearchModel roleSearchModel) {
        return roleMapper.list(roleSearchModel);
    }
}
