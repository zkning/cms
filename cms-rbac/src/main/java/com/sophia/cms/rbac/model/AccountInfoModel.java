package com.sophia.cms.rbac.model;

import com.sophia.cms.rbac.domain.Res;
import com.sophia.cms.rbac.security.OAuth2Principal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2018/5/30.
 */
@Data
public class AccountInfoModel implements Serializable {

    private App app;

    /**
     * 前端layout动态设置
     */
    private Map<String, String> layout;
    private OAuth2Principal user;
    private List<Res> menu;
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
