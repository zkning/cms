package com.fast.admin.rbac.domain;

import com.fast.admin.orm.domain.Auditable;
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
@Table(name = "t_rbac_role_resource_relation")
public class RoleResourcesRelation extends Auditable {

    @ApiModelProperty(value = "角色id")
    @Column(name = "role_id")
    private Long roleId;
    @ApiModelProperty(value = "资源id")
    @Column(name = "resource_id")
    private Long resourceId;
}
