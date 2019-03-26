package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sophia.cms.rbac.domain.Resources;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ResourcesMapper extends BaseMapper<Resources> {


    /**
     * 根据code查询资源
     *
     * @param code 资源编码
     * @return
     */
    @Select("select * from t_rbac_resource where code = #{code}")
    Resources findByCode(@Param("code") String code);


    // 根据用户角色查询所有资源
    @Select(value = "select trr.* from t_rbac_resource trr where EXISTS (select ts.resource_id from (select trrrr.resource_id from t_rbac_role_resource_relation trrrr \n" +
            "where EXISTS (select trrur.role_id from t_rbac_user_role_relation trrur where trrur.user_id = #{userId} and trrur.role_id = trrrr.role_id)) ts where ts.resource_id = trr.id)  ")
    List<Resources> findResourceByRoleUserId(@Param("userId") Long userId);


    @Select("select * from t_rbac_resource where resource_type = #{resourceType}")
    List<Resources> findByResourceType(@Param("resourceType") Integer resType);


    /**
     * 获取所有资源根据用户id，资源类型
     *
     * @param userId
     * @param type
     * @return
     */
    @Select(value = "select trr.* from t_rbac_resource trr where EXISTS (select ts.resource_id from (select trrrr.resource_id from t_rbac_role_resource_relation trrrr \n" +
            "where EXISTS (select trrur.role_id from t_rbac_user_role_relation trrur where trrur.user_id = #{userId} and " +
            "trrur.role_id = trrrr.role_id)) ts where ts.resource_id = trr.id) and resource_type = #{type} ")
    List<Resources> findResourceByRoleUserIdAndType(@Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 根据角色id查询对应的资源
     *
     * @param roleId
     * @return
     */
    @Select(value = "SELECT t.* FROM t_rbac_resource t " +
            " join t_rbac_role_resource_relation r on t.id = r.resource_id  where r.role_id = #{roleId}")
    List<Resources> findByRoleId(@Param("roleId") Long roleId);

    @Select("select * from t_rbac_resource where pid = #{pid}")
    List<Resources> findByPid(@Param("pid") Long pid);
}
