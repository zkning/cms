package com.fast.admin.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class PreviewModel {

    @ApiModelProperty(value = "表名")
    @NotBlank
    private String tablename;

    //    @NotBlank
    @ApiModelProperty(value = "数据源")
    private Long  datasource;
}
