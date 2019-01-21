package com.sophia.cms.sm.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lenovo on 2018/2/12.
 */
@Data
public class SchemaTableModel implements Serializable{

    private String tableName;
    private String tableComment;
    private List<SchemaColumnModel> schemaColumnModelList;
}
