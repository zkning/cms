package com.sophia.cms.sm.model;

import com.sophia.cms.framework.request.PagerRequest;
import lombok.Data;

@Data
public class SqlDefineSearchModel extends PagerRequest {

    private Long id;
    private String sqlName;
    private String tableName;
}
