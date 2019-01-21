package com.sophia.cms.rbac.model;

import com.sophia.cms.rbac.domain.RbacUserInfo;
import com.sophia.cms.rbac.domain.Resources;
import com.sophia.cms.rbac.security.OAuth2Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lenovo on 2018/5/30.
 */
@Data
public class AccountInfoModel implements Serializable {

    private App app;
    private OAuth2Principal user;
    private List<Resources> menu;
    private List<String> permits;

    //APP系统配置
    @Data
    public static class App implements Serializable {

        @ApiModelProperty(value = "应用名称")
        private String name;

        @ApiModelProperty(value = "应用概要")
        private String description;
    }
}
