package com.sophia.cms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by ningzuokun on 2017/5/15.
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
public class SmApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmApplication.class, args);
    }
}
