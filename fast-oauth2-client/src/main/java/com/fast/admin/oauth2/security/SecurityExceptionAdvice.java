package com.fast.admin.oauth2.security;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class SecurityExceptionAdvice {

    @ExceptionHandler(value = AccessDeniedException.class)
    public void unauthorizedException(HttpServletResponse httpServletResponse, Exception ex) throws IOException {
        httpServletResponse.setCharacterEncoding(CharEncoding.UTF_8);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
