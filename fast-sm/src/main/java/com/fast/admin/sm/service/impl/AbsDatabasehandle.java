package com.fast.admin.sm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.response.Response;
import com.fast.admin.sm.constant.DataViewConstant;
import com.fast.admin.sm.constant.FieldTypeEnum;
import com.fast.admin.sm.model.*;
import com.fast.admin.sm.repository.SqlDefineRepository;
import com.fast.admin.sm.domain.DataView;
import com.fast.admin.sm.domain.SqlDefine;
import com.fast.admin.sm.service.DataViewService;
import com.fast.admin.sm.utils.SimpleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public abstract class AbsDatabasehandle extends DataSourceCrudhandle {

    @Autowired
    SqlDefineRepository sqlDefineRepository;

    @Autowired
    DataViewService dataViewService;

    public SqlDefine findOne(Long sqlId) {
        return sqlDefineRepository.getOne(sqlId);
    }

    public Map<String, Object> getFieldUpperCaseMap(SqlRowSet resultSet) {
        SqlRowSetMetaData srsmd = resultSet.getMetaData();
        Map<String, Object> fieldMap = new HashMap<>();
        for (int index = 1; index <= srsmd.getColumnCount(); index++) {
            fieldMap.put(srsmd.getColumnLabel(index).toUpperCase(), srsmd.getColumnLabel(index));
        }
        return fieldMap;
    }

    public Map<String, String> getFieldUpperCommentMap(SqlDefine sqlDefine, SqlRowSet resultSet) throws Exception {
        Map<String, String> commentMap = new HashMap<>();
        List<SchemaColumnModel> columnModels = getInformationSchemaColumns(sqlDefine.getTableName(), sqlDefine.getDatasource());
        columnModels.forEach(item -> {
            commentMap.put(item.getColumnName().toUpperCase(), item.getColumnComment());
        });
        return commentMap;
    }

    public String getInformationSchemaColumnsSql() {
        return "SELECT t.column_name,t.column_key,t.column_comment FROM information_schema.COLUMNS t where t.table_name = ? and t.table_schema = ? ";
    }

    public List<SchemaColumnModel> getInformationSchemaColumns(String tablename, Long datasource) throws Exception {
        Connection connection = null;
        try {
            JdbcTemplate jdbcTemplate = this.getJdbcTemplate(datasource);
            connection = jdbcTemplate.getDataSource().getConnection();
            LinkedHashMap<String, Object> paraMap = new LinkedHashMap();
            paraMap.put("tableName", tablename);
            paraMap.put("tableschema", connection.getCatalog());
            List<SchemaColumnModel> dataList = jdbcTemplate.query(this.getInformationSchemaColumnsSql(), SimpleUtils.linkedHashMapToValues(paraMap),
                    (ResultSet resultSet, int i) -> {
                        SchemaColumnModel mySQLColumnResult = new SchemaColumnModel();
                        mySQLColumnResult.setColumnName(resultSet.getString("column_name"));
                        mySQLColumnResult.setColumnKey(resultSet.getString("column_key").toUpperCase());
                        mySQLColumnResult.setColumnComment(resultSet.getString("column_comment"));
                        return mySQLColumnResult;
                    });
            return dataList;
        } finally {
            this.destroyConnection(connection);
        }
    }

    public String getInformationSchemaTableSql() {
        return "select table_name,table_comment from information_schema.tables " +
                "where table_schema= ?  and TABLE_NAME like ? ";
    }

    public List<SchemaTableModel> getInformationSchemaTable(SchemaTableSearchModel schemaTableSearchModel) throws Exception {
        Connection connection = null;
        try {
            List<SchemaTableModel> dataList = new ArrayList<>();
            if (StringUtils.isBlank(schemaTableSearchModel.getTablename())) {
                return dataList;
            }
            JdbcTemplate jdbcTemplate = this.getJdbcTemplate(schemaTableSearchModel.getDatasource());
            connection = jdbcTemplate.getDataSource().getConnection();
            LinkedHashMap<String, Object> paraMap = new LinkedHashMap();

            //get dbname
            paraMap.put("tableschema", connection.getCatalog());
            paraMap.put("tableName", schemaTableSearchModel.getTablename() + '%');
            dataList = jdbcTemplate.query(getInformationSchemaTableSql(), SimpleUtils.linkedHashMapToValues(paraMap),
                    (ResultSet resultSet, int i) -> {
                        SchemaTableModel tablesResult = new SchemaTableModel();
                        tablesResult.setTableName(resultSet.getString("table_name"));
                        tablesResult.setTableComment(resultSet.getString("table_comment"));
                        return tablesResult;
                    });
            return dataList;
        } finally {
            this.destroyConnection(connection);
        }
    }

    public String getShowFullColumnsSql(String sql) {
        return new StringBuffer(" select t.* from (").append(sql).append(") t where 1=2 ").toString();
    }

    public List<FieldModel> showFullColumns(SqlDefine sqlDefine, Map<String, Object> fieldMap, Map<String, String> commentMap)
            throws Exception {
        Connection connection = null;
        try {
            //获取sql查询所有列
            DataSource dataSource = this.getJdbcTemplate(sqlDefine.getDatasource()).getDataSource();
            connection = dataSource.getConnection();
            ResultSet resultSet = connection.prepareStatement(getShowFullColumnsSql(sqlDefine.getSelectSql())).executeQuery();
            List<FieldModel> list = Lists.newArrayList();

            // 获取sql元数据
            ResultSetMetaData srsmd = resultSet.getMetaData();
            for (int index = 1; index <= srsmd.getColumnCount(); index++) {
                FieldModel field = this.buildColumnModel(srsmd, index);
                this.fieldFormat(field, fieldMap, commentMap);
                field.setSort(index);
                list.add(field);
            }
            return list;
        } finally {
            this.destroyConnection(connection);
        }
    }

    public FieldModel buildColumnModel(ResultSetMetaData srsmd, int index) throws SQLException {
        FieldModel field = new FieldModel();

        //  as 后的值 ，getColumnName 原始值
        field.setField(srsmd.getColumnLabel(index));
        field.setMaxlength(srsmd.getPrecision(index));
        field.setDataType(srsmd.getColumnTypeName(index));
        field.setFieldType(FieldTypeEnum.TEXT.getValue());
        field.setAlign(DataViewConstant.align_center);
        field.setHalign(DataViewConstant.align_center);
        field.setDuplicated(false);
        field.setSort(index);
        return field;
    }

    public void fieldFormat(FieldModel field, Map<String, Object> fieldMap, Map<String, String> commentMap) {

        // 表包含的SQL查询列,属性特殊处理
        if (fieldMap.containsKey(field.getField())) {
            field.setUpdateType(DataViewConstant.MODIFTY_HIDE);
            field.setInsert(true);
            field.setVisible(true);
            field.setView(true);
        }

        // 设置title
        if (commentMap.containsKey(field.getField())) {
            field.setTitle(commentMap.get(field.getField()));
        }
    }


    protected String getFetchSql(SqlDefine sqlDefine) {
        return new StringBuilder("select t.* from (")
                .append(sqlDefine.getSelectSql())
                .append(") t ")
                .append(" where ")
                .append(sqlDefine.getPri())
                .append("=")
                .append(":id").toString();
    }

    public Response fetch(Long sqlId, Long recordId) {
        SqlDefine sqlDefine = sqlDefineRepository.findOne(sqlId);
        Map<String, Object> paraMap = new HashedMap();
        paraMap.put("id", recordId);

        //查询指定数据库的数据
        return Response.SUCCESS(this.queryForObject(sqlDefine.getDatasource(), getFetchSql(sqlDefine), paraMap, buildColumnMapRowMapper()));
    }

    protected ColumnMapRowMapper buildColumnMapRowMapper() {
        return new ColumnMapRowMapper() {

            @Override
            protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
                Object columnValue = super.getColumnValue(rs, index);
                if (null != columnValue && columnValue instanceof Long) {
                    return columnValue.toString();
                }
                return columnValue;
            }
        };
    }


    public DataView findByDataViewId(Long id) {
        return dataViewService.findById(id);
    }

    private Response checkPri(SqlDefine sqlDefine) {
        if (StringUtils.isBlank(sqlDefine.getPri())) {
            return Response.FAILURE("主键不能为空,sqlId:" + sqlDefine.getId());
        }
        return Response.SUCCESS();
    }

    public Response<List<FieldModel>> checkSqlDefineConfig(SqlDefine sqlDefine, DataView dataView) {

        //是否包含主键
        Response priCheck = checkPri(sqlDefine);
        if (!priCheck.checkSuccess()) {
            return priCheck;
        }

        //获取修改列
        List<FieldModel> dataViewFields = JSON.parseArray(dataView.getFields(), FieldModel.class);
        if (CollectionUtils.isEmpty(dataViewFields)) {
            return Response.FAILURE("字段集合不能为空,sqlId:" + dataView.getSqlId());
        }
        return Response.SUCCESS(dataViewFields);
    }

    /**
     * 数据唯一校验
     */
    private boolean unduplicated(FieldModel field, SqlDefine sqlDefine, JSONObject rowValue, boolean insert) {
        if (!field.isDuplicated()) {
            return false;
        }
        StringBuffer checkSql = new StringBuffer("select count(1) from ").append(sqlDefine.getTableName());

        //新增
        Map<String, Object> checkParams = new HashedMap();
        if (insert) {
            checkSql.append(" t where ").append(" t.").append(field.getField()).append(" = :").append(field.getField());
            checkParams.put(field.getField(), rowValue.get(field.getField()));
        } else {
            checkSql.append(" t where t.").append(sqlDefine.getPri())
                    .append(" <> :").append(sqlDefine.getPri())
                    .append(" and t.").append(field.getField()).append(" = :").append(field.getField());
            checkParams.put(sqlDefine.getPri(), rowValue.get(sqlDefine.getPri()));
            checkParams.put(field.getField(), rowValue.get(field.getField()));
        }
        return queryForObject(sqlDefine.getDatasource(), checkSql.toString(), checkParams, Long.class) > 0;
    }

    public Response updateByDataViewId(Long id, JSONObject rowValue) {
        DataView dataView = findByDataViewId(id);
        SqlDefine sqlDefine = sqlDefineRepository.findOne(dataView.getSqlId());
        Response<List<FieldModel>> checkResp = checkSqlDefineConfig(sqlDefine, dataView);
        if (!checkResp.checkSuccess()) {
            return checkResp;
        }

        //update SQL
        StringBuffer updateSql = new StringBuffer(" update ").append(sqlDefine.getTableName()).append(" set ");

        //参数绑定
        Map<String, Object> paramMap = new HashMap<>();
        for (FieldModel fieldModel : checkResp.getResult()) {
            if (DataViewConstant.MODIFTY_ENABLE.equals(fieldModel.getUpdateType())) {
                if (this.unduplicated(fieldModel, sqlDefine, rowValue, false)) {
                    return Response.FAILURE(fieldModel.getTitle() + "数据重复");
                }
                updateSql.append(fieldModel.getField()).append("= :")
                        .append(fieldModel.getField()).append(",");
                paramMap.put(fieldModel.getField(), rowValue.get(fieldModel.getField()));
            }
        }

        //sql整理
        updateSql.deleteCharAt(updateSql.lastIndexOf(","));

        //sql条件处理
        StringBuffer whereSql = new StringBuffer().append(" where ")
                .append(sqlDefine.getPri()).append("= :").append(sqlDefine.getPri());
        paramMap.put(sqlDefine.getPri(), rowValue.get(sqlDefine.getPri()));

        //获取参数配置
        OptionsModel optionsModel = JSON.parseObject(dataView.getOptions(), OptionsModel.class);

        //根据版本号更新
        if (StringUtils.isNotBlank(optionsModel.getVersion())) {
            int version = (Integer) rowValue.get(optionsModel.getVersion()) + 1;

            //修改版本号
            updateSql.append(", ").append(optionsModel.getVersion()).append("  = :")
                    .append(optionsModel.getVersion());
            paramMap.put(optionsModel.getVersion(), version);

            //where条件添加版本
            whereSql.append(" and ").append(optionsModel.getVersion()).append("  < :")
                    .append(optionsModel.getVersion());
            paramMap.put(optionsModel.getVersion(), version);
        }
        updateSql = updateSql.append(whereSql);
        if (update(sqlDefine.getDatasource(), updateSql.toString(), paramMap)) {
            return Response.FAILURE(updateSql);
        }
        return Response.SUCCESS();
    }

    protected String getDeleteSql(SqlDefine sqlDefine) {
        return new StringBuffer(" delete from ")
                .append(sqlDefine.getTableName())
                .append(" where ")
                .append(sqlDefine.getPri())
                .append(" = :")
                .append(sqlDefine.getPri()).toString();
    }

    public Response deleteByDataViewId(Long id, JSONObject rowValue) {
        DataView dataView = this.findByDataViewId(id);
        SqlDefine sqlDefine = this.findOne(dataView.getSqlId());
        Response priCheckResp = checkPri(sqlDefine);
        if (!priCheckResp.checkSuccess()) {
            return priCheckResp;
        }
        String deleteSql = getDeleteSql(sqlDefine);

        //参数绑定
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(sqlDefine.getPri(), rowValue.get(sqlDefine.getPri()));
        if (update(sqlDefine.getDatasource(), deleteSql, paramMap)) {
            return Response.FAILURE(deleteSql);
        }
        return Response.SUCCESS();
    }

    public Response createByDataViewId(Long id, JSONObject rowValue) {
        DataView dataView = findByDataViewId(id);
        SqlDefine sqlDefine = findOne(dataView.getSqlId());
        Response<List<FieldModel>> checkResponse = checkSqlDefineConfig(sqlDefine, dataView);
        if (!checkResponse.checkSuccess()) {
            return checkResponse;
        }

        //create sql
        StringBuffer createsql = new StringBuffer("insert into ").append(sqlDefine.getTableName()).append("(");

        //列表达式
        StringBuffer expressionsql = new StringBuffer(") values (");

        //参数绑定
        Map<String, Object> paramMap = new HashMap<>();
        for (FieldModel fieldModel : checkResponse.getResult()) {
            if (!fieldModel.isInsert()) {
                continue;
            }

            if (this.unduplicated(fieldModel, sqlDefine, rowValue, true)) {
                return Response.FAILURE(fieldModel.getTitle() + "数据重复");
            }
            createsql.append(fieldModel.getField()).append(",");
            expressionsql.append(":").append(fieldModel.getField()).append(",");
            paramMap.put(fieldModel.getField(), rowValue.get(fieldModel.getField()));
        }
        createsql = createsql.deleteCharAt(createsql.lastIndexOf(","));
        expressionsql = expressionsql.deleteCharAt(expressionsql.lastIndexOf(",")).append(")");

        //end sql
        String sql = createsql.append(expressionsql).toString();
        if (update(sqlDefine.getDatasource(), sql, paramMap)) {
            return Response.FAILURE(sql);
        }
        return Response.SUCCESS();
    }
}
