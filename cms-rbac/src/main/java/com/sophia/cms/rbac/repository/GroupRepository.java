package com.sophia.cms.rbac.repository;

import com.sophia.cms.rbac.domain.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liangyonghua
 * @date 2018/7/13 14:55
 */
@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {

    /**
     * 根据父id查询所有组id
     *
     * @param id 上级id
     * @return
     */
    @Query(value = "select id from Group where pid = ?1")
    List<Long> findIdsByPid(Long id);

    List<Group> findByPid(Long id);

    /**
     * 查询id是ids的组别
     *
     * @param ids
     * @return
     */
    List<Group> findByIdIn(List ids);

    /**
     * 根据id查询组别
     *
     * @param id
     * @return
     */
    Group findById(Long id);



    Group findByGroupName(String groupName);

    /**
     * 根据id跟父id查询组别
     *
     * @param id
     * @param pid
     * @return
     */
    Group findByIdAndPid(Long id, Long pid);


    /**
     * 查询用户拥有角色的分组权限
     * @param userId
     * @return
     */
    @Query(value = "SELECT t.* FROM t_rbac_group t where id in (select b.group_id from t_rbac_role b join t_rbac_user_role_relation r on b.id = r.role_id where r.user_id = ?1)"
            , nativeQuery = true)
    List<Group> findRoleGroupIdByUserId(Long userId);


    /**
     * 根据用户id获取分组
     * @param userId
     * @return
     */
    @Query(value = "SELECT t.* FROM t_rbac_group t join t_rbac_user r on t.id = r.group_id where r.id = ?1"
            , nativeQuery = true)
    Group findByUserId(Long userId);
}
