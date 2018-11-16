package com.fast.admin.jwt.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 存储用户名密码，另一个是一个权限类型，负责存储权限和角色。
 * Created by ningzuokun on 2017/12/18.
 */
@Data
public class AccountCredentials implements Serializable {
    private String username;
    private String password;
    private String code;
}

