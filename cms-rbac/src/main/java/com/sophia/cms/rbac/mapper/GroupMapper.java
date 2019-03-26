package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sophia.cms.rbac.domain.Group;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GroupMapper extends BaseMapper<Group> {

    @Select("select id from t_rbac_group where pid = #{id}")
    List<Long> findIdsByPid(@Param("id") Long id);

    @Select("select * from t_rbac_group where pid = #{pid}")
    List<Group> findByPid(@Param("pid") Long pid);


    @Select("select * from t_rbac_group where group_name like #{groupName}")
    Group findByGroupName(@Param("groupName") String groupName);

    /**
     * 查询用户拥有角色的分组权限
     *
     * @param userId
     * @return
     */
    @Select("SELECT t.* FROM t_rbac_group t " +
            "where id in (select b.group_id from t_rbac_role b " +
            "join t_rbac_user_role_relation r on b.id = r.role_id where r.user_id = #{userId})")
    List<Group> findRoleGroupIdByUserId(@Param("userId") Long userId);


    /**
     * 根据用户id获取分组
     *
     * @param userId
     * @return
     */
    @Select("SELECT t.* FROM t_rbac_group t join t_rbac_user r on t.id = r.group_id where r.id = #{userId}")
    Group findByUserId(@Param("userId") Long userId);
}
