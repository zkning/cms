package com.sophia.cms.sm.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2017/8/31.
 */
@Data
public class BootstrapPageResult {

    private long total;
    private List<Map<String, Object>> rows;
}
