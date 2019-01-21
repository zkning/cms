package com.sophia.cms.framework.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PagerRequest extends Request{

    @ApiModelProperty(value = "页码数")
    private int pageSize = 10;

    @ApiModelProperty(value = "页码")
    private int pageNo = 1;
}
