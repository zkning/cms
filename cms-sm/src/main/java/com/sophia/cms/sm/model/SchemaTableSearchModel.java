package com.sophia.cms.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by lenovo on 2018/2/12.
 */
@Data
public class SchemaTableSearchModel {

    @ApiModelProperty(value = "表名")
    @NotBlank
    private String tablename;

    @NotNull
    @ApiModelProperty(value = "数据源")
    private Long  datasource;
}
