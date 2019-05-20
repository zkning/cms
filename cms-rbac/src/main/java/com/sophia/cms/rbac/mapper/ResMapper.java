package com.sophia.cms.rbac.mapper;

import com.august.rbac.domain.Res;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * zkning
 */
@Mapper
public interface ResMapper extends BaseMapper<Res> {

    Res findByCode(@Param("code") String code);

    /**
     * 根据用户角色查询所有资源
     *
     * @param userId
     * @return
     */
    List<Res> findResourceByRoleUserId(@Param("userId") Long userId);

    List<Res> findByresType(@Param("resType") Integer resType);

    /**
     * 获取所有资源根据用户id，资源类型
     *
     * @param userId
     * @param resType
     * @return
     */
    List<Res> findResourceByRoleUserIdAndType(@Param("userId") Long userId, @Param("resType") Integer resType);

    /**
     * 根据角色id查询对应的资源
     *
     * @param roleId
     * @return
     */
    List<Res> findByRoleId(@Param("roleId") Long roleId);

    List<Res> findByPid(@Param("pid") Long pid);
}
