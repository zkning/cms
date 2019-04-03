package com.sophia.cms.framework.exception;

import com.sophia.cms.framework.constants.StatusCodeEnum;
import com.sophia.cms.framework.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zkning
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Response<?> handleBisException(ServiceException e) {
        log.error("Server RuntimeException", e);
        return Response.FAILURE(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e) {
        log.error("Server Exception", e);
        return Response.FAILURE(StatusCodeEnum.SERVICE_UNACCESSABLE.getCode(), StatusCodeEnum.SERVICE_UNACCESSABLE.getMessage());
    }
}
