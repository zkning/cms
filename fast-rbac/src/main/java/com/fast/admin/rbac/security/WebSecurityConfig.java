package com.fast.admin.rbac.security;

import com.fast.admin.framework.cors.BootstrapCorsConfigurationSource;
import org.assertj.core.util.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.List;

/**
 * Created by ningzuokun on 2017/12/18.
 */
@Configuration
@Order(-1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final List<String> antPatterns = Lists.newArrayList();

    static {
        antPatterns.add("/**/permit/**");
        antPatterns.add("/druid/**");
        antPatterns.add("/swagger-ui.html");
        antPatterns.add("/swagger-resources/**");
        antPatterns.add("/swagger-resources/**");
        antPatterns.add("/webjars/**");
        antPatterns.add("/v2/api-docs/**");
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
        http.csrf().disable();
        http
                .requestMatchers()
                .antMatchers("/login", "/oauth/authorize")
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**", "/home").authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/home")
                .loginPage("/login")
                .permitAll();
        http.headers().frameOptions().sameOrigin().disable();
        http.cors().configurationSource(new BootstrapCorsConfigurationSource());
        http.requestMatchers().antMatchers(HttpMethod.OPTIONS, "/oauth/token").and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(securityUserDetailService()).passwordEncoder(new Md5PasswordEncoder());
    }

    /**
     * 添加排除拦截的url
     *
     * @param url
     */
    public static void addAntPattern(String url) {
        antPatterns.add(url);
    }

    @Bean
    public SecurityUserDetailService securityUserDetailService() {
        return new SecurityUserDetailService();
    }
}
