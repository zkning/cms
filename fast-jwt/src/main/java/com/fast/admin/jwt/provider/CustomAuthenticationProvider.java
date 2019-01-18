package com.fast.admin.jwt.provider;

import com.sophia.cms.framework.response.Response;
import com.sophia.cms.framework.util.SpringContextUtil;
import com.fast.admin.jwt.service.JwtLoginService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author zkning
 * 自定义身份认证验证组件
 * Created by ningzuokun on 2017/12/18.
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 获取认证的用户名 & 密码
        JwtLoginService jwtLoginService = SpringContextUtil.getBean(JwtLoginService.class);

        // 认证逻辑
        Response tokenInfoResponse = jwtLoginService.findByUserNameAndPassword(username, password);
        if (!tokenInfoResponse.checkSuccess()) {
            throw new BadCredentialsException(tokenInfoResponse.getMessage());
        }

        // 生成令牌
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, password, null);

        //存储用户详细信息
        usernamePasswordAuthenticationToken.setDetails(tokenInfoResponse.getResult());
        return usernamePasswordAuthenticationToken;
    }

    /**
     * 是否可以提供输入类型的认证服务
     *
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}