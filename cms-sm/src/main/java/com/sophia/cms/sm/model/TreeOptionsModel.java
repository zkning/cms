package com.sophia.cms.sm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by lenovo on 2017/8/13.
 */
@Data
public class TreeOptionsModel {

    @ApiModelProperty(value = "是否显示树")
    private boolean visible;

    @ApiModelProperty(value = "sqlId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sqlId;

    @ApiModelProperty(value = "节点值")
    private String idKey;

    @ApiModelProperty(value = "节点文本值")
    private String name;

    @ApiModelProperty(value = "父级值")
    private String pidKey;

    @ApiModelProperty(value = "显示范围")
    private String scope;

    @ApiModelProperty(value = "Tree控件宽度")
    private int width;

    @ApiModelProperty(value = "是否异步")
    private boolean enable;

    @ApiModelProperty(value = "关联外键值")
    private String foreignKey;
}
