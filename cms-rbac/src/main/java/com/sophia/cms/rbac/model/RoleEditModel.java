package com.sophia.cms.rbac.model;

import com.sophia.cms.framework.request.Request;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class RoleEditModel extends Request {

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "该角色拥有的分组id(分组顶级节点)")
    private Long groupId;

    @ApiModelProperty(value = "分类")
    private Integer roleType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "角色id")
    private Long id;

    @ApiModelProperty(value = "版本号")
    private Long version;
}
