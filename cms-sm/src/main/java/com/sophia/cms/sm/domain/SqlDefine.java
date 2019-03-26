package com.sophia.cms.sm.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sophia.cms.orm.domain.Auditable;
import lombok.Data;


@Data
@TableName(value = "t_sm_sqldefine")
public class SqlDefine extends Auditable {
    private static final long serialVersionUID = 1L;

    @TableField(value = "sql_name")
    private String sqlName;

    @TableField(value = "select_sql")
    private String selectSql;
    private String manipulate;

    @TableField(value = "sql_extra")
    private String sqlExtra;
    private Long datasource;
    /**
     * 是否缓存
     */
    @TableField(value = "is_cache")
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
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 主表对应的ID
     */
    private String pri;
}
