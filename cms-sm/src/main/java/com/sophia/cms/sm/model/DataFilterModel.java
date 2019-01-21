package com.sophia.cms.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by lenovo on 2017/8/13.
 */
@Data
public class DataFilterModel {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "字段名")
    private String field;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

//    @ApiModelProperty(value = "数据类型")
//    private String dataType;

    @ApiModelProperty(value = "表达式")
    private String expression;

    @ApiModelProperty(value = "索引")
    private String sort;

    @ApiModelProperty(value = "扩展")
    private String extra;
}
