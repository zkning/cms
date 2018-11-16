package com.fast.admin.rbac.model;

import com.fast.admin.framework.request.PagerRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictSearchModel extends PagerRequest {

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "文本值")
    private String text;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "所属字典")
    private String pidText;

    @ApiModelProperty(value = "所属上级")
    private Long pid;
}
