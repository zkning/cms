package com.fast.admin.sm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/9/2.
 */
@Data
public class ConditionModel implements Serializable {

    @ApiModelProperty(value = "值")
    private Object value;

    @ApiModelProperty(value = "字段名")
    private String field;

    @ApiModelProperty(value = "表达式")
    private String expression;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "排序")
    private String order;
}
