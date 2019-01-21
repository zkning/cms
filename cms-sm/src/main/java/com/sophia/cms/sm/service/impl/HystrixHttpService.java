package com.sophia.cms.sm.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HystrixHttpService {

    @HystrixCommand(fallbackMethod = "httpRequestForHttpFallback")
    public String httpRequestForBaidu(String params) {
        try {
            Thread.sleep(1000 * 3L);
        } catch (InterruptedException e) {
            log.error("httpRequestForBaidu休眠异常");
        }
        return "baidu";
    }

    @HystrixCommand(fallbackMethod = "httpRequestForHttpFallback")
    public String httpRequestForGoogle(String params) {
        log.info(this.getClass().getSimpleName());
        try {
            Thread.sleep(1000 * 3L);
        } catch (InterruptedException e) {
            log.error("httpRequestForGoogle休眠异常");
        }
        return "google";
    }

    private String httpRequestForHttpFallback(String params) {
        log.info("httpRequestForBaiduFallback fail result");
        return "fail result";
    }
}
