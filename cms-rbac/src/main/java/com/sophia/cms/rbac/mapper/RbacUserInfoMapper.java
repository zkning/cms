package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.rbac.domain.RbacUserInfo;
import com.sophia.cms.rbac.model.RbacUserInfoFetchModel;
import com.sophia.cms.rbac.model.RbacUserInfoSearchModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RbacUserInfoMapper extends BaseMapper<RbacUserInfo> {

    List<RbacUserInfoFetchModel> list(Page page, @Param("params") RbacUserInfoSearchModel rbacUserInfoSearchModel);

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return
     */
    @Select("select * from t_rbac_user where user_name = #{userName}")
    RbacUserInfo findByUserName(@Param("userName") String userName);

    /**
     * 根据用户名跟密码查询用户
     */
    @Select("select * from t_rbac_user where user_name = #{userName} and password = #{password}")
    RbacUserInfo findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    /**
     * 查询组id是groupId的所有用户
     */
    List<RbacUserInfo> findByGroupIdIn(@Param("groupIdList") List groupIdList);

    /**
     * 根据组id查询用户
     */
    @Select("select * from t_rbac_user where group_id = #{groupId}")
    List<RbacUserInfo> findByGroupId(@Param("groupId") Long groupId);

    /**
     * 查询id是userIds的所有用户
     *
     * @param userIds 用户id
     * @return
     */
    List<RbacUserInfo> findByIdIn(@Param("userIds") List userIds);
}
