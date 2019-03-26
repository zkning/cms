package com.sophia.cms.rbac.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sophia.cms.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by lenovo on 2017/11/11.
 */
@Data
@TableName(value = "t_rbac_resource")
public class Resources extends Auditable {

    @ApiModelProperty(value = "文本")
    private String text;

    @ApiModelProperty(value = "资源编码")
    private String code;

    @ApiModelProperty(value = "国际化")
    private String i18n;

    @ApiModelProperty(value = "angular路由")
    private String link;

    @ApiModelProperty(value = "外部链接")
    @TableField(value = "external_link")
    private String externalLink;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "父级菜单")
    private Long pid;

    @ApiModelProperty(value = "类型")
    @TableField(value = "resource_type")
    private Integer resourceType;

    @ApiModelProperty(value = "扩展数据")
    private String extra;

    /**
     * 下级菜单
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "下级菜单")
    private List<Resources> children;

}
