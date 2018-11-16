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
import javax.persistence.Transient;
import java.util.List;

/**
 * 用户组
 * Created by lenovo on 2017/11/11.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_rbac_group")
public class Group extends Auditable implements Comparable<Group> {

    @ApiModelProperty(value = "分组名称")
    @Column(name = "group_name")
    private String groupName;

    @ApiModelProperty(value = "备注")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(value = "上级id")
    @Column(name = "pid")
    private Long pid;

    @ApiModelProperty(value = "分组类型")
    @Column(name = "group_type")
    private Integer groupType;

    @ApiModelProperty(value = "扩展数据")
    @Column(name = "extra")
    private String extra;

    @ApiModelProperty(value = "是否有效")
    private Integer isValid;

    @Transient
    private List<Group> children;

    @Transient
    private int level;

    @Override
    public int compareTo(Group group) {
        return this.getLevel() - group.getLevel();
    }
}
