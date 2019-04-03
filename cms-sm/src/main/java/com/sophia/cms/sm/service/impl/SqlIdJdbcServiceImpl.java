package com.sophia.cms.sm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.sm.constant.SqlDefineStatusEnum;
import com.sophia.cms.sm.domain.DataView;
import com.sophia.cms.sm.domain.SqlDefine;
import com.sophia.cms.sm.model.*;
import com.sophia.cms.sm.service.SqlIdJdbcService;
import com.sophia.cms.sm.utils.SimpleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zkning
 */
@Slf4j
@Service
public class SqlIdJdbcServiceImpl extends DataViewDetailsService implements SqlIdJdbcService {

    @Override
    public List<FieldModel> showFullColumnsBySqlId(Long sqlId) throws Exception {
        SqlDefine sqlDefine = findOne(sqlId);

        // 获取table sqlRowSet信息
        SqlRowSet resultSet = getSqlRowSet(sqlDefine.getDatasource(), getFieldSql(sqlDefine), new HashMap<>());

        // 表列map
        Map<String, Object> fieldMap = getFieldUpperCaseMap(resultSet);

        // 查询列注释
        Map<String, String> commentMap = getFieldUpperCommentMap(sqlDefine, resultSet);

        //查询SQL列信息列表
        return showFullColumns(sqlDefine, fieldMap, commentMap);
    }

    @Override
    public Response deleteByDataViewId(Long id, JSONObject rowValue) {
        return super.deleteByDataViewId(id, rowValue);
    }

    @Override
    public Response modifyByDataViewId(Long id, JSONObject rowValue) {
        return super.updateByDataViewId(id, rowValue);
    }

    @Override
    public Response createByDataViewId(Long id, JSONObject rowValue) {
        return super.createByDataViewId(id, rowValue);
    }

    @Override
    public Response fetch(Long dataViewId, String recordId) {
        return super.fetch(dataViewId, recordId);
    }

    @Override
    public Response<BootstrapPageResult>
    getBootstrapTableResponse(BootstrapSearchParam sp, Long dataViewId) {
        return this.getBootstrapTableResponse(sp.getPageSize()
                , sp.getPageNumber()
                , sp.getSearchText()
                , sp.getSortName()
                , sp.getSortOrder()
                , dataViewId
                , sp);
    }

    /**
     * 默认获取bootstrapTable服务
     */
    @Override
    public Response<BootstrapPageResult> getBootstrapTableResponse(Integer pageSize,
                                                                   Integer pageNumber,
                                                                   String searchText,
                                                                   String sortName,
                                                                   String sortOrder,
                                                                   Long dataViewId,
                                                                   BootstrapSearchParam bootstrapSearchParam) {

        return this.getBootstrapTableDataResponse(pageSize, pageNumber, searchText, sortName, sortOrder, dataViewId, bootstrapSearchParam);
    }


    @Override
    public Response getTree(ZtreeModel ztreeModel) {
        return super.getTree(ztreeModel);
    }


    @Override
    public List<SchemaTableModel> getInformationSchemaTable(SchemaTableSearchModel schemaTableSearchModel) throws Exception {
        return super.getInformationSchemaTable(schemaTableSearchModel);
    }

    @Override
    public List<SchemaColumnModel> getInformationSchemaColumns(String tablename, Long datasource) throws Exception {
        return super.getInformationSchemaColumns(tablename, datasource);
    }

    @Override
    public Response<SqlDefine> preview(PreviewModel previewModel) throws Exception {
        SqlDefine sqlDefine = new SqlDefine();
        sqlDefine.setTableName(previewModel.getTablename());

        // 查询表列信息
        List<SchemaColumnModel> columnList = getInformationSchemaColumns(previewModel.getTablename(), previewModel.getDatasource());
        for (SchemaColumnModel schemaColumnModel : columnList) {
            if (StringUtils.isNoneBlank(schemaColumnModel.getColumnKey())) {
                sqlDefine.setPri(schemaColumnModel.getColumnName().toUpperCase());
                break;
            }
        }
        sqlDefine.setState(SqlDefineStatusEnum.UN_ISSUE.getCode());
        sqlDefine.setSelectSql(SimpleUtils.buildQuerySql(previewModel.getTablename(), columnList));
        return Response.SUCCESS(sqlDefine);
    }
}
