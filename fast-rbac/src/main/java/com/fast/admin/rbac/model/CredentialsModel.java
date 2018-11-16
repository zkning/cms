package com.fast.admin.rbac.model;

import com.fast.admin.rbac.domain.RbacUserInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by ningzuokun on 2018/3/26.
 */
@Data
public class CredentialsModel implements Serializable {

    private String token;
    private RbacUserInfo user;
}


