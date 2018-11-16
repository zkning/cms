package com.fast.admin.rbac.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupSearchModel {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "分组名称")
    @NotNull(message = "组别名称不能为空")
    private String groupName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "上级组别ID")
    @NotNull(message = "上级组别不能为空")
    private String pid;

    @ApiModelProperty(value = "分组类型(0=机构,1=部门)")
    private Integer groupType;

    @ApiModelProperty(value = "扩展数据JSON")
    private String extra;

    @ApiModelProperty(value = "是否有效")
    private Integer isValid;

    @ApiModelProperty(value = "版本号")
    private Long version;
}
