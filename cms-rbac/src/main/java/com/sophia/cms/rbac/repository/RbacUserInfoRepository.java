package com.sophia.cms.rbac.repository;

import com.sophia.cms.rbac.domain.RbacUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liangyonghua
 * @date 2018/7/13 16:28
 */
@Repository
public interface RbacUserInfoRepository extends JpaRepository<RbacUserInfo, Long>, JpaSpecificationExecutor<RbacUserInfo> {

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return
     */
    RbacUserInfo findByUserName(String userName);

    /**
     * 根据用户名跟密码查询用户
     *
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    RbacUserInfo findByUserNameAndPassword(String userName, String password);

    /**
     * 查询组id是groupId的所有用户
     *
     * @param groupId 组id
     * @return
     */
    List<RbacUserInfo> findByGroupIdIn(List groupId);

    /**
     * 根据组id查询用户
     *
     * @param groupId 组id
     * @return
     */
    List<RbacUserInfo> findByGroupId(Long groupId);

    /**
     * 查询id是userIds的所有用户
     *
     * @param userIds 用户id
     * @return
     */
    List<RbacUserInfo> findByIdIn(List userIds);

    /**
     * 根据id查询用户
     *
     * @param id 用户id
     * @return
     */
    RbacUserInfo findById(Long id);
}
