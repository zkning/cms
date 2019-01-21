package com.sophia.cms.rbac.shiro;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * Token session manager
 */
public class AccessTokenSessionManager extends DefaultWebSessionManager {
    public static final String AccessToken = "token";

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {

        // 从请求头中获取token
        String accessToken = WebUtils.toHttp(request).getHeader(AccessToken);

        // 判断是否有值
        if (StringUtils.isNoneBlank(accessToken)) {

            // 设置当前session状态
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ShiroHttpServletRequest.URL_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, accessToken);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return accessToken;
        }
        return super.getSessionId(request, response);
    }
}
