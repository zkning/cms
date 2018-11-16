package com.fast.admin.sm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class DataViewFetchModel{
    private String dataViewName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long sqlId;
    private String remark;
    private OptionsModel options;
    private List<FieldModel> fields;
    private List<ButtonModel> buttons;
    private TreeOptionsModel treeOptions;
    private List<DataFilterModel> dataFilters;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long version;
}
