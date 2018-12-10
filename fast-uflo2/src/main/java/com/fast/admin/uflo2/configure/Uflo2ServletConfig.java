package com.fast.admin.uflo2.configure;

import com.bstek.uflo.console.UfloServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Uflo2ServletConfig {

    @Bean
    public ServletRegistrationBean ufloServlet() {
        return new ServletRegistrationBean(new UfloServlet(), "/uflo/*");
    }
}
