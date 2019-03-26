package com.sophia.cms.orm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 信审相关表的通用字段
 */
@Data
public class Auditable implements Serializable {
    protected static final long serialVersionUID = 1L;

    //    @Id
//    @GeneratedValue(generator = "snowFlakeId")
//    @GenericGenerator(name = "snowFlakeId", strategy = "com.sophia.cms.orm.idworker.SnowflakeId")
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

    public void prePersist() {
        Date currentTime = new Date();
        this.setCreateTime(currentTime);
        this.setLastUpdateTime(currentTime);
    }

    public void preUpdate() {
        this.setLastUpdateTime(new Date());
    }
}