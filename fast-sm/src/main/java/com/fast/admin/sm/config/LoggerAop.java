package com.fast.admin.sm.config;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
public class LoggerAop {

    private static String CONNECTOR = ".";

    @Pointcut("execution(* com.fast.admin.*.ctrl..*.*(..))")
    public void executionContoller() {
    }

    @Before(value = "executionContoller()")
    public void doBefore(JoinPoint joinPoint) {
        String requestId = String.valueOf(System.currentTimeMillis());
        MDC.put("TRACE_ID", requestId);
        if (null != joinPoint.getArgs() && joinPoint.getArgs().length > 0) {
            String method = joinPoint.getTarget().getClass().getName() + CONNECTOR + joinPoint.getSignature().getName();
            log.info("{} Request：{}", method, JSONObject.toJSONString(joinPoint.getArgs()));
        }
    }

    @AfterReturning(pointcut = "executionContoller()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        MDC.clear();
    }
}
