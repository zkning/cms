package com.fast.admin.rbac.domain;

import com.fast.admin.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户角色
 * Created by lenovo on 2017/11/11.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_rbac_role")
public class Role extends Auditable {

    @ApiModelProperty(value = "角色名称")
    @Column(name = "role_name")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    @Column(name = "role_code")
    private String roleCode;

    @ApiModelProperty(value = "该角色拥有的分组id(分组顶级节点)")
    @Column(name = "group_id")
    private Long groupId;

    @ApiModelProperty(value = "分类")
    private Integer roleType;

    @ApiModelProperty(value = "备注")
    @Column(name = "remark")
    private String remark;
}
