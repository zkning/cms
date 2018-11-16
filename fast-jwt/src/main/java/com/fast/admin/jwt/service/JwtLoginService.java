package com.fast.admin.jwt.service;

import com.fast.admin.framework.response.Response;
import com.fast.admin.jwt.model.AccountCredentials;

import java.io.Serializable;

public interface JwtLoginService {

    /**
     * 图形验证码验证
     *
     * @param creds
     * @return
     */
    Response checkCode(AccountCredentials creds);

    /**
     * 用户验证(返回结果存入缓存)
     *
     * @param userName
     * @param password
     * @return
     */
    Response findByUserNameAndPassword(String userName, String password);

    /**
     * 登陆后账户凭据(登录成功响应客户端)
     * @return
     */
    Serializable getAccountCredentials(Object details, String JWToken);
}
