package com.sophia.cms.sm.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by lenovo on 2017/8/13.
 */
@Data
@TableName(value = "t_sm_dataview")
public class DataView {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    @Version
    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(value = "last_update_time")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "修改者")
    @TableField(value = "last_update_user")
    private String lastUpdateUser;

    @ApiModelProperty(value = "创建者")
    @TableField(value = "create_user")
    private String createUser;


    @TableField(value = "data_view_name")
    private String dataViewName;
    private Long sqlId;
    private String remark;
    private String options;
    private String fields;
    private String buttons;

    @TableField(value = "tree_options")
    private String treeOptions;

    @TableField(value = "data_filters")
    private String dataFilters;

    public void prePersist() {
        Date currentTime = new Date();
        this.setCreateTime(currentTime);
        this.setLastUpdateTime(currentTime);
    }

    public void preUpdate() {
        this.setVersion(this.version + 1);
        this.setLastUpdateTime(new Date());
    }
}
