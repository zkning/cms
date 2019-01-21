package com.sophia.cms.rbac.model;

import com.sophia.cms.framework.request.Request;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author liangyonghua
 * @date 2018/7/23 16:13
 */
@Data
public class GroupEditModel extends Request {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "分组名称", required = true)
    @NotBlank(message = "组别名称不能为空")
    private String groupName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "上级组别ID", required = true)
    @NotNull(message = "上级组别不能为空")
    private Long pid;

    @ApiModelProperty(value = "分组类型(0=机构,1=部门)", required = true)
    private Integer groupType;

    @ApiModelProperty(value = "扩展数据JSON")
    private String extra;

    @ApiModelProperty(value = "是否有效")
    private Integer isValid;

    @ApiModelProperty(value = "版本号")
    private Long version;
}
