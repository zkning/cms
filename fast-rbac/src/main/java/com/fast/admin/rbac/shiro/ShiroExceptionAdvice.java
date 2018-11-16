package com.fast.admin.rbac.shiro;


import com.fast.admin.framework.constants.StatusCodeEnum;
import com.fast.admin.framework.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class ShiroExceptionAdvice {

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseBody
    public Response authenticationException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Exception ex) {
        log.error(httpServletRequest.getServletPath() + ": 用户认证失败！");
        return Response.FAILURE(StatusCodeEnum.SYSTEM_ERROR.getCode(), StatusCodeEnum.SYSTEM_ERROR.getMessage());
    }

    @ExceptionHandler(value = AuthorizationException.class)
    public void authorizationException(HttpServletResponse httpServletResponse, Exception ex) throws IOException {
        httpServletResponse.setCharacterEncoding(CharEncoding.UTF_8);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //未授权
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public void unauthorizedException(HttpServletResponse httpServletResponse, Exception ex) throws IOException {
        httpServletResponse.setCharacterEncoding(CharEncoding.UTF_8);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //拒绝访问
        httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
