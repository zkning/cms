package com.sophia.cms.rbac.service;

import com.sophia.cms.framework.response.Response;
import com.sophia.cms.framework.util.BeanUtils;
import com.sophia.cms.rbac.constants.ResourceTypeEnum;
import com.sophia.cms.rbac.domain.Resources;
import com.sophia.cms.rbac.domain.RoleResourcesRelation;
import com.sophia.cms.rbac.domain.UserRoleRelation;
import com.sophia.cms.rbac.mapper.ResourcesMapper;
import com.sophia.cms.rbac.mapper.RoleResourcesRelationMapper;
import com.sophia.cms.rbac.mapper.UserRoleRelationMapper;
import com.sophia.cms.rbac.model.*;
import com.sophia.cms.rbac.security.OAuth2Principal;
import com.sophia.cms.rbac.utils.RecursiveTools;
import com.sophia.cms.rbac.utils.SessionContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lenovo on 2017/11/11.
 */
@Service
public class ResourcesService {
    private static final Long TOP_NODE = 0L;

    @Autowired
    private ResourcesMapper resourcesMapper;
    @Autowired
    private RoleResourcesRelationMapper roleResourcesRelationMapper;
    @Autowired
    private UserRoleRelationMapper userRoleRelationMapper;

    private static final String ADMINISTRATOR = "administrator";

    /**
     * 资源新增及修改
     *
     * @param model
     * @return
     */
    public Response<Resources> edit(ResourceEditModel model) {
        Resources resources = this.resourcesMapper.findByCode(model.getCode());
        // 创建
        if (null != resources && null == model.getId()) {
            return Response.FAILURE("资源编码存在!");
        } else if (null != resources &&
                null != model.getId() &&
                !resources.getId().equals(model.getId()) &&
                resources.getCode().equals(model.getCode())) {
            return Response.FAILURE("资源编码已存在!");
        }
        Resources resouce = new Resources();
        Boolean flag = null != model.getId();
        if (flag) {
            resouce = resourcesMapper.selectById(model.getId());
        }
        if (null != model.getPid()) {
            resouce.setPid(model.getPid());
        } else {
            resouce.setPid(0L);
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(model, resouce);
        if (flag) {
            resourcesMapper.updateById(resouce);
        } else {
            resourcesMapper.insert(resouce);
        }
        return Response.SUCCESS(resouce);
    }

    @Transactional
    public Response delete(Long id) {
        Resources item = this.resourcesMapper.selectById(id);
        if (BeanUtils.isEmpty(item)) {
            return Response.FAILURE("未知的资源!");
        }

        // 查询子级分组
        List<Resources> subItems = RecursiveTools.forEachItem(item, (Resources index) -> {
            return resourcesMapper.findByPid(index.getId());
        });
        subItems.add(item);

        //删除已关联的角色关联关系
        List<Long> resourcesIds = subItems.stream().map(Resources::getId).collect(Collectors.toList());
        resourcesMapper.deleteBatchIds(resourcesIds);
        roleResourcesRelationMapper.deleteByResourceIdIn(resourcesIds);
        return Response.SUCCESS();
    }

    public Response<ResouceFetchModel> fetch(Long id) {
        Resources item = resourcesMapper.selectById(id);
        if (null == item) {
            return Response.FAILURE("记录不存在");
        }
        ResouceFetchModel model = new ResouceFetchModel();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(item, model);
        return Response.SUCCESS(model);
    }


    /**
     * 根据用户ID获取所有资源
     *
     * @param userId 用户id
     * @return
     */
    public List<Resources> findAllResourcesByUserId(Long userId) {
        List<Resources> resources = this.resourcesMapper.findResourceByRoleUserId(userId);
        return resources;
    }

    /**
     * 根据用户ID获取指定类型资源
     *
     * @param userId
     * @return
     */
    public List<Resources> findResourcesByUserIdAndType(Long userId, Integer type) {
        List<Resources> resources = this.resourcesMapper.findResourceByRoleUserIdAndType(userId, type);
        return resources;
    }

    /**
     * 根据角色id获取资源
     *
     * @param roleId
     * @return
     */
    public List<TreeNodeModel> getRoleResTree(Long roleId) {

        // 根据角色id获取资源
        List<Long> resIdList = Lists.newArrayList();
        if (null != roleId) {
            List<Resources> resources = resourcesMapper.findByRoleId(roleId);
            if (CollectionUtils.isNotEmpty(resources)) {
                resources.forEach(item -> {
                    resIdList.add(item.getId());
                });
            }
        }
        return this.TreeNodeModel(TOP_NODE, resIdList);
    }

    /**
     * 获取资源tree
     */
    public List<TreeNodeModel> TreeNodeModel(Long pid, List<Long> resIdList) {
        List<Resources> resourcesList = resourcesMapper.findByPid(pid);
        if (CollectionUtils.isEmpty(resourcesList)) {
            return Lists.newArrayList();
        }
        List<TreeNodeModel> treeNodeModels = Lists.newArrayList();
        resourcesList.forEach(item -> {
            treeNodeModels.add(TreeNodeModel.builder()
                    .key(item.getId() + "")
                    .pid(item.getPid() + "")
                    .selectable(true)
                    .expanded(true)
//                    .checked(resIdList.contains(item.getId()))
                    .title(item.getText())
                    .build());
        });
        return RecursiveTools.forEachTreeItems(treeNodeModels, (TreeNodeModel item) -> {
            List<Resources> resList = resourcesMapper.findByPid(Long.valueOf(item.getKey()));

            // 不存在子节点
            if (CollectionUtils.isEmpty(resList)) {
                item.setLeaf(true);

                // 页子节点判断是否选中
                item.setChecked(resIdList.contains(Long.valueOf(item.getKey())));
                return null;
            }
            List<TreeNodeModel> list = Lists.newArrayList();
            resList.forEach(res -> {
                list.add(TreeNodeModel.builder()
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
    public Response saveRoleRes(SaveRoleResModel saveRoleResModel) {

        //删除旧数据
        List<RoleResourcesRelation> roleResourcesRelations = this.roleResourcesRelationMapper.findByRoleId(saveRoleResModel.getRoleId());
        if (CollectionUtils.isNotEmpty(roleResourcesRelations)) {
            List<Long> roleResourcesRelationIds = roleResourcesRelations.stream().map(RoleResourcesRelation::getId).collect(Collectors.toList());
            this.roleResourcesRelationMapper.deleteBatchIds(roleResourcesRelationIds);
        }

        //保存新关系
        Long roleId = saveRoleResModel.getRoleId();
        saveRoleResModel.getResourceIds().forEach((Long resourceId) -> {
            RoleResourcesRelation roleResourcesRelation = new RoleResourcesRelation();
            roleResourcesRelation.setRoleId(roleId);
            roleResourcesRelation.setResourceId(resourceId);
            roleResourcesRelation.setCreateTime(new Date());
            roleResourcesRelation.setLastUpdateTime(new Date());
            roleResourcesRelationMapper.insert(roleResourcesRelation);
        });
        return Response.SUCCESS();
    }

    /**
     * 关联角色用户
     *
     * @param saveRoleResModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Response saveRoleUser(EditRoleUserModel saveRoleResModel) {

        //保存新关系
        Long roleId = saveRoleResModel.getRoleId();
        saveRoleResModel.getUserIds().forEach((Long userId) -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setRoleId(roleId);
            userRoleRelation.setUserId(userId);
            userRoleRelation.setCreateTime(new Date());
            userRoleRelation.setLastUpdateTime(new Date());
            this.userRoleRelationMapper.insert(userRoleRelation);
        });
        return Response.SUCCESS();
    }

    /**
     * 关联角色用户
     *
     * @param editUserRolesModel
     * @return
     */
    public Response saveUserRoles(EditUserRolesModel editUserRolesModel) {

        //保存新关系
        Long userId = editUserRolesModel.getUserId();
        editUserRolesModel.getRoleIds().forEach((Long roleId) -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setRoleId(roleId);
            userRoleRelation.setUserId(userId);
            userRoleRelation.setLastUpdateTime(new Date());
            userRoleRelation.setCreateTime(new Date());
            userRoleRelationMapper.insert(userRoleRelation);
        });
        return Response.SUCCESS();
    }

    /**
     * 移除角色用户管理
     *
     * @param editRoleUserModel
     * @return
     */
    @Transactional
    public Response removeRoleUser(EditRoleUserModel editRoleUserModel) {
        this.userRoleRelationMapper.deleteByRoleIdAndUserIdIn(editRoleUserModel.getRoleId(), editRoleUserModel.getUserIds());
        return Response.SUCCESS();
    }

    @Transactional
    public Response removeUserRoles(EditUserRolesModel editUserRolesModel) {
        this.userRoleRelationMapper.deleteByUserIdAndRoleIdIn(editUserRolesModel.getUserId(), editUserRolesModel.getRoleIds());
        return Response.SUCCESS();
    }

    /**
     * 获取账户信息
     *
     * @param userId
     * @return
     */
    public AccountInfoModel getAccountInfo(Long userId) {
        AccountInfoModel accountInfoModel = new AccountInfoModel();

        //读取用户信息
        OAuth2Principal oAuth2Principal = SessionContextHolder.getPrincipal();
        accountInfoModel.setUser(oAuth2Principal);

        //登录返回菜单列表
        Response<GetMenuModel> resultResponse = getMenuItems(oAuth2Principal.getId(), oAuth2Principal.getUsername());
        accountInfoModel.setMenu(resultResponse.getResult().getResources());

        // 返回用户所有权限
        List<Resources> resources = findAllResourcesByUserId(userId);
        List<String> permits = new ArrayList<>();
        accountInfoModel.setPermits(permits);
        if (CollectionUtils.isNotEmpty(resources)) {
            resources.forEach(res -> {
                if (StringUtils.isNotBlank(res.getLink())) {
                    permits.add(res.getLink());
                }
            });
        }

        //aPP配置
        AccountInfoModel.App app = new AccountInfoModel.App();
        app.setName("");
        app.setDescription("");
        accountInfoModel.setApp(app);
        return accountInfoModel;
    }

    public Response<GetMenuModel> getMenuItems(Long userId, String userName) {
        GetMenuModel getMenuResult = new GetMenuModel();

        // 查找用户指定类型资源列表
        List<Resources> resources = Lists.newArrayList();
        if (ADMINISTRATOR.equalsIgnoreCase(userName)) {
            resources = resourcesMapper.findByResourceType(ResourceTypeEnum.MENU.getCode());
        } else {
            resources = this.findResourcesByUserIdAndType(userId, ResourceTypeEnum.MENU.getCode());
        }
        if (CollectionUtils.isEmpty(resources)) {
            return Response.SUCCESS(getMenuResult);
        }
        List<Resources> parentList = Lists.newArrayList();
        getMenuResult.setResources(parentList);

        //字节集合,key为parent
        Map<Long, List<Resources>> childMap = new HashedMap();
        for (Resources resource : resources) {

            //一级节点
            if (TOP_NODE.equals(resource.getPid())) {
                parentList.add(resource);
                continue;
            }

            //子节点集合
            if (childMap.containsKey(resource.getPid())) {
                childMap.get(resource.getPid()).add(resource);
            } else {
                List<Resources> childList = Lists.newArrayList();
                childList.add(resource);
                childMap.put(resource.getPid(), childList);
            }
        }
        RecursiveTools.forEachTreeItems(parentList, (Resources res) -> {
            if (!childMap.containsKey(res.getId())) {
                return Lists.newArrayList();
            }
            res.setChildren(childMap.get(res.getId()));
            return Lists.newArrayList();
        });
        return Response.SUCCESS(getMenuResult);
    }
}
