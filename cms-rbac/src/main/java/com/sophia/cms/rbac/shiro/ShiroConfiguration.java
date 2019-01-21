package com.sophia.cms.rbac.shiro;

import com.sophia.cms.framework.util.MD5Utils;
import com.sophia.cms.rbac.domain.RbacUserInfo;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;
import java.util.*;

//@Configuration
public class ShiroConfiguration {
    public static final List<String> antPatterns = new ArrayList<>();

    static {
        antPatterns.add("/druid/**");
        antPatterns.add("/swagger-ui.html");
        antPatterns.add("/swagger-resources/**");
        antPatterns.add("/webjars/**");
        antPatterns.add("/v2/api-docs/**");
        antPatterns.add("/**/permit/**");
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(@Qualifier(value = "securityManager") SecurityManager securityManager
    ) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);

        // 配置登录的url和登录成功的url
        bean.setLoginUrl("/login");
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put(ShiroConstants.authc, new ShiroFormAuthenticationFilter());
        bean.setFilters(filterMap);

        // 配置访问权限
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        antPatterns.forEach(item -> {
            filterChainDefinitionMap.put(item, ShiroConstants.anon);
        });
        filterChainDefinitionMap.put("/login", ShiroConstants.anon);
        filterChainDefinitionMap.put("/logout", ShiroConstants.anon);
        filterChainDefinitionMap.put("/*", ShiroConstants.authc);
        filterChainDefinitionMap.put("/**", ShiroConstants.authc);
        filterChainDefinitionMap.put("/*.*", ShiroConstants.authc);
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    @Bean
    public AccessTokenSessionManager accessTokenSessionManager(
            @Qualifier("redisSessionDao") RedisSessionDao redisSessionDao
    ) {
        AccessTokenSessionManager accessTokenSessionManager = new AccessTokenSessionManager();
        accessTokenSessionManager.setSessionDAO(redisSessionDao);
        return accessTokenSessionManager;
    }

    /**
     * 配置核心安全事务管理器
     */
    @Bean
    public SecurityManager securityManager(@Qualifier("authorizingRealm") AuthorizingRealm authorizingRealm,
                                           @Qualifier("accessTokenSessionManager") AccessTokenSessionManager accessTokenSessionManager,
                                           @Qualifier("redisCacheManager") RedisCacheManager redisCacheManager
    ) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(authorizingRealm);
        defaultWebSecurityManager.setSessionManager(accessTokenSessionManager);
        defaultWebSecurityManager.setCacheManager(redisCacheManager);
        return defaultWebSecurityManager;
    }

    /**
     * 配置自定义的权限登录器
     *
     * @param credentialsMatcher
     * @return
     */
    @Bean
    public AuthorizingRealm authorizingRealm(@Qualifier("credentialsMatcher") CredentialsMatcher credentialsMatcher) {
        AuthorizingRealm authRealm = new ShiroAuthorizingRealm();
        authRealm.setCredentialsMatcher(credentialsMatcher);

        // 开启缓存
        authRealm.setCachingEnabled(true);

        // 启用身份验证缓存，即缓存AuthenticationInfo，默认false
        authRealm.setAuthorizationCachingEnabled(true);

        // 启用授权缓存，即缓存AuthorizationInfo的信息，默认为false
        authRealm.setAuthorizationCachingEnabled(true);
        return authRealm;
    }

    /**
     * 配置自定义的密码比较器
     */
    @Bean
    public CredentialsMatcher credentialsMatcher() {
        return (AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) -> {
            UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
            RbacUserInfo rbacUserInfo = (RbacUserInfo) authenticationInfo.getPrincipals().getPrimaryPrincipal();
            return MD5Utils.string2MD5(new String(usernamePasswordToken.getPassword())).equals(rbacUserInfo.getPassword());
        };
    }

    /**
     * Shiro生命周期处理器
     *
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    /**
     * 使用@RequiresRoles,@RequiresPermissions等注解-
     *
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }


    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public RedisSessionDao redisSessionDao(@Qualifier("redisCacheManager") RedisCacheManager redisCacheManager) {
        RedisSessionDao redisSessionDao = new RedisSessionDao(ShiroConstants.timeout);
        redisSessionDao.setCacheManager(redisCacheManager);
        return redisSessionDao;
    }


    @Bean
    public RedisCacheManager redisCacheManager(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        return new RedisCacheManager(redisTemplate, ShiroConstants.timeout);
    }
}
