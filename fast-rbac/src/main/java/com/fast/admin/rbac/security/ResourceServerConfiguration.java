package com.fast.admin.rbac.security;

import com.fast.admin.framework.cors.BootstrapCorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Slf4j
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    ResourceServerProperties resourceServerProperties;

//    @Autowired
//    TokenStore jwtTokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceServerProperties.getResourceId()).stateless(true);
//        resources.tokenStore(jwtTokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers().anyRequest()
                .and()
                .authorizeRequests()
                .antMatchers(WebSecurityConfig.antPatterns.toArray(new String[WebSecurityConfig.antPatterns.size()])).permitAll()
                .anyRequest().authenticated();
        http.headers().frameOptions().sameOrigin().disable();
        http.cors().configurationSource(new BootstrapCorsConfigurationSource());
    }
}
