package com.fast.admin.sm.model;

import com.fast.admin.framework.request.PagerRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataSourceSearchModel extends PagerRequest {

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "")
    private String url;

    @ApiModelProperty(value = "用户名")
    private String dbUsername;

    @ApiModelProperty(value = "名称")
    private String name;
}
