package com.fast.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Created by lenovo on 2018/6/3.
 */
@SpringBootApplication
@EnableCaching
public class RabcApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabcApplication.class, args);
    }
}
