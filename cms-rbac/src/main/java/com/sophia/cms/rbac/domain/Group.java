package com.sophia.cms.rbac.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sophia.cms.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户组
 * Created by lenovo on 2017/11/11.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_rbac_group")
public class Group extends Auditable implements Comparable<Group> {

    @ApiModelProperty(value = "分组名称")
    @TableField(value = "group_name")
    private String groupName;

    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    @ApiModelProperty(value = "上级id")
    @TableField(value = "pid")
    private Long pid;

    @ApiModelProperty(value = "分组类型")
    @TableField(value = "group_type")
    private Integer groupType;

    @ApiModelProperty(value = "扩展数据")
    @TableField(value = "extra")
    private String extra;

    @ApiModelProperty(value = "是否有效")
    private Integer isValid;

    @TableField(exist = false)
    private List<Group> children;

    @TableField(exist = false)
    private int level;

    @Override
    public int compareTo(Group group) {
        return this.getLevel() - group.getLevel();
    }
}
