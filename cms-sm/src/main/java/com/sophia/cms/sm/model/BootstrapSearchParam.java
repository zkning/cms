package com.sophia.cms.sm.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lenovo on 2017/9/2.
 */
@Data
public class BootstrapSearchParam implements Serializable {

    private Integer pageSize;
    private Integer pageNumber;
    private String searchText;
    private String sortName;
    private String sortOrder;
    private TreeOptionsFilterModel treeOptions;
    private List<ConditionModel> searchArray;
}
