package com.sophia.cms.rbac.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sophia.cms.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by lenovo on 2017/11/10.
 */
@Data
@NoArgsConstructor
@TableName(value = "t_rbac_user")
public class RbacUserInfo extends Auditable {

    @JsonIgnore
    @TableField(value = "password")
    private String password;

    @ApiModelProperty(value = "用户名")
    @TableField(value = "user_name")
    private String userName;

    @ApiModelProperty(value = "姓名")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty(value = "手机号码")
    @TableField(value = "mobile")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    @TableField(value = "email")
    private String email;

    @ApiModelProperty(value = "头像")
    @TableField(value = "avatar")
    private String avatar;

    @ApiModelProperty(value = "所属组别id")
    @TableField(value = "group_id")
    private Long groupId;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属组别名称")
    private String groupName;
}
