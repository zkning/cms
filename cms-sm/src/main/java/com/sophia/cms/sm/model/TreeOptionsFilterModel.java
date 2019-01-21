package com.sophia.cms.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TreeOptionsFilterModel extends TreeOptionsModel {

    @ApiModelProperty(value = "当前节点值")
    private String nodeValue;
}
