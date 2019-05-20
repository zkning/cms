package com.sophia.cms.rbac.mapper;


import com.august.rbac.domain.RoleResRelation;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleResRelationMapper extends BaseMapper<RoleResRelation> {

    /**
     * 根据用户ID查询资源
     *
     * @param userId
     * @return
     */
    List<Long> findResourecesIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询角色拥有的资源
     *
     * @param roleId
     * @return
     */
    List<RoleResRelation> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除资源id为resId的中间关联数据
     *
     * @param resId
     */
    void deleteByresIdIn(@Param("resIds") List resIds);
}
