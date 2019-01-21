package com.sophia.cms.rbac.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoleSearchModel {

    @ApiModelProperty(value = "角色分类")
    private Integer roleType;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "该角色拥有的分组id(分组顶级节点)")
    private Long groupId;

}
