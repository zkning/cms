package com.fast.admin.sm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fast.admin.framework.request.Request;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据视图创建请求
 * Created by lenovo on 2017/8/13.
 */
@Data
public class DataViewEditModel extends Request {

    @NotBlank(message = "视图名称不能为空值")
    @ApiModelProperty(value = "视图名称")
    private String dataViewName;

    @NotNull(message = "sqlId不能为空值")
    @ApiModelProperty(value = "sqlId")
    private Long sqlId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @NotNull(message = "表格选项不能为空值")
    @ApiModelProperty(value = "表格选项")
    private OptionsModel options;

    @NotNull(message = "列表字段不能为空值")
    @ApiModelProperty(value = "列表字段")
    private List<FieldModel> fields;

    @ApiModelProperty(value = "树设置")
    private TreeOptionsModel treeOptions;

    @ApiModelProperty(value = "过滤集合")
    private List<DataFilterModel> dataFilters;

    @ApiModelProperty(value = "按钮集合")
    private List<ButtonModel> buttons;

    @ApiModelProperty(value = "版本号")
    private long version;
}
