package com.fast.admin.mybatis.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_rbac_dict")
public class TRbacDict {
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 值
     */
    private String value;

    /**
     * 文本值
     */
    private String text;

    /**
     * 备注
     */
    private String remark;

    /**
     * 所属字典
     */
    private Long pid;

    private Integer sort;

    private Long version;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "last_update_user")
    private String lastUpdateUser;

    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取值
     *
     * @return value - 值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取文本值
     *
     * @return text - 文本值
     */
    public String getText() {
        return text;
    }

    /**
     * 设置文本值
     *
     * @param text 文本值
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取所属字典
     *
     * @return pid - 所属字典
     */
    public Long getPid() {
        return pid;
    }

    /**
     * 设置所属字典
     *
     * @param pid 所属字典
     */
    public void setPid(Long pid) {
        this.pid = pid;
    }

    /**
     * @return sort
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @param sort
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * @return version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return create_user
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * @param createUser
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return last_update_user
     */
    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    /**
     * @param lastUpdateUser
     */
    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    /**
     * @return last_update_time
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * @param lastUpdateTime
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}