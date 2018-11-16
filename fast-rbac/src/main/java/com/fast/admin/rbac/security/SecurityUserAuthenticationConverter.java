package com.fast.admin.rbac.security;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.Map;

/**
 * 资源服务器解析token
 */
public class SecurityUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    public static final String USER_DETAILS = "user";

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) super.extractAuthentication(map);

        // 判断是否包含自定义参数
        if (map.containsKey(USER_DETAILS) && null != usernamePasswordAuthenticationToken) {
            AdditionalInfo tokenUserDetailModel = new AdditionalInfo();
            new ModelMapper().map(map.get(USER_DETAILS), tokenUserDetailModel);
            usernamePasswordAuthenticationToken.setDetails(tokenUserDetailModel);
        }
        return usernamePasswordAuthenticationToken;
    }
}

