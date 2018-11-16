package com.fast.admin.orm.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 信审相关表的通用字段
 *
 * Created by Kim on 2015/9/21.
 */
@Data
@MappedSuperclass
public class Auditable implements Serializable {
    protected static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = "com.fast.admin.orm.idworker.SnowflakeId")
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