package com.sophia.cms.rbac.service;

import com.august.rbac.consts.ResTypeEnum;
import com.august.rbac.domain.Res;
import com.august.rbac.domain.RoleResRelation;
import com.august.rbac.domain.UserRoleRelation;
import com.august.rbac.dto.*;
import com.august.rbac.mapper.ResMapper;
import com.august.rbac.mapper.RoleResRelationMapper;
import com.august.rbac.mapper.UserRoleRelationMapper;
import com.august.website.utils.RecursiveTools;
import com.august.website.utils.Resp;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lenovo on 2017/11/11.
 */
@Service
public class ResService {
    private static final Long TOP_NODE = 0L;

    @Autowired
    private ResMapper resMapper;
    @Autowired
    private RoleResRelationMapper roleResRelationMapper;
    @Autowired
    private UserRoleRelationMapper userRoleRelationMapper;

    private static final String ADMINISTRATOR = "administrator";

    /**
     * 资源新增及修改
     *
     * @param model
     * @return
     */
    public Resp<Res> edit(ResEditDTO model) {
        Res resources = this.resMapper.findByCode(model.getCode());
        // 创建
        if (null != resources && null == model.getId()) {
            return Resp.FAILURE("资源编码存在!");
        } else if (null != resources &&
                null != model.getId() &&
                !resources.getId().equals(model.getId()) &&
                resources.getCode().equals(model.getCode())) {
            return Resp.FAILURE("资源编码已存在!");
        }
        Res res = new Res();
        Boolean isEdit = null != model.getId();
        if (isEdit) {
            res = resMapper.selectById(model.getId());
        }
        res.setPid(null != model.getPid() ? model.getPid() : 0L);
        res.setCode(model.getCode());
        res.setIcon(model.getIcon());
        res.setExtra(model.getExtra());
        res.setLink(model.getLink());
        res.setPid(model.getPid());
        res.setExtraLink(model.getExternalLink());
        res.setResType(model.getResType());
        res.setText(model.getText());
        res.setVersion(model.getVersion());
        if (isEdit) {
            resMapper.updateById(res);
        } else {
            resMapper.insert(res);
        }
        return Resp.SUCCESS(res);
    }

    @Transactional
    public Resp delete(Long id) {
        Res item = this.resMapper.selectById(id);
        if (ObjectUtils.isEmpty(item)) {
            return Resp.FAILURE("未知的资源!");
        }

        // 查询子级分组
        List<Res> subItems = RecursiveTools.forEachItem(item, (Res index) -> {
            return resMapper.findByPid(index.getId());
        });
        subItems.add(item);
        List<Long> rids = subItems.stream().map(Res::getId).collect(Collectors.toList());
        resMapper.deleteBatchIds(rids);

        // 删除已关联的角色关联关系
        List<Long> resIds = subItems.stream().map(Res::getId).collect(Collectors.toList());
        roleResRelationMapper.deleteByresIdIn(resIds);
        return Resp.SUCCESS();
    }

    public Resp<ResFetchDTO> fetch(Long id) {
        Res item = resMapper.selectById(id);
        if (null == item) {
            return Resp.FAILURE("记录不存在");
        }
        ResFetchDTO model = new ResFetchDTO();
        model.setId(item.getId());
        model.setCode(item.getCode());
        model.setVersion(item.getVersion());
        model.setExtraLink(item.getExtraLink());
        model.setExtra(item.getExtra());
        model.setIcon(item.getIcon());
        model.setLink(item.getLink());
        model.setPid(item.getPid());
        model.setResType(item.getResType());
        model.setText(item.getText());
        return Resp.SUCCESS(model);
    }


    /**
     * 根据用户ID获取所有资源
     *
     * @param userId 用户id
     * @return
     */
    public List<Res> findAllResourcesByUserId(Long userId) {
        List<Res> resources = this.resMapper.findResourceByRoleUserId(userId);
        return resources;
    }

    /**
     * 根据用户ID获取指定类型资源
     *
     * @param userId
     * @return
     */
    public List<Res> findResourcesByUserIdAndType(Long userId, Integer type) {
        List<Res> resources = this.resMapper.findResourceByRoleUserIdAndType(userId, type);
        return resources;
    }

    /**
     * 根据角色id获取资源
     *
     * @param roleId
     * @return
     */
    public List<TreeNodeDTO> getRoleResTree(Long roleId) {

        // 根据角色id获取资源
        List<Long> resIdList = Lists.newArrayList();
        if (null != roleId) {
            List<Res> resources = resMapper.findByRoleId(roleId);
            if (CollectionUtils.isNotEmpty(resources)) {
                resources.forEach(item -> {
                    resIdList.add(item.getId());
                });
            }
        }
        return this.getTreeNode(TOP_NODE, resIdList);
    }

    /**
     * 获取资源tree
     */
    private List<TreeNodeDTO> getTreeNode(Long pid, List<Long> resIdList) {
        List<Res> resourcesList = resMapper.findByPid(pid);
        if (CollectionUtils.isEmpty(resourcesList)) {
            return Lists.newArrayList();
        }
        List<TreeNodeDTO> treeNodeModels = Lists.newArrayList();
        resourcesList.forEach(item -> {
            treeNodeModels.add(TreeNodeDTO.builder()
                    .key(item.getId() + "")
                    .pid(item.getPid() + "")
                    .selectable(true)
                    .expanded(true)
//                    .checked(resIdList.contains(item.getId()))
                    .title(item.getText())
                    .build());
        });
        return RecursiveTools.forEachTreeItems(treeNodeModels, (TreeNodeDTO item) -> {
            List<Res> resList = resMapper.findByPid(Long.valueOf(item.getKey()));

            // 不存在子节点
            if (CollectionUtils.isEmpty(resList)) {
                item.setLeaf(true);

                // 页子节点判断是否选中
                item.setChecked(resIdList.contains(Long.valueOf(item.getKey())));
                return null;
            }
            List<TreeNodeDTO> list = Lists.newArrayList();
            resList.forEach(res -> {
                list.add(TreeNodeDTO.builder()
                        .key(res.getId() + "")
                        .selectable(true)
                        .title(res.getText())
                        .pid(res.getPid() + "")
//                        .checked(resIdList.contains(res.getId()))
                        .build());
            });
            item.setChildren(list);
            return item.getChildren();
        });
    }

    /**
     * 关联角色资源
     *
     * @param saveRoleResModel
     * @return
     */
    @Transactional
    public Resp saveRoleRes(SaveRoleResDTO saveRoleResModel) {

        //删除旧数据
        List<RoleResRelation> roleResRelations = this.roleResRelationMapper.findByRoleId(saveRoleResModel.getRoleId());
        if (CollectionUtils.isNotEmpty(roleResRelations)) {
            List<Long> ids = roleResRelations.stream().map(RoleResRelation::getId).collect(Collectors.toList());
            this.roleResRelationMapper.deleteBatchIds(ids);
        }
        //保存新关系
        Long roleId = saveRoleResModel.getRoleId();
        saveRoleResModel.getResIds().forEach((Long resId) -> {
            RoleResRelation roleResRelation = new RoleResRelation();
            roleResRelation.setRoleId(roleId);
            roleResRelation.setResId(resId);
            roleResRelationMapper.insert(roleResRelation);
        });
        return Resp.SUCCESS();
    }

    /**
     * 关联角色用户
     *
     * @param saveRoleResModel
     * @return
     */
    public Resp saveRoleUser(EditRoleUserDTO saveRoleResModel) {

        //保存新关系
        Long roleId = saveRoleResModel.getRoleId();
        saveRoleResModel.getUserIds().forEach((Long userId) -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setRoleId(roleId);
            userRoleRelation.setUserId(userId);
            userRoleRelationMapper.insert(userRoleRelation);
        });
        return Resp.SUCCESS();
    }

    /**
     * 关联角色用户
     *
     * @param editUserRolesModel
     * @return
     */
    public Resp saveUserRoles(EditUserRolesDTO editUserRolesModel) {

        //保存新关系
        Long userId = editUserRolesModel.getUserId();
        editUserRolesModel.getRoleIds().forEach((Long roleId) -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setRoleId(roleId);
            userRoleRelation.setUserId(userId);
            userRoleRelationMapper.insert(userRoleRelation);
        });
        return Resp.SUCCESS();
    }

    /**
     * 移除角色用户管理
     *
     * @param editRoleUserModel
     * @return
     */
    @Transactional
    public Resp removeRoleUser(EditRoleUserDTO editRoleUserModel) {
        this.userRoleRelationMapper.deleteByRoleIdAndUserIdIn(editRoleUserModel.getRoleId(), editRoleUserModel.getUserIds());
        return Resp.SUCCESS();
    }

    @Transactional
    public Resp removeUserRoles(EditUserRolesDTO editUserRolesModel) {
        this.userRoleRelationMapper.deleteByUserIdAndRoleIdIn(editUserRolesModel.getUserId(), editUserRolesModel.getRoleIds());
        return Resp.SUCCESS();
    }


    private Resp<List<Res>> getMenuItems(Long userId, String userName) {

        // 查找用户指定类型资源列表
        List<Res> resources = Lists.newArrayList();
        if (ADMINISTRATOR.equalsIgnoreCase(userName)) {
            resources = resMapper.findByresType(ResTypeEnum.MENU.getCode());
        } else {
            resources = this.findResourcesByUserIdAndType(userId, ResTypeEnum.MENU.getCode());
        }
        if (CollectionUtils.isEmpty(resources)) {
            return Resp.SUCCESS(resources);
        }
        List<Res> parentList = Lists.newArrayList();

        //字节集合,key为parent
        Map<Long, List<Res>> childMap = new HashedMap();
        for (Res resource : resources) {

            //一级节点
            if (TOP_NODE.equals(resource.getPid())) {
                parentList.add(resource);
                continue;
            }

            //子节点集合
            if (childMap.containsKey(resource.getPid())) {
                childMap.get(resource.getPid()).add(resource);
            } else {
                List<Res> childList = Lists.newArrayList();
                childList.add(resource);
                childMap.put(resource.getPid(), childList);
            }
        }
        RecursiveTools.forEachTreeItems(parentList, (Res res) -> {
            if (!childMap.containsKey(res.getId())) {
                return Lists.newArrayList();
            }
            res.setChildren(childMap.get(res.getId()));
            return Lists.newArrayList();
        });
        return Resp.SUCCESS(parentList);
    }
}
