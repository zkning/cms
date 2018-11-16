package com.fast.admin.sm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SqlDefineFetchModel {

    @ApiModelProperty(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "别名")
    private String sqlName;

    @ApiModelProperty(value = "查询SQL")
    private String selectSql;

    @ApiModelProperty(value = "扩展SQL")
    private String sqlExtra;

    @ApiModelProperty(value = "数据源")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long datasource;

    /**
     * 是否缓存
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "是否缓存,0=缓存,1=不缓存")
    private Integer isCache;

    /**
     * 1-编辑,2-发布
     */
    @ApiModelProperty(value = "0-待发布,1-已发布")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer state;

    /**
     * 功能描述
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 主表
     */
    @ApiModelProperty(value = "对象")
    private String tableName;

    /**
     * 主表对应的ID
     */
    @ApiModelProperty(value = "对象主键")
    private String pri;
    private Long version;
}
