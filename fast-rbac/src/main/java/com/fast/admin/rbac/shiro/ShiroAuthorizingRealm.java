package com.fast.admin.rbac.shiro;

import com.fast.admin.rbac.domain.RbacUserInfo;
import com.fast.admin.rbac.domain.Resources;
import com.fast.admin.rbac.service.RbacUserService;
import com.fast.admin.rbac.service.ResourcesService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 用于进行权限信息的验证
 */
public class ShiroAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    RbacUserService rbacUserService;

    @Autowired
    ResourcesService resourcesService;

    /**
     * 授权访问控制，用于对用户进行的操作进行认证授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        RbacUserInfo rbacUserInfo = (RbacUserInfo) principalCollection.getPrimaryPrincipal();

        //查询权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<Resources> resources = resourcesService.findAllResourcesByUserId(rbacUserInfo.getId());
        for (Resources res : resources) {
            simpleAuthorizationInfo.addStringPermission(res.getCode());
        }
        return simpleAuthorizationInfo;
    }

    /**
     * 验证用户身份
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //获取用户输入的token
        String username = (String) authenticationToken.getPrincipal();
        RbacUserInfo rbacUserInfo = rbacUserService.findByUserName(username);
        if (null == rbacUserInfo) {
            return null;
        }
        //放入shiro.调用CredentialsMatcher检验密码
        return new SimpleAuthenticationInfo(rbacUserInfo, rbacUserInfo.getPassword(), this.getClass().getName());
    }

    public void clearAuthorizationInfo(String username) {
        SimplePrincipalCollection pc = new SimplePrincipalCollection();
        pc.add(username, super.getName());
        super.clearCachedAuthorizationInfo(pc);
    }

}
