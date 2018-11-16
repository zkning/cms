package com.fast.admin.rbac.utils;

import com.fast.admin.rbac.domain.RbacUserInfo;
import com.fast.admin.rbac.security.AdditionalInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class SessionContextHolder {

    public static RbacUserInfo getShiroUser() {
        Subject subject = SecurityUtils.getSubject();
        return (RbacUserInfo) subject.getPrincipals().getPrimaryPrincipal();
    }

    public static RbacUserInfo getCurrentUser() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

        if (null != oAuth2Authentication.getUserAuthentication()) {
            return new ModelMapper().map(oAuth2Authentication.getUserAuthentication().getPrincipal(), RbacUserInfo.class);
        }
        /* jwt
        if (null != oAuth2Authentication.getUserAuthentication()) {
            return new ModelMapper().map(oAuth2Authentication.getUserAuthentication().getDetails(), RbacUserInfo.class);
        }*/
        return null;
    }

    public static AdditionalInfo me() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        if (null != oAuth2Authentication.getUserAuthentication()) {
            return new ModelMapper().map(oAuth2Authentication.getUserAuthentication().getDetails(), AdditionalInfo.class);
        }
        return null;
    }
}
