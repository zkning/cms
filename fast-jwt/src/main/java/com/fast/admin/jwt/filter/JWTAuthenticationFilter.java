package com.fast.admin.jwt.filter;

import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.constants.StatusCodeEnum;
import com.fast.admin.framework.response.Response;
import com.fast.admin.jwt.config.WebSecurityConfig;
import com.fast.admin.jwt.service.TokenAuthenticationService;
import com.fast.admin.framework.util.SpringContextUtil;
import org.apache.commons.codec.CharEncoding;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截所有需要JWT的请求，然后调用TokenAuthenticationService类的静态方法去做JWT验证
 * Created by ningzuokun on 2017/12/18.
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        if (hasPermit((HttpServletRequest) request)) {
            filterChain.doFilter(request, response);
            return;
        }

        TokenAuthenticationService tokenAuthenticationService = SpringContextUtil.getBean(TokenAuthenticationService.class);
        Response<Authentication> authenticationResponse = tokenAuthenticationService.getAuthentication((HttpServletRequest) request);
        if (authenticationResponse.checkSuccess()) {
            SecurityContextHolder.getContext().setAuthentication(authenticationResponse.getResult());
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding(CharEncoding.UTF_8);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (StatusCodeEnum.INSUFFICIENT_PRIVILEGES.getCode().equals(authenticationResponse.getCode())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        httpServletResponse.getWriter().println(JSONObject.toJSONString(authenticationResponse));
    }

    private boolean hasPermit(HttpServletRequest request) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String url : WebSecurityConfig.antPatterns) {
            if (antPathMatcher.match(url, request.getServletPath())) {
                return true;
            }
        }
        return false;
    }
}