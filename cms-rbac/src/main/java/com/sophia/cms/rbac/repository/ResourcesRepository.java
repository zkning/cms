package com.sophia.cms.rbac.repository;

import com.sophia.cms.rbac.domain.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lenovo on 2017/11/11.
 */
@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    /**
     * 根据code查询资源
     *
     * @param code 资源编码
     * @return
     */
    Resources findByCode(String code);


    // 根据用户角色查询所有资源
    @Query(value = "select trr.* from t_rbac_resource trr where EXISTS (select ts.resource_id from (select trrrr.resource_id from t_rbac_role_resource_relation trrrr \n" +
            "where EXISTS (select trrur.role_id from t_rbac_user_role_relation trrur where trrur.user_id = ?1 and trrur.role_id = trrrr.role_id)) ts where ts.resource_id = trr.id)  ", nativeQuery = true)
    List<Resources> findResourceByRoleUserId(Long userId);


    List<Resources> findByResourceType(Integer resType);


    /**
     * 获取所有资源根据用户id，资源类型
     *
     * @param userId
     * @param type
     * @return
     */
    @Query(value = "select trr.* from t_rbac_resource trr where EXISTS (select ts.resource_id from (select trrrr.resource_id from t_rbac_role_resource_relation trrrr \n" +
            "where EXISTS (select trrur.role_id from t_rbac_user_role_relation trrur where trrur.user_id = ?1 and " +
            "trrur.role_id = trrrr.role_id)) ts where ts.resource_id = trr.id) and resource_type = ?2 ", nativeQuery = true)
    List<Resources> findResourceByRoleUserIdAndType(Long userId, Integer type);

    /**
     * 根据角色id查询对应的资源
     * @param roleId
     * @return
     */
    @Query(value = "SELECT t.* FROM t_rbac_resource t " +
            " join t_rbac_role_resource_relation r on t.id = r.resource_id  where r.role_id = ?1" ,nativeQuery = true)
    List<Resources> findByRoleId(Long roleId);

    List<Resources> findByPid(Long pid);
}
