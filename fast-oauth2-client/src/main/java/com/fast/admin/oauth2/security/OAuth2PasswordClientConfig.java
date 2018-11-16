package com.fast.admin.oauth2.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Configuration

// 开启security aop
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class OAuth2PasswordClientConfig extends ResourceServerConfigurerAdapter {
    private static final String public_key = "pubkey.txt";
    private static final String permit = "/**/permit/**";

    @Autowired
    ResourceServerProperties resourceServerProperties;

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceServerProperties.getResourceId());
//        resources.tokenStore(new RedisTokenStore(redisConnectionFactory));
//        resources.tokenServices(clientTokenServices());
    }

    /**
     ／／　jwtToken　配置

    @Bean
    public DefaultTokenServices clientTokenServices() {
//            使用 DefaultTokenServices 在资源服务器本地配置令牌存储、解码、解析方式
//            使用 RemoteTokenServices 资源服务器通过 HTTP 请求来解码令牌，每次都请求授权服务器端点 /oauth/check_token
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(clientAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter clientAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 对称加密
//            converter.setSigningKey("123");

        // 设置用于解码的非对称加密的公钥
        converter.setVerifierKey(obtainPubKey());
        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        defaultAccessTokenConverter.setUserTokenConverter(new SecurityUserAuthenticationConverter());
        converter.setAccessTokenConverter(defaultAccessTokenConverter);
        return converter;
    }

    // 获取本地公钥
    private String obtainPubKey() {
        Resource resource = new ClassPathResource(public_key);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String pubKey = br.lines().collect(Collectors.joining("\n"));
            log.info("客户端JWT公钥:{}", pubKey);
            return pubKey;
        } catch (IOException ioe) {
            return obtainKeyFromAuthorizationServer();
        }
    }

    // 从授权服务器获取公钥
    private String obtainKeyFromAuthorizationServer() {
        ObjectMapper objectMapper = new ObjectMapper();
        String pubKey = new RestTemplate().getForObject(resourceServerProperties.getJwt().getKeyUri(), String.class);
        try {
            Map map = objectMapper.readValue(pubKey, Map.class);
            String pubKeyValue = map.get("value").toString();
            log.info("授权服务器JWT公钥:{}", pubKeyValue);
            return pubKeyValue;
        } catch (IOException e) {
            log.error("授权服务器公钥获取失败", e);
        }
        return null;
    }
     **/

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // Since we want the protected resources to be accessible in the UI as well we need
                // session creation to be allowed (it's disabled by default in 2.0.6)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .requestMatchers().anyRequest()
                .and()
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers(permit).permitAll()
                .anyRequest().authenticated();//配置order访问控制，必须认证过后才可以访问
    }
}
