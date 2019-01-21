package com.sophia.cms.rbac.model;

import com.sophia.cms.framework.request.Request;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author liangyonghua
 * @date 2018/7/26 11:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceEditModel extends Request {

    @ApiModelProperty(value = "角色id")
    private Long id;

    @NotBlank(message = "文本不能为空")
    @ApiModelProperty(value = "文本")
    private String text;

    @NotBlank(message = "资源编码不能为空")
    @ApiModelProperty(value = "资源编码")
    private String code;

    //    @NotBlank(message = "路由不能为空")
    @ApiModelProperty(value = "angular路由")
    private String link;

    @ApiModelProperty(value = "外部链接")
    private String externalLink;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "父级菜单")
    private Long pid;

    @ApiModelProperty(value = "类型")
    private Integer resourceType;

    @ApiModelProperty(value = "扩展数据")
    private String extra;

    @ApiModelProperty(value = "版本号")
    private Long version;
}
