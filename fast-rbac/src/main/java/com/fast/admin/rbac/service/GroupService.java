package com.fast.admin.rbac.service;

import com.fast.admin.framework.response.Response;
import com.fast.admin.framework.util.BeanUtils;
import com.fast.admin.rbac.domain.Group;
import com.fast.admin.rbac.domain.RbacUserInfo;
import com.fast.admin.rbac.model.GroupEditModel;
import com.fast.admin.rbac.model.GroupSearchModel;
import com.fast.admin.rbac.model.TreeNodeModel;
import com.fast.admin.rbac.repository.GroupRepository;
import com.fast.admin.rbac.repository.RbacUserInfoRepository;
import com.fast.admin.rbac.utils.RecursiveTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupService {

    public static Long TOP_NODE = 0L;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private RbacUserInfoRepository rbacUserInfoRepository;

    /**
     * 保存或更新用户
     *
     * @param groupModel
     * @return
     */
    public Response<Group> saveOrUpdate(GroupEditModel groupModel) {
        Group orgGroup = groupRepository.findByGroupName(groupModel.getGroupName());

        // 创建
        if (null != orgGroup && null == groupModel.getId()) {
            return Response.FAILURE("分组名称已存在!");
        } else if (null != orgGroup &&
                null != groupModel.getId() &&
                !orgGroup.getId().equals(groupModel.getId()) &&
                orgGroup.getGroupName().equals(groupModel.getGroupName())) {
            return Response.FAILURE("分组名称已存在!");
        }
        Group group = new Group();
        if (null != groupModel.getId()) {
            group = groupRepository.findOne(groupModel.getId());
            group.setVersion(groupModel.getVersion());
        }
        if (null == groupModel.getPid()) {
            group.setPid(0L);
        } else {
            group.setPid(groupModel.getPid());
        }
        group.setExtra(groupModel.getExtra());
        group.setRemark(groupModel.getRemark());
        group.setGroupType(groupModel.getGroupType());
        group.setIsValid(groupModel.getIsValid());

        group.setGroupName(groupModel.getGroupName());
        groupRepository.save(group);
        return Response.SUCCESS(group);
    }

    /**
     * 删除组别
     *
     * @param id
     * @return
     */
    @Transactional
    public Response delete(Long id) {
        Group group = this.groupRepository.findOne(id);
        if (BeanUtils.isEmpty(group)) {
            return Response.FAILURE("未知的分组!");
        }
        // 查询子级分组
        List<Group> subGroupList = RecursiveTools.forEachItem(group,
                new RecursiveTools.ItemCircleRule<Group>() {
                    @Override
                    public List<Group> findChildren(Group item) {
                        return groupRepository.findByPid(item.getId());
                    }
                });
        subGroupList.add(group);
        List<Long> groupIds = subGroupList.stream().map(Group::getId).collect(Collectors.toList());
        List<RbacUserInfo> users = this.rbacUserInfoRepository.findByGroupIdIn(groupIds);
        if (BeanUtils.isNotEmpty(users)) {
            return Response.FAILURE("当前分组或子分组下还有人员,请处理后再删除!");
        }
        groupRepository.delete(subGroupList);
        return Response.SUCCESS();
    }

    /**
     * 获取分组树根据上级id
     */
    public List<TreeNodeModel> getGroupTreeModel(Long pid) {
        List<Group> groups = groupRepository.findByPid(pid);
        if (CollectionUtils.isEmpty(groups)) {
            return Lists.newArrayList();
        }
        List<TreeNodeModel> treeNodeModels = Lists.newArrayList();
        groups.forEach(group -> {
            treeNodeModels.add(TreeNodeModel.builder()
                    .key(group.getId() + "")
                    .pid(group.getPid() + "")
                    .selectable(true)
                    .expanded(true)
                    .title(group.getGroupName())
                    .build());
        });
        return createTreeModel(treeNodeModels);
    }

    public Response<GroupSearchModel> findById(Long id) {
        Group group = groupRepository.findOne(id);
        if (null == group) {
            return Response.FAILURE("记录不存在");
        }
        GroupSearchModel groupQueryModel = GroupSearchModel.builder()
                .groupName(group.getGroupName())
                .groupType(group.getGroupType())
                .extra(group.getExtra())
                .isValid(group.getIsValid())
                .pid(group.getPid() + "")
                .version(group.getVersion())
                .remark(group.getRemark())
                .id(group.getId() + "").build();
        return Response.SUCCESS(groupQueryModel);
    }


    /**
     * 根据用户id获取权限角色树
     *
     * @param userId
     * @return
     */
    public List<TreeNodeModel> getPermitTreeModel(Long userId) {
        List<Group> groups = this.getMaxGroupIdByUserId(userId);

        List<TreeNodeModel> treeNodeModels = Lists.newArrayList();
        groups.forEach(group -> {
            treeNodeModels.add(TreeNodeModel.builder()
                    .key(group.getId() + "")
                    .pid(group.getPid() + "")
                    .selectable(true)
                    .expanded(true)
                    .title(group.getGroupName())
                    .build());
        });
        return createTreeModel(treeNodeModels);
    }

    private List<TreeNodeModel> createTreeModel(List<TreeNodeModel> treeNodeModels) {
        return RecursiveTools.forEachTreeItems(treeNodeModels, (TreeNodeModel item) -> {
            List<Group> groupList = groupRepository.findByPid(Long.valueOf(item.getKey()));
            if (CollectionUtils.isEmpty(groupList)) {
                item.setLeaf(true);
                return null;
            }
            List<TreeNodeModel> list = Lists.newArrayList();
            groupList.forEach(group -> {
                list.add(TreeNodeModel.builder()
                        .key(group.getId() + "")
                        .selectable(true)
                        .title(group.getGroupName())
                        .pid(group.getPid() + "")
                        .build());
            });
            item.setChildren(list);
            return item.getChildren();
        });
    }


    /**
     * 获取当前用户拥有的最大机构权限
     */
    public List<Group> getMaxGroupIdByUserId(Long userId) {
        List<Group> groupIds = Lists.newArrayList();

        //获取用户所在分组
        Group userGroup = groupRepository.findByUserId(userId);

        //查询用户关联的所有角色的分组权限id
        List<Group> list = groupRepository.findRoleGroupIdByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            groupIds.add(userGroup);
            return groupIds;
        } else {
            list.add(userGroup);
        }
        list.forEach(item -> {

            // 计算全路径
            List<Group> groups = RecursiveTools.forEachItem(item, (Group group) -> {

                // 找上级
                Group pGroup = groupRepository.findById(group.getPid());
                return Lists.newArrayList(pGroup);
            });

            // level设置
            item.setLevel(groups.size());
        });

        // 升序排序
        Collections.sort(list);
        int level = 0;
        for (int index = 0; index < list.size(); index++) {

            // 大于级别终止循环
            if (index > 0 && list.get(index).getLevel() >= level) {
                break;
            }
            level = list.get(index).getLevel();
            groupIds.add(list.get(index));
        }
        return groupIds;
    }
}
