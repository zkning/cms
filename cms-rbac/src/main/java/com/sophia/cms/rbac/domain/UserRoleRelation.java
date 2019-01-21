package com.sophia.cms.rbac.domain;

import com.sophia.cms.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户角色关联
 * Created by lenovo on 2017/11/11.
 */
@Data
@Entity
@Table(name = "t_rbac_user_role_relation")
public class UserRoleRelation extends Auditable {

    @ApiModelProperty(value = "用户id")
    @Column(name = "user_id")
    private Long userId;
    @ApiModelProperty(value = "角色id")
    @Column(name = "role_id")
    private Long roleId;
}
