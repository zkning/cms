package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sophia.cms.rbac.domain.RoleResourcesRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleResourcesRelationMapper extends BaseMapper<RoleResourcesRelation> {

    /**
     * 根据用户ID查询资源
     *
     * @param userId
     * @return
     */
    @Select(value = "select resource_id from t_rbac_role_resource_relation where role_id in (select role_id from t_rbac_user_role_relation where user_id = #{userId})")
    List<Long> findResourecesIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询角色拥有的资源
     *
     * @param roleId
     * @return
     */
    @Select("select * from t_rbac_role_resource_relation where role_id = #{roleId}")
    List<RoleResourcesRelation> findByRoleId(Long roleId);

    /**
     * 删除资源id为resourceId的中间关联数据
     *
     * @param resourceId
     */
    void deleteByResourceIdIn(@Param("resourceIdList") List<Long> resourceIdList);
}
