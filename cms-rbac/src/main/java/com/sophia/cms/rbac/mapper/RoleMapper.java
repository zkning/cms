package com.sophia.cms.rbac.mapper;

import com.august.rbac.domain.Role;
import com.august.rbac.dto.RoleFetchDTO;
import com.august.rbac.dto.RoleSearchDTO;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<RoleFetchDTO> list(@Param("params") RoleSearchDTO roleSearchModel);

    List<Role> findAllRoleByUserId(@Param("userId") Long userId);

    Role findRoleByUserIdAndRoleType(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    Role findByRoleCode(@Param("roleCode") String roleCode);

    List<Role> findByRoleType(@Param("roleType") Integer roleType);
}
