package com.fast.admin.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by lenovo on 2017/8/13.
 */
@Data
public class ButtonModel {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "server,model,window")
    private String option;

    @ApiModelProperty(value = "组件")
    private String modal;

    @ApiModelProperty(value = "窗口大小")
    private int size;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "服务端url")
    private String url;

    @ApiModelProperty(value = "按钮位置row,nav")
    private String position;

    @ApiModelProperty(value = "索引")
    private String sort;

    @ApiModelProperty(value = "颜色")
    private String color;

    @ApiModelProperty(value = "1=增加，2=修改，3=删除，4=查看")
    private Integer curd;

    @ApiModelProperty(value = "是否校验数据")
    private boolean verify;
}
