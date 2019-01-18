package com.fast.admin.jwt.config;
import com.sophia.cms.framework.cors.BootstrapCorsConfigurationSource;
import com.fast.admin.jwt.filter.JWTLoginFilter;
import com.fast.admin.jwt.filter.JWTAuthenticationFilter;
import com.fast.admin.jwt.provider.CustomAuthenticationProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningzuokun on 2017/12/18.
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final List<String> antPatterns = new ArrayList<>();

    static {
        antPatterns.add("/druid/**");
        antPatterns.add("/swagger-ui.html");
        antPatterns.add("/swagger-resources/**");
        antPatterns.add("/swagger-resources/**");
        antPatterns.add("/webjars/**");
        antPatterns.add("/v2/api-docs/**");
        antPatterns.add("/**/permit/**");
    }

    /**
     * 设置 HTTP 验证规则
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 关闭csrf验证
        http.csrf().disable()

                // 对请求进行认证
                .authorizeRequests()
                .antMatchers(antPatterns.toArray(new String[antPatterns.size()])).permitAll()

                // 所有请求需要身份认证
                .anyRequest().authenticated()
                .and()

                // 添加一个过滤器 所有访问 /login 的请求交给 JWTLoginFilter 来处理 这个类处理所有的JWT相关内容
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)

                // 添加一个过滤器验证其他请求的Token是否合法
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().sameOrigin().disable();
        http.cors().configurationSource(new BootstrapCorsConfigurationSource());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义身份验证组件
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }

    /**
     * 添加排除拦截的url
     *
     * @param url
     */
    public static void addAntPattern(String url) {
        antPatterns.add(url);
    }
}
