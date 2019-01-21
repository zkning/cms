package com.sophia.cms.sm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class DataSourceFetchModel {


    @ApiModelProperty(value = "")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @ApiModelProperty(value = "驱动名")
    private String driverClassName;


    @ApiModelProperty(value = "")
    private String url;


    @ApiModelProperty(value = "用户名")
    private String dbUsername;


    @ApiModelProperty(value = "密码")
    private String dbPassword;


    @ApiModelProperty(value = "描述")
    private String remark;


    @ApiModelProperty(value = "名称")
    private String name;


    @ApiModelProperty(value = "")
    private Long version;


    @ApiModelProperty(value = "")
    private Timestamp createTime;


    @ApiModelProperty(value = "")
    private String createUser;


    @ApiModelProperty(value = "")
    private Timestamp lastUpdateTime;


    @ApiModelProperty(value = "")
    private String lastUpdateUser;
}
