package com.sophia.cms.sm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class DataViewModel {
    private String dataViewName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long sqlId;
    private String remark;
    private String options;
    private String fields;
    private String buttons;
    private String treeOptions;
    private String dataFilters;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long version;
    private Date createTime;
}
