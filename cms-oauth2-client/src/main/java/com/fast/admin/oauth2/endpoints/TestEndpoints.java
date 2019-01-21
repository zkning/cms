package com.fast.admin.oauth2.endpoints;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@RestController
public class TestEndpoints {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        //单位为ms
        factory.setReadTimeout(5000);

        //单位为ms
        factory.setConnectTimeout(5000);
        return factory;
    }

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/permit/product/{id}")
    public String getProduct(@PathVariable String id) {
        //for debug
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "product id : " + id;
    }

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable String id) {
        //for debug
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
        String token = oAuth2AuthenticationDetails.getTokenValue();
        log.info("token {}", token);

        String url = "http://localhost:8081/hzed-admin/dict/tree?access_token=" + token;
        JSONObject json = restTemplate.getForEntity(url, JSONObject.class).getBody();
        log.info("授权服务器接口响应:{}", json.toJSONString());

        log.info("authentication:{}", JSONObject.toJSONString(authentication));
        return "order id : " + id;
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        //for debug
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication:{}", JSONObject.toJSONString(authentication));
        return "user id : " + id;
    }

    @GetMapping("/user")
    public Authentication me(Authentication user) {
        return user;
    }
}
