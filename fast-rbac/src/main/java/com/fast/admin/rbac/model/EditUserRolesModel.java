package com.fast.admin.rbac.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EditUserRolesModel {

    @NotNull(message = "角色id不能为空")
    @ApiModelProperty(value = "角色id",required = true)
    private List<Long> roleIds;

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private Long userId;
}
