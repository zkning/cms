package com.fast.admin.sm.service;

import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.response.Response;
import com.fast.admin.sm.domain.SqlDefine;
import com.fast.admin.sm.model.*;

import java.util.List;

public interface SqlIdJdbcService {


    /**
     * 根据sql获取该查询语句的列信息
     */
    public List<FieldModel> showFullColumnsBySqlId(Long sqlId) throws Exception;

    /**
     * 根据sqlid构建bootstrap table数据
     */
    Response<BootstrapPageResult> getBootstrapTableResponse(Integer pageSize, Integer pageNumber, String searchText,
                                                            String sortName, String sortOrder, Long sqlId, BootstrapSearchParam bootstrapSearchParam);

    /**
     * 根据sqlid获取bootstrap table数据
     */
    Response<BootstrapPageResult> getBootstrapTableResponse(BootstrapSearchParam bootstrapSearchParam, Long sqlId);

    /**
     * 查询内容根据sqlid和数据id
     */
    Response fetch(Long sqlId, Long recordId);

    /**
     * 创建数据视图根据SQL定义编码
     */
    Response createByDataViewId(Long id, JSONObject rowValue);

    /**
     * 删除数据视图根据SQL定义编码
     */
    Response deleteByDataViewId(Long id, JSONObject rowValue);

    /**
     * 更新数据视图根据SQL定义编码
     */
    Response modifyByDataViewId(Long id, JSONObject rowValue);

    /**
     * 构建ztree数据
     */
    Response ztree(ZtreeModel ztreeModel);
    Response getTree(ZtreeModel ztreeModel);

    /**
     * 查找系统表
     */
    List<SchemaTableModel> getInformationSchemaTable(SchemaTableSearchModel schemaTableSearchModel) throws Exception;


    /**
     * 根据表获取columns
     */
    List<SchemaColumnModel> getInformationSchemaColumns(String tablename, Long dataSourceId) throws Exception;

    /**
     * 根据所选表名预览sqldefine
     */
    Response<SqlDefine> preview(PreviewModel previewModel) throws Exception;
}
