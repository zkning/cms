package com.fast.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by ningzuokun on 2017/5/15.
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
@EntityScan({"com.bstek.uflo.model","com.fast.admin"})
public class SmApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmApplication.class, args);
    }
}
