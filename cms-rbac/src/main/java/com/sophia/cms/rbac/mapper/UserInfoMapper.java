package com.sophia.cms.rbac.mapper;

import com.august.rbac.domain.UserInfo;
import com.august.rbac.dto.UserInfoFetchDTO;
import com.august.rbac.dto.UserInfoSearchDTO;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    List<UserInfoFetchDTO> list(Page page, @Param("params") UserInfoSearchDTO rbacUserInfoSearchModel);

    UserInfo findByUserName(@Param("userName") String userName);

    UserInfo findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    List<UserInfo> findByGroupIdIn(@Param("gids") List gids);

    List<UserInfo> findByGroupId(@Param("gid") Long gid);

    List<UserInfo> findByIdIn(@Param("uids") List uids);
}
