package com.sophia.cms.rbac.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sophia.cms.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户角色关联
 * Created by lenovo on 2017/11/11.
 */
@Data
@TableName(value = "t_rbac_user_role_relation")
public class UserRoleRelation extends Auditable {

    @ApiModelProperty(value = "用户id")
    @TableField(value = "user_id")
    private Long userId;

    @ApiModelProperty(value = "角色id")
    @TableField(value = "role_id")
    private Long roleId;
}
