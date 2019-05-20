package com.sophia.cms.rbac.mapper;


import com.august.rbac.domain.UserRoleRelation;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleRelationMapper extends BaseMapper<UserRoleRelation> {

    /**
     * 根据用户id查询所拥有的角色
     * 本地sql会返回Long会当成bigInt类型
     *
     * @param userId
     * @return
     */
    List<Long> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户id查询所拥有的角色
     *
     * @param roleId 角色id
     * @return
     */
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色id查询数据
     *
     * @param roleId
     * @return
     */
    List<UserRoleRelation> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色id跟用户id删除中间表
     * @param roleId
     * @param userIds
     */
    void deleteByRoleIdAndUserIdIn(@Param("roleId") Long roleId, @Param("userIds") List userIds);


    void deleteByUserIdAndRoleIdIn(@Param("userId") Long userId, @Param("roleIds") List roleIds);

}
