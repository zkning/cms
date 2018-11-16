package com.fast.admin.rbac.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeModel {

    @ApiModelProperty(value = "文本")
    private String title;

    @ApiModelProperty(value = "值")
    private String key;

    @ApiModelProperty(value = "是否叶子节点")
    @JsonProperty(value = "isLeaf")
    private boolean isLeaf;

    @ApiModelProperty(value = "是否显示")
    private boolean disabled;

    @ApiModelProperty(value = "子节点")
    List<TreeNodeModel> children;

    @ApiModelProperty(value = "设置节点是否可被选中")
    private boolean selectable;

    @ApiModelProperty(value = "设置节点是否展开(叶子节点无效)")
    private boolean expanded;

    @ApiModelProperty(value = "上级id")
    private String pid;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "是否选中")
    private boolean checked;
}
