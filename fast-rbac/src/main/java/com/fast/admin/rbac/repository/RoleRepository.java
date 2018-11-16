package com.fast.admin.rbac.repository;

import com.fast.admin.rbac.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lenovo on 2017/11/11.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据用户id查询该用户下的所有角色
     *
     * @param userId 用户id
     * @return
     */
    @Query(value = "select trr.* from t_rbac_role trr where EXISTS (select trurr.role_id from t_rbac_user_role_relation trurr where trurr.user_id = :userId and trurr.role_id = trr.id) ", nativeQuery = true)
    List<Role> findAllRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID跟角色码获取角色
     *
     * @param userId   用户ID
     * @param roleCode 角色编码
     * @return
     */
    @Query(value = "select trr.* from t_rbac_role trr where EXISTS (select trurr.role_id from t_rbac_user_role_relation trurr where trurr.user_id = :userId and trurr.role_id = trr.id and trr.role_code = :roleCode", nativeQuery = true)
    Role findRoleByUserIdAndRoleType(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return
     */
    Role findByRoleCode(String roleCode);

    List<Role> findByRoleType(Integer roleType);
}
