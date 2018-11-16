package com.fast.admin.sm.model;

import com.fast.admin.framework.request.PagerRequest;
import lombok.Data;

@Data
public class DataViewSearchModel extends PagerRequest {

    private String dataViewName;
    private String sqlId;
}
