package com.sophia.cms.rbac.domain;

import com.sophia.cms.orm.domain.Auditable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by lenovo on 2017/11/11.
 */
@Data
@Entity
@Table(name = "t_rbac_resource")
public class Resources extends Auditable {

    @ApiModelProperty(value = "文本")
    private String text;

    @ApiModelProperty(value = "资源编码")
    private String code;

    @ApiModelProperty(value = "angular路由")
    private String link;

    @ApiModelProperty(value = "外部链接")
    @Column(name = "external_link")
    private String externalLink;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "父级菜单")
    private Long pid;

    @ApiModelProperty(value = "类型")
    private Integer resourceType;

    @ApiModelProperty(value = "扩展数据")
    private String extra;

    /** 下级菜单 */
    @Transient
    @ApiModelProperty(value = "下级菜单")
    private List<Resources> children;

}
