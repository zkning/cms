package com.sophia.cms.sm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Created by lenovo on 2017/8/13.
 */
@Data
public class FieldModel implements Serializable {

    @NotBlank(message = "字段不能为空值")
    private String field;

    @NotBlank(message = "名称不能为空值")
    private String title;

    @NotBlank(message = "名称不能为空值")
    private String updateType;
    private boolean view;
    private boolean insert;
    private boolean visible;
    private String dataType;
    private String fieldType;
    private int maxlength;
    private int sort;
    private boolean radio;
    private boolean checkbox;

    @ApiModelProperty(value = "提醒")
    private String titleTooltip;
    private int rowspan;
    private int colspan;
    private String align;
    private String halign;
    private String falign;
    private String valign;
    private String width;
    private boolean sortable;
    private String order;
    private boolean cardVisible;
    private boolean switchable;

    private boolean duplicated;
    private boolean clickToSelect;
    private String formatter;
    private String sortName;
    private boolean searchable;
    private boolean searchFormatter;
    private boolean escape;
    private String dataFormat;

    @ApiModelProperty(value = "表单校验器")
    private String pattern;

    @ApiModelProperty(value = "脱敏类型:1=中文名，2=身份证，3=手机号，4=电子邮件，5=银行卡")
    private Integer masking;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据字典")
    private String dict;

//    private String footerFormatter;
//    private String events;
//    private String sorter;
//    private String cellStyle;
}
