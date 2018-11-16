package com.fast.admin.rbac.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fast.admin.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by lenovo on 2017/11/10.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "t_rbac_user_info")
public class RbacUserInfo extends Auditable {

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @ApiModelProperty(value = "用户名")
    @Column(name = "user_name")
    private String userName;

    @ApiModelProperty(value = "姓名")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "手机号码")
    @Column(name = "mobile")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    @Column(name = "email")
    private String email;

    @ApiModelProperty(value = "头像")
    @Column(name = "avatar")
    private String avatar;

    @ApiModelProperty(value = "所属组别id")
    @Column(name = "group_id")
    private Long groupId;

    @Transient
    @ApiModelProperty(value = "所属组别名称")
    private String groupName;
}
