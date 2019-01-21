package com.sophia.cms.rbac.repository;

import com.sophia.cms.rbac.domain.RoleResourcesRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liangyonghua
 * @date 2018/7/13 17:23
 */
@Repository
public interface RoleResourcesRelationRepository extends JpaRepository<RoleResourcesRelation, Long> {

    /**
     * 根据用户ID查询资源
     * @param userId
     * @return
     */
    @Query(value = "select resource_id from t_rbac_role_resource_relation where role_id in (select role_id from t_rbac_user_role_relation where user_id = ?1)",nativeQuery = true)
    List<Long> findResourecesIdsByUserId(Long userId);

    /**
     * 查询角色拥有的资源
     * @param roleId
     * @return
     */
    List<RoleResourcesRelation> findByRoleId(Long roleId);

    /**
     * 删除资源id为resourceId的中间关联数据
     * @param resourceId
     */
    void deleteByResourceIdIn(List<Long> resourceId);
}
