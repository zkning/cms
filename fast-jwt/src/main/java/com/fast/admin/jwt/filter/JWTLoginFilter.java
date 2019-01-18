package com.fast.admin.jwt.filter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sophia.cms.framework.response.Response;
import com.fast.admin.jwt.model.AccountCredentials;
import com.fast.admin.jwt.service.JwtLoginService;
import com.fast.admin.jwt.service.TokenAuthenticationService;
import com.sophia.cms.framework.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * attemptAuthentication - 登录时需要验证时候调用
 * successfulAuthentication - 验证成功后调用
 * unsuccessfulAuthentication - 验证失败后调用，这里直接灌入500错误返回，由于同一JSON返回，HTTP就都返回200了
 * Created by ningzuokun on 2017/12/18.
 */
@Slf4j
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    public JWTLoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {

        // JSON反序列化成 AccountCredentials
        AccountCredentials creds = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);
        this.checkCode(creds);

        // 返回一个验证令牌
        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword()));
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {
        TokenAuthenticationService tokenAuthenticationService = SpringContextUtil.getBean(TokenAuthenticationService.class);
        Serializable result = tokenAuthenticationService.addAuthentication(authentication);
        try {
            response.setCharacterEncoding(CharEncoding.UTF_8);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(JSONObject.toJSONString(Response.SUCCESS(result)));
        } finally {
            response.getWriter().close();
        }
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(JSONObject.toJSONString(Response.FAILURE(failed.getMessage())));
    }

    /**
     * 图形验证码
     *
     * @param creds
     */
    private void checkCode(AccountCredentials creds) {
        JwtLoginService jwtLoginService = SpringContextUtil.getBean(JwtLoginService.class);
        Response response = jwtLoginService.checkCode(creds);
        if (!response.checkSuccess()) {
            throw new AuthenticationServiceException(response.getMessage());
        }
    }
}