package com.sophia.cms.sm.model;

import com.sophia.cms.framework.request.PagerRequest;
import lombok.Data;

@Data
public class DataViewSearchModel extends PagerRequest {

    private String dataViewName;
    private String sqlId;
}
