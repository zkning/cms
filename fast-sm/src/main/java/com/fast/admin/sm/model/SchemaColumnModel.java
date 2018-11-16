package com.fast.admin.sm.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/4/15.
 */
@Data
public class SchemaColumnModel implements Serializable{
    private String columnName;
    private String columnKey;
    private String columnComment;
}
