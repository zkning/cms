package com.sophia.cms.sm.domain;

import com.sophia.cms.orm.domain.Auditable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_sm_sqldefine")
public class SqlDefine extends Auditable {
    private static final long serialVersionUID = 1L;
    private String sqlName;
    private String selectSql;
    private String manipulate;
    private String sqlExtra;
    private Long datasource;
    /**
     * 是否缓存
     */
    private Integer isCache;

    /**
     * 1-编辑,2-发布
     */
    private Integer state;

    /**
     * 功能描述
     */
    private String remark;

    /**
     * 主表
     */
    private String tableName;

    /**
     * 主表对应的ID
     */
    private String pri;
}
