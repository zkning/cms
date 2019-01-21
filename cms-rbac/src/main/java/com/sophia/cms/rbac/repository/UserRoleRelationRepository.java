package com.sophia.cms.rbac.repository;

import com.sophia.cms.rbac.domain.UserRoleRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liangyonghua
 * @date 2018/7/13 17:56
 */
@Repository
public interface UserRoleRelationRepository extends JpaRepository<UserRoleRelation, Long> {

    /**
     * 根据用户id查询所拥有的角色
     * 本地sql会返回Long会当成bigInt类型
     * @param userId
     * @return
     */
    @Query(value = "select roleId from UserRoleRelation where user_id = ?1")
    List<Long> findByUserId(Long userId);

    /**
     * 根据用户id查询所拥有的角色
     *
     * @param roleId 角色id
     * @return
     */
    @Query(value = "select user_id from t_rbac_user_role_relation where role_id = ?1", nativeQuery = true)
    List<Long> findUserIdsByRoleId(Long roleId);

    /**
     * 根据角色id查询数据
     *
     * @param roleId
     * @return
     */
    List<UserRoleRelation> findByRoleId(Long roleId);

    /**
     * 根据角色id跟用户id删除中间表
     *
     * @param RoleId
     * @param userId
     */
    void deleteByRoleIdAndUserIdIn(Long RoleId, List userId);


    void deleteByUserIdAndRoleIdIn(Long userId, List RoleIds);
}
