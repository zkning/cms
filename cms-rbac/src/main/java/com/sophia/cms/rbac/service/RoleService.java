package com.sophia.cms.rbac.service;

import com.august.rbac.domain.Dict;
import com.august.rbac.domain.Role;
import com.august.rbac.dto.RoleEditDTO;
import com.august.rbac.dto.RoleFetchDTO;
import com.august.rbac.dto.RoleSearchDTO;
import com.august.rbac.dto.TreeNodeDTO;
import com.august.rbac.mapper.DictMapper;
import com.august.rbac.mapper.RoleMapper;
import com.august.website.utils.RecursiveTools;
import com.august.website.utils.Resp;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lenovo on 2017/11/11.
 */
@Service
public class RoleService {

    private static final String ROLE_TYPE_CODE = "JUE_SE_FEN_LEI";

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    DictMapper dictMapper;


    /**
     * 根据用户id获取所有角色
     *
     * @param userId 用户ID
     * @return
     */
    public List<Role> findAllRoleByUserId(Long userId) {
        return roleMapper.findAllRoleByUserId(userId);
    }

    /**
     * 根据用户id,角色编码判断是否拥有该角色
     *
     * @param userId   用户id
     * @param roleCode 角色编码
     * @return
     */
    public boolean hasRoleOrNotByUserIdAndRoleCode(Long userId, String roleCode) {
        Role role = roleMapper.findRoleByUserIdAndRoleType(userId, roleCode);
        if (ObjectUtils.isNotEmpty(role)) {
            return true;
        }
        return false;
    }

    public Resp edit(RoleEditDTO roleEditModel) {
        Role role = roleMapper.findByRoleCode(roleEditModel.getRoleCode());
        if (null != role && null == roleEditModel.getId()) {
            return Resp.FAILURE("角色编号已存在!");
        } else if (null != role &&
                null != roleEditModel.getId() &&
                !role.getId().equals(roleEditModel.getId()) &&
                role.getRoleCode().equals(roleEditModel.getRoleCode())) {
            return Resp.FAILURE("角色编号已存在!");
        }
        Boolean isEdit = null != roleEditModel.getId();
        Role roleModel = new Role();
        if (isEdit) {
            roleModel = roleMapper.selectById(roleEditModel.getId());
            roleModel.setVersion(roleEditModel.getVersion());
        }
        roleModel.setRoleName(roleEditModel.getRoleName());
        roleModel.setRoleCode(roleEditModel.getRoleCode());
        roleModel.setRemark(roleEditModel.getRemark());
        roleModel.setGroupId(roleEditModel.getGroupId());
        roleModel.setRoleType(roleEditModel.getRoleType());
        roleModel.setRemark(roleEditModel.getRemark());
        if (isEdit) {
            roleMapper.updateById(roleModel);
        } else {
            roleMapper.insert(roleModel);
        }
        return Resp.SUCCESS(roleModel);
    }

    public Resp delete(Long id) {
        roleMapper.deleteById(id);
        return Resp.SUCCESS();
    }

    public Resp<RoleFetchDTO> fetch(Long id) {
        Role role = roleMapper.selectById(id);
        if (null == role) {
            return Resp.FAILURE("记录不存在");
        }
        RoleFetchDTO roleFetchModel = RoleFetchDTO.builder()
                .roleCode(role.getRoleCode())
                .groupId(role.getGroupId())
                .roleName(role.getRoleName())
                .roleType(role.getRoleType() + "")
                .version(role.getVersion())
                .remark(role.getRemark())
                .id(role.getId()).build();
        return Resp.SUCCESS(roleFetchModel);
    }

    /**
     * 获取分组树
     */
    public List<TreeNodeDTO> TreeNodeModel() {

        //从数据字典获取分类
        Dict dict = dictMapper.findByValue(ROLE_TYPE_CODE);

        List<Dict> dicts = dictMapper.findByPidOrderBySortDesc(dict.getId());
        if (CollectionUtils.isEmpty(dicts)) {
            return Lists.newArrayList();
        }
        List<TreeNodeDTO> treeNodeModels = Lists.newArrayList();
        dicts.forEach(item -> {
            treeNodeModels.add(TreeNodeDTO.builder()
                    .key(item.getValue() + "")
                    .pid(item.getPid() + "")
                    .selectable(false)
                    .title(item.getText())
                    .build());
        });
        return RecursiveTools.forEachTreeItems(treeNodeModels, (TreeNodeDTO item) -> {
            List<Role> roleList = roleMapper.findByRoleType(Integer.valueOf(item.getKey()));
            if (CollectionUtils.isEmpty(roleList)) {
                item.setLeaf(true);
                return null;
            }
            List<TreeNodeDTO> list = Lists.newArrayList();
            roleList.forEach(role -> {
                list.add(TreeNodeDTO.builder()
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

    public List<RoleFetchDTO> list(RoleSearchDTO roleSearchModel) {
        return roleMapper.list(roleSearchModel);
    }
}
