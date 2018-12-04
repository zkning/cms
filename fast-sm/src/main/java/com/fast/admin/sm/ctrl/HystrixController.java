package com.fast.admin.sm.ctrl;

import com.fast.admin.sm.service.impl.HystrixHttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    @Autowired
    HystrixHttpService hystrixService;

    @GetMapping(value = "/permit/baidu")
    public String baidu() {
        return hystrixService.httpRequestForBaidu("www.baidu.com");
    }

    @GetMapping(value = "/permit/google")
    public String google() {
        return hystrixService.httpRequestForGoogle("www.google.com");
    }
}
