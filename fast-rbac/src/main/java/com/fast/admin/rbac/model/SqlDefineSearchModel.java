package com.fast.admin.rbac.model;

import com.fast.admin.framework.request.PagerRequest;
import lombok.Data;

@Data
public class SqlDefineSearchModel extends PagerRequest {

    private Long id;
    private String sqlName;
    private String tableName;
}
