package com.fast.admin.orm.model;

import lombok.Data;

@Data
public class ColumnModel {

    private String field;
    private String type;

    /**
     * 索引,PRI，MUL,UNI,
     */
    private String key;
    private String comment;
}
