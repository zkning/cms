package com.fast.admin.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by lenovo on 2017/8/13.
 */
@Data
public class FieldModel implements Serializable{

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
//    private String footerFormatter;
//    private String events;
//    private String sorter;
//    private String cellStyle;
}
