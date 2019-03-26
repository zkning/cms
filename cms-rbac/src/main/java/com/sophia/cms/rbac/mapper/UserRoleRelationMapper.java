package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sophia.cms.rbac.domain.UserRoleRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserRoleRelationMapper extends BaseMapper<UserRoleRelation> {

    /**
     * 根据用户id查询所拥有的角色
     * 本地sql会返回Long会当成bigInt类型
     *
     * @param userId
     * @return
     */
    @Select(value = "select role_id from t_rbac_user_role_relation where user_id = #{userId}")
    List<Long> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id查询所拥有的角色
     *
     * @param roleId 角色id
     * @return
     */
    @Select(value = "select user_id from t_rbac_user_role_relation where role_id = #{roleId}")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色id查询数据
     *
     * @param roleId
     * @return
     */
    @Select(value = "select * from t_rbac_user_role_relation where role_id = #{roleId}")
    List<UserRoleRelation> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色id跟用户id删除中间表
     *
     * @param RoleId
     * @param userId
     */
    void deleteByRoleIdAndUserIdIn(@Param("roleId") Long RoleId, @Param("userIds") List userIds);


    void deleteByUserIdAndRoleIdIn(@Param("userId") Long userId, @Param("roleIds") List roleIds);
}
