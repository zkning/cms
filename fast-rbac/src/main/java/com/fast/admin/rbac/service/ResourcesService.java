package com.fast.admin.rbac.service;

import com.fast.admin.framework.response.Response;
import com.fast.admin.framework.util.BeanUtils;
import com.fast.admin.rbac.constants.ResourceTypeEnum;
import com.fast.admin.rbac.domain.RbacUserInfo;
import com.fast.admin.rbac.domain.Resources;
import com.fast.admin.rbac.domain.RoleResourcesRelation;
import com.fast.admin.rbac.domain.UserRoleRelation;
import com.fast.admin.rbac.model.*;
import com.fast.admin.rbac.repository.ResourcesRepository;
import com.fast.admin.rbac.repository.RoleResourcesRelationRepository;
import com.fast.admin.rbac.repository.UserRoleRelationRepository;
import com.fast.admin.rbac.utils.RecursiveTools;
import com.fast.admin.rbac.utils.SessionContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private ResourcesRepository resourcesRepository;
    @Autowired
    private RoleResourcesRelationRepository roleResourcesRelationRepository;
    @Autowired
    private UserRoleRelationRepository userRoleRelationRepository;

    private static final String ADMINISTRATOR = "administrator";

    /**
     * 资源新增及修改
     *
     * @param model
     * @return
     */
    public Response<Resources> edit(ResourceEditModel model) {
        Resources resources = this.resourcesRepository.findByCode(model.getCode());
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
        if (null != model.getId()) {
            resouce = resourcesRepository.findOne(model.getId());
        }
        if (null != model.getPid()) {
            resouce.setPid(model.getPid());
        } else {
            resouce.setPid(0L);
        }
        resouce.setCode(model.getCode());
        resouce.setIcon(model.getIcon());
        resouce.setExtra(model.getExtra());
        resouce.setLink(model.getLink());
        resouce.setPid(model.getPid());
        resouce.setExternalLink(model.getExternalLink());
        resouce.setResourceType(model.getResourceType());
        resouce.setText(model.getText());
        resouce.setVersion(model.getVersion());
        Resources res = resourcesRepository.save(resouce);
        return Response.SUCCESS(res);
    }

    @Transactional
    public Response delete(Long id) {
        Resources item = this.resourcesRepository.findOne(id);
        if (BeanUtils.isEmpty(item)) {
            return Response.FAILURE("未知的资源!");
        }

        // 查询子级分组
        List<Resources> subItems = RecursiveTools.forEachItem(item, (Resources index) -> {
            return resourcesRepository.findByPid(index.getId());
        });
        subItems.add(item);
        resourcesRepository.delete(subItems);

        //删除已关联的角色关联关系
        List<Long> resourcesIds = subItems.stream().map(Resources::getId).collect(Collectors.toList());
        roleResourcesRelationRepository.deleteByResourceIdIn(resourcesIds);
        return Response.SUCCESS();
    }

    public Response<ResouceFetchModel> fetch(Long id) {
        Resources item = resourcesRepository.findOne(id);
        if (null == item) {
            return Response.FAILURE("记录不存在");
        }
        ResouceFetchModel model = new ResouceFetchModel();
        model.setId(item.getId());
        model.setCode(item.getCode());
        model.setVersion(item.getVersion());
        model.setExternalLink(item.getExternalLink());
        model.setExtra(item.getExtra());
        model.setIcon(item.getIcon());
        model.setLink(item.getLink());
        model.setPid(item.getPid());
        model.setResourceType(item.getResourceType());
        model.setText(item.getText());
        return Response.SUCCESS(model);
    }


    /**
     * 根据用户ID获取所有资源
     *
     * @param userId 用户id
     * @return
     */
    public List<Resources> findAllResourcesByUserId(Long userId) {
        List<Resources> resources = this.resourcesRepository.findResourceByRoleUserId(userId);
        return resources;
    }

    /**
     * 根据用户ID获取指定类型资源
     *
     * @param userId
     * @return
     */
    public List<Resources> findResourcesByUserIdAndType(Long userId, Integer type) {
        List<Resources> resources = this.resourcesRepository.findResourceByRoleUserIdAndType(userId, type);
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
            List<Resources> resources = resourcesRepository.findByRoleId(roleId);
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
        List<Resources> resourcesList = resourcesRepository.findByPid(pid);
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
            List<Resources> resList = resourcesRepository.findByPid(Long.valueOf(item.getKey()));

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
        List<RoleResourcesRelation> roleResourcesRelations = this.roleResourcesRelationRepository.findByRoleId(saveRoleResModel.getRoleId());
        if (CollectionUtils.isNotEmpty(roleResourcesRelations)) {
            this.roleResourcesRelationRepository.delete(roleResourcesRelations);
        }
        //保存新关系
        ArrayList<RoleResourcesRelation> list = Lists.newArrayList();
        Long roleId = saveRoleResModel.getRoleId();
        saveRoleResModel.getResourceIds().forEach((Long resourceId) -> {
            RoleResourcesRelation roleResourcesRelation = new RoleResourcesRelation();
            roleResourcesRelation.setRoleId(roleId);
            roleResourcesRelation.setResourceId(resourceId);
            list.add(roleResourcesRelation);
        });
        this.roleResourcesRelationRepository.save(list);
        return Response.SUCCESS();
    }

    /**
     * 关联角色用户
     *
     * @param saveRoleResModel
     * @return
     */
    public Response saveRoleUser(EditRoleUserModel saveRoleResModel) {

        //保存新关系
        ArrayList<UserRoleRelation> list = Lists.newArrayList();
        Long roleId = saveRoleResModel.getRoleId();
        saveRoleResModel.getUserIds().forEach((Long userId) -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setRoleId(roleId);
            userRoleRelation.setUserId(userId);
            list.add(userRoleRelation);
        });
        this.userRoleRelationRepository.save(list);
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
        ArrayList<UserRoleRelation> list = Lists.newArrayList();
        Long userId = editUserRolesModel.getUserId();
        editUserRolesModel.getRoleIds().forEach((Long roleId) -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setRoleId(roleId);
            userRoleRelation.setUserId(userId);
            list.add(userRoleRelation);
        });
        this.userRoleRelationRepository.save(list);
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
        this.userRoleRelationRepository.deleteByRoleIdAndUserIdIn(editRoleUserModel.getRoleId(), editRoleUserModel.getUserIds());
        return Response.SUCCESS();
    }

    @Transactional
    public Response removeUserRoles(EditUserRolesModel editUserRolesModel) {
        this.userRoleRelationRepository.deleteByUserIdAndRoleIdIn(editUserRolesModel.getUserId(), editUserRolesModel.getRoleIds());
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
        RbacUserInfo rbacUserInfo = SessionContextHolder.getCurrentUser();
        accountInfoModel.setUser(rbacUserInfo);

        //登录返回菜单列表
        Response<GetMenuModel> resultResponse = getMenuItems(rbacUserInfo);
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

    public Response<GetMenuModel> getMenuItems(RbacUserInfo rbacUserInfo) {
        GetMenuModel getMenuResult = new GetMenuModel();

        // 查找用户指定类型资源列表
        List<Resources> resources = Lists.newArrayList();
        if (ADMINISTRATOR.equalsIgnoreCase(rbacUserInfo.getUserName())) {
            resources = resourcesRepository.findByResourceType(ResourceTypeEnum.MENU.getCode());
        } else {
            resources = this.findResourcesByUserIdAndType(rbacUserInfo.getId(), ResourceTypeEnum.MENU.getCode());
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
