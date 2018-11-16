package com.fast.admin.rbac.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {
    private static final String XRequestedWith = "X-Requested-With";
    private static final String XMLHttpRequest = "XMLHttpRequest";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (this.isLoginRequest(request, response)) {
            if (this.isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }

                return this.executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }

                return true;
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the Authentication url [" + this.getLoginUrl() + "]");
            }
            if (isAjax(WebUtils.toHttp(request))) {
                HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                this.saveRequestAndRedirectToLogin(request, response);
            }
            return false;
        }
    }

    boolean isAjax(HttpServletRequest request) {
        return (request.getHeader(XRequestedWith) != null && XMLHttpRequest.equals(request.getHeader(XRequestedWith).toString())) ||
                null != request.getHeader(AccessTokenSessionManager.AccessToken);
    }
}
