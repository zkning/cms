package com.sophia.cms.rbac.service;

import com.august.rbac.domain.Group;
import com.august.rbac.domain.UserInfo;
import com.august.rbac.dto.GroupEditDTO;
import com.august.rbac.dto.GroupSearchDTO;
import com.august.rbac.dto.TreeNodeDTO;
import com.august.rbac.mapper.GroupMapper;
import com.august.rbac.mapper.UserInfoMapper;
import com.august.website.utils.RecursiveTools;
import com.august.website.utils.Resp;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupService {
    public static Long TOP_NODE = 0L;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 保存或更新用户
     *
     * @param gm
     * @return
     */
    public Resp<Group> saveOrUpdate(GroupEditDTO gm) {
        Group orig = groupMapper.findByGroupName(gm.getGroupName());

        if (null != orig && null == gm.getId()) {
            return Resp.FAILURE("分组名称已存在!");
        } else if (null != orig &&
                null != gm.getId() &&
                !orig.getId().equals(gm.getId()) &&
                orig.getGroupName().equals(gm.getGroupName())) {
            return Resp.FAILURE("分组名称已存在!");
        }
        Boolean isEdit = null != gm.getId();
        Group group = new Group();
        if (isEdit) {
            group = groupMapper.selectById(gm.getId());
            group.setVersion(gm.getVersion());
        }
        group.setPid(null == gm.getPid() ? TOP_NODE : gm.getPid());
        group.setExtra(gm.getExtra());
        group.setRemark(gm.getRemark());
        group.setGroupType(gm.getGroupType());
        group.setIsValid(gm.getIsValid());
        group.setGroupName(gm.getGroupName());
        if (isEdit) {
            groupMapper.updateById(group);
        } else {
            groupMapper.insert(group);
        }
        return Resp.SUCCESS(group);
    }

    /**
     * 删除组别
     *
     * @param id
     * @return
     */
    @Transactional
    public Resp delete(Long id) {
        Group group = this.groupMapper.selectById(id);
        if (ObjectUtils.isEmpty(group)) {
            return Resp.FAILURE("未知的分组!");
        }

        // 查询子级分组
        List<Group> subGroupList = RecursiveTools.forEachItem(group,
                item -> {
                    return groupMapper.findByPid(item.getId());
                });
        subGroupList.add(group);
        List<Long> gIds = subGroupList.stream().map(Group::getId).collect(Collectors.toList());

        // 查询分组下人员
        List<UserInfo> users = this.userInfoMapper.findByGroupIdIn(gIds);
        if (CollectionUtils.isNotEmpty(users)) {
            return Resp.FAILURE("当前分组或子分组下还有人员,请处理后再删除!");
        }
        groupMapper.deleteBatchIds(gIds);
        return Resp.SUCCESS();
    }

    /**
     * 获取分组树根据上级id
     */
    public List<TreeNodeDTO> getGroupTreeModel(Long pid) {
        List<Group> groups = groupMapper.findByPid(pid);
        if (CollectionUtils.isEmpty(groups)) {
            return new ArrayList<>();
        }
        List<TreeNodeDTO> treeNodeModels = new ArrayList<>();
        groups.forEach(group -> {
            treeNodeModels.add(TreeNodeDTO.builder()
                    .key(group.getId() + "")
                    .pid(group.getPid() + "")
                    .selectable(true)
                    .expanded(true)
                    .title(group.getGroupName())
                    .build());
        });
        return createTreeModel(treeNodeModels);
    }

    public Resp<GroupSearchDTO> findById(Long id) {
        Group group = groupMapper.selectById(id);
        if (null == group) {
            return Resp.FAILURE("记录不存在");
        }
        GroupSearchDTO groupQueryModel = GroupSearchDTO.builder()
                .groupName(group.getGroupName())
                .groupType(group.getGroupType())
                .extra(group.getExtra())
                .isValid(group.getIsValid())
                .pid(group.getPid() + "")
                .version(group.getVersion())
                .remark(group.getRemark())
                .id(group.getId() + "").build();
        return Resp.SUCCESS(groupQueryModel);
    }


    /**
     * 根据用户id获取权限角色树
     *
     * @param userId
     * @return
     */
    public List<TreeNodeDTO> getPermitTreeModel(Long userId) {
        List<Group> groups = this.getMaxGroupIdByUserId(userId);

        List<TreeNodeDTO> treeNodeModels = new ArrayList<>();
        groups.forEach(group -> {
            treeNodeModels.add(TreeNodeDTO.builder()
                    .key(group.getId() + "")
                    .pid(group.getPid() + "")
                    .selectable(true)
                    .expanded(true)
                    .title(group.getGroupName())
                    .build());
        });
        return createTreeModel(treeNodeModels);
    }

    private List<TreeNodeDTO> createTreeModel(List<TreeNodeDTO> treeNodeModels) {
        return RecursiveTools.forEachTreeItems(treeNodeModels, (TreeNodeDTO item) -> {
            List<Group> groupList = groupMapper.findByPid(Long.valueOf(item.getKey()));
            if (CollectionUtils.isEmpty(groupList)) {
                item.setLeaf(true);
                return null;
            }
            List<TreeNodeDTO> list = new ArrayList<>();
            groupList.forEach(group -> {
                list.add(TreeNodeDTO.builder()
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
        List<Group> groupIds = new ArrayList<>();

        //获取用户所在分组
        Group userGroup = groupMapper.findByUserId(userId);

        //查询用户关联的所有角色的分组权限id
        List<Group> list = groupMapper.findRoleGroupIdByUserId(userId);
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
                Group pGroup = groupMapper.selectById(group.getPid());
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
