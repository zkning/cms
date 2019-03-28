package com.sophia.cms.sm.model;

import com.sophia.cms.framework.request.Request;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by ningzuokun on 2017/11/8.
 */
@Data
public class SqlDefineEditModel extends Request {

    private Long id;

    @NotBlank(message = "sqlName不能为空")
    private String sqlName;

    @NotBlank(message = "selectSql不能为空")
    private String selectSql;
    private String tableName;

    /**
     * 主表对应的ID
     */
    private String pri;
    private Integer state;
    private String sqlExtra;
    private Long datasource;

    @NotNull(message = "操纵类型不能为空")
    private Integer sqlType;
    private Integer isCache;
    private String remark;
    private Long version;
}
