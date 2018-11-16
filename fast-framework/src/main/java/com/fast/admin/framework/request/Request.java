package com.fast.admin.framework.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by ningzuokun on 2018/3/17.
 */
@Data
public class Request implements Serializable {

    @ApiModelProperty(value="请求序列号",hidden=true)
    private String serialNo;

    @ApiModelProperty(value="用户id",hidden=true)
    private Long sessionUserId;
}
