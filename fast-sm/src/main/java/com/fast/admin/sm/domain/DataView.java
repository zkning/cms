package com.fast.admin.sm.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lenovo on 2017/8/13.
 */
@Data
@Entity
@Table(name = "t_sm_dataview")
public class DataView {

    @Id
    private Long id;

    @Version
    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "修改者")
    @Column(name = "last_update_user")
    private String lastUpdateUser;

    @ApiModelProperty(value = "创建者")
    @Column(name = "create_user")
    private String createUser;

    private String dataViewName;
    private Long sqlId;
    private String remark;
    private String options;
    private String fields;
    private String buttons;
    private String treeOptions;
    private String dataFilters;

    @PrePersist
    public void prePersist() {
        Date currentTime = new Date();
        this.setCreateTime(currentTime);
        this.setLastUpdateTime(currentTime);
    }

    @PreUpdate
    public void preUpdate() {
        this.setVersion(this.version + 1);
        this.setLastUpdateTime(new Date());
    }
}
