package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sophia.cms.rbac.domain.Role;
import com.sophia.cms.rbac.model.RoleFetchModel;
import com.sophia.cms.rbac.model.RoleSearchModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    List<RoleFetchModel> list(@Param("params") RoleSearchModel roleSearchModel);


    /**
     * 根据用户id查询该用户下的所有角色
     *
     * @param userId 用户id
     * @return
     */
    @Select(value = "select trr.* from t_rbac_role trr where EXISTS (select trurr.role_id from t_rbac_user_role_relation trurr where trurr.user_id = #{userId} and trurr.role_id = trr.id) ")
    List<Role> findAllRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID跟角色码获取角色
     *
     * @param userId   用户ID
     * @param roleCode 角色编码
     * @return
     */
    @Select("select trr.* from t_rbac_role trr where EXISTS (select trurr.role_id from t_rbac_user_role_relation trurr where trurr.user_id = #{userId} and trurr.role_id = trr.id and trr.role_code = #{roleCode}")
    Role findRoleByUserIdAndRoleType(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return
     */
    @Select("select * from t_rbac_role where role_code = #{roleCode}")
    Role findByRoleCode(String roleCode);

    @Select("select * from t_rbac_role where role_type = #{roleType}")
    List<Role> findByRoleType(Integer roleType);
}
