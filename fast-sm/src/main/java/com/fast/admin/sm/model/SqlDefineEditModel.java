package com.fast.admin.sm.model;

import com.fast.admin.framework.request.Request;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

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

//    @NotBlank(message = "表名不能为空")
    private String tableName;

    /**
     * 主表对应的ID
     */
//    @NotBlank(message = "主键不能为空")
    private String pri;
    private Integer state;
    private String sqlExtra;
    private Long datasource;

    @NotBlank(message = "操纵类型不能为空")
    private String manipulate;
    private Integer isCache;
    private String remark;
    private Long version;
}
