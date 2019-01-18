package com.fast.admin.jwt.service;

import com.alibaba.fastjson.JSONObject;
import com.sophia.cms.framework.constants.StatusCodeEnum;
import com.sophia.cms.framework.response.Response;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ningzuokun on 2017/12/18.
 */
@Slf4j
@Service
public class TokenAuthenticationService {

    /**
     * 5天
     */
    static final long EXPIRATIONTIME = 1000 * 60 * 60 * 24 * 5;

    /**
     * JWT密码
     */
    static final String SECRET = "P@ssw02d";

    /**
     * Token前缀
     */
    static final String TOKEN_PREFIX = "Bearer";

    /**
     * 存放Token的Header Key
     */
    static final String HEADER_STRING = "token";
    static final String AUTHORITIES = "authorities";
    static final String USER_TOKEN = "userToken:";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    JwtLoginService jwtLoginService;

    /**
     * JWT生成方法
     */
    public Serializable addAuthentication(Authentication authentication) {

        //过期时间
        long expirationTimes = System.currentTimeMillis() + EXPIRATIONTIME;
        String username = authentication.getName();
        Object details = authentication.getDetails();

        // 生成JWT
        String JWTString = Jwts.builder()
                // 保存权限（角色）
                .claim(AUTHORITIES, "")
                // 用户名写入标题
                .setSubject(username)
                // 有效期设置
                .setExpiration(new Date(expirationTimes))
                // 签名设置
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        //将登陆用户信息，以字符串类型写入redis
        stringRedisTemplate.opsForValue().set(this.createtokenId(username),
                JSONObject.toJSONString(details),
                expirationTimes - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return jwtLoginService.getAccountCredentials(details, JWTString);
    }

    private String createtokenId(String username) {
        return USER_TOKEN + username;
    }

    public Claims httpServletRequestClaims(HttpServletRequest request) {

        // 从Header中拿到token
        String token = request.getHeader(HEADER_STRING);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        // 解析 Token
        Claims claims = Jwts.parser()
                // 验签
                .setSigningKey(SECRET)
                // 去掉 Bearer
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody();
        return claims;
    }


    /**
     * JWT验证方法
     *
     * @param httpServletRequest
     * @return
     */
    public Response<Authentication> getAuthentication(HttpServletRequest httpServletRequest) {
        Claims claims = null;
        try {
            claims = httpServletRequestClaims(httpServletRequest);
            if (null == claims) {

                //未登陆
                return Response.FAILURE(StatusCodeEnum.NEED_LOGIN.getCode(), StatusCodeEnum.NEED_LOGIN.getMessage());
            }
        } catch (ExpiredJwtException ex) {

            //已过期 Session失效
            return Response.FAILURE(StatusCodeEnum.SESSION_EXPIRED.getCode(), StatusCodeEnum.SESSION_EXPIRED.getMessage());
        }

        // 拿用户名
        String username = claims.getSubject();

        //校验redis subject用户账户,获取用户详细信息
        String tokenInfoJson = stringRedisTemplate.opsForValue().get(this.createtokenId(username));
        if (StringUtils.isBlank(tokenInfoJson)) {

            // Session失效
            return Response.FAILURE(StatusCodeEnum.SESSION_EXPIRED.getCode(), StatusCodeEnum.SESSION_EXPIRED.getMessage());
        }

        // 得到权限（角色）
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get(AUTHORITIES));

        // 返回验证令牌
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = StringUtils.isNotBlank(username) ?
                new UsernamePasswordAuthenticationToken(username, null, authorities) :
                null;
        usernamePasswordAuthenticationToken.setDetails(tokenInfoJson);
        return Response.SUCCESS(usernamePasswordAuthenticationToken);
    }
}
