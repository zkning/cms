package com.fast.admin.rbac.security;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdditionalInfo implements Serializable {

    private Long id;

    @ApiModelProperty(value = "所属分组ID")
    private Long groupId;

    @ApiModelProperty(value = "所属分组名称")
    private String groupName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号码")
    private String avatar;
}
