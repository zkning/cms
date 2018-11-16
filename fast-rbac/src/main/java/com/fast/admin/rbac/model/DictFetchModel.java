package com.fast.admin.rbac.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DictFetchModel implements Serializable {

    private static final long serialVersionUID = 6493174796984177374L;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "文本值")
    private String text;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "所属项id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pid;

    @ApiModelProperty(value = "所属项")
    private String pidText;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "备注")
    private String remark;
}
