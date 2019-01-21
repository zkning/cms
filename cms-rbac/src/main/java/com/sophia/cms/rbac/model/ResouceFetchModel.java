package com.sophia.cms.rbac.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class ResouceFetchModel {

    @ApiModelProperty(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "文本")
    private String text;

    @ApiModelProperty(value = "资源编码")
    private String code;

    @ApiModelProperty(value = "angular路由")
    private String link;

    @ApiModelProperty(value = "外部链接")
    @Column(name = "external_link")
    private String externalLink;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "父级菜单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pid;

    @ApiModelProperty(value = "类型")
    private Integer resourceType;

    @ApiModelProperty(value = "扩展数据")
    private String extra;
}
