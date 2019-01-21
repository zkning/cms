package com.sophia.cms.rbac.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DictEditModel{

    @NotBlank
    @ApiModelProperty(value = "文本值")
    private String text;

    @NotBlank
    @ApiModelProperty(value = "值")
    private String value;

    @NotNull
    @ApiModelProperty(value = "所属字典")
    private Long pid;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "备注")
    private String remark;
}
