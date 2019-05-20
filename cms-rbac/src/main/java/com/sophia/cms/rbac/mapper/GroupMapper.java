package com.sophia.cms.rbac.mapper;

import com.august.rbac.domain.Group;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    List<Long> findIdsByPid(@Param("pid") Long pid);

    List<Group> findByPid(@Param("pid") Long pid);

    List<Group> findByIdIn(@Param("ids") List ids);

    Group findByGroupName(@Param("groupName") String groupName);

    Group findByIdAndPid(@Param("id") Long id, @Param("pid") Long pid);


    /**
     * 查询用户拥有角色的分组权限
     *
     * @param userId
     * @return
     */
    List<Group> findRoleGroupIdByUserId(@Param("userId") Long userId);


    /**
     * 根据用户id获取分组
     *
     * @param userId
     * @return
     */
    Group findByUserId(@Param("userId") Long userId);
}
