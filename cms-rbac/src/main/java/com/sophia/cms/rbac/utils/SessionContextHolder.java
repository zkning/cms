package com.sophia.cms.rbac.utils;

import com.sophia.cms.rbac.domain.RbacUserInfo;
import com.sophia.cms.rbac.security.OAuth2Principal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class SessionContextHolder {

    public static RbacUserInfo getShiroUser() {
        Subject subject = SecurityUtils.getSubject();
        return (RbacUserInfo) subject.getPrincipals().getPrimaryPrincipal();
    }

    public static OAuth2Principal getPrincipal() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

        if (null != oAuth2Authentication) {
            return (OAuth2Principal) oAuth2Authentication.getPrincipal();
        }
        /* jwt
        if (null != oAuth2Authentication.getUserAuthentication()) {
            return new ModelMapper().map(oAuth2Authentication.getUserAuthentication().getDetails(), RbacUserInfo.class);
        }*/
        return null;
    }
}
