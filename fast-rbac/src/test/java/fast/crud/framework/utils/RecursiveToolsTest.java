package fast.crud.framework.utils;

import com.alibaba.fastjson.JSONObject;
import com.fast.admin.rbac.domain.Group;
import com.fast.admin.rbac.utils.RecursiveTools;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

public class RecursiveToolsTest {
    private static final List<Group> groupList = Lists.newArrayList();

    static {
        Group group = Group.builder().pid(0L).groupName("合众财富金融投资管理有限公司").build();
        group.setId(1L);
        groupList.add(group);
        group = Group.builder().pid(1L).groupName("风控中心").build();
        group.setId(2L);
        groupList.add(group);
        group = Group.builder().pid(2L).groupName("信审部").build();
        group.setId(3L);
        groupList.add(group);
        group = Group.builder().pid(1L).groupName("技术中心").build();
        group.setId(4L);
        groupList.add(group);
        group = Group.builder().pid(4L).groupName("引流组").build();
        group.setId(5L);
        groupList.add(group);
        group = Group.builder().pid(4L).groupName("风控组").build();
        group.setId(6L);
        groupList.add(group);
    }

    @Test
    public void forEachTreeItems() {
        List<Group> groupArray = Lists.newArrayList();
        Group group = new Group();
        group.setId(1L);
        group.setGroupName("1");
        groupArray.add(group);

        RecursiveTools.forEachTreeItems(groupArray, (Group groupItem) -> {
                    List<Group> groups = Lists.newArrayList();
                    groupList.forEach(item -> {
                        if (groupItem.getId() == item.getPid()) {
                            groups.add(item);
                        }
                    });
                    groupItem.setChildren(groups);
                    return groupItem.getChildren();
                }
        );
        System.out.println(JSONObject.toJSONString(groupArray));
    }

    @Test
    public void forEachItem() {
        Group item = new Group();
        item.setId(1L);
        item.setGroupName("00000");
        List<Group> list = RecursiveTools.forEachItem(item, (Group groupItem) -> {
            List<Group> groups = Lists.newArrayList();
            groupList.forEach(group -> {
                if (group.getPid() == groupItem.getId()) {
                    groups.add(group);
                }
            });
            return groups;
        });
        System.out.println(JSONObject.toJSONString(list));
    }

    @Test
    public void forEachTreeItem() {

        Group item = new Group();
        item.setId(1L);
        item.setGroupName("00000");

        RecursiveTools.forEachTreeItem(item, (Group group) -> {
            List<Group> groups = Lists.newArrayList();
            groupList.forEach(groupItem -> {
                if (group.getPid() == groupItem.getId()) {
                    groups.add(group);
                }
            });
            group.setChildren(groups);
            return group.getChildren();
        });
        System.out.println(JSONObject.toJSONString(item));
    }


    @Test
    public void forEachItems() {

        List<Group> groupLists = Lists.newArrayList();
        Group group = Group.builder().pid(1L).groupName("技术中心").build();
        group.setId(4L);
        groupLists.add(group);
        group = Group.builder().pid(1L).groupName("风控中心").build();
        group.setId(2L);
        groupLists.add(group);

        List<Group> result = RecursiveTools.forEachItems(groupLists, (Group item) -> {
            List<Group> groups = Lists.newArrayList();
            groupList.forEach(groupItem -> {
                if (groupItem.getPid() == item.getId()) {
                    groups.add(groupItem);
                }
            });
            return groups;
        });
        System.out.println(JSONObject.toJSONString(result));
    }
}