package com.fast.admin.rbac.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleFetchModel {

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "该角色拥有的分组id(分组顶级节点)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @ApiModelProperty(value = "分类")
    private String roleType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "版本号")
    private Long version;
}
