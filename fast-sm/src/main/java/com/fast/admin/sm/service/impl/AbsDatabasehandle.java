package com.fast.admin.sm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.response.Response;
import com.fast.admin.rbac.model.TreeNodeModel;
import com.fast.admin.rbac.utils.RecursiveTools;
import com.fast.admin.sm.constant.DataViewConstant;
import com.fast.admin.sm.constant.FieldTypeEnum;
import com.fast.admin.sm.constant.SqlExpression;
import com.fast.admin.sm.constant.TreeNodeHandleType;
import com.fast.admin.sm.domain.DataView;
import com.fast.admin.sm.domain.SqlDefine;
import com.fast.admin.sm.model.*;
import com.fast.admin.sm.repository.SqlDefineRepository;
import com.fast.admin.sm.service.DataViewService;
import com.fast.admin.sm.utils.DataFilter;
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

    //默认空字符串
    private static final String BLANK_STR = "-1";
    private static final String default_top = "0";

    private static final String manipulate_query = "QUERY";

    @Autowired
    SqlDefineRepository sqlDefineRepository;

    @Autowired
    DataViewService dataViewService;

    public SqlDefine findOne(Long sqlId) {
        return sqlDefineRepository.getOne(sqlId);
    }


    // 获取字段SQL
    public String getFieldSql(SqlDefine sqlDefine) {
        if (manipulate_query.equals(sqlDefine.getManipulate())) {
            return String.format("select * from ( %s ) t where 1=2 ", sqlDefine.getSelectSql());
        }
        return String.format("select * from  %s  where 1=2 ", sqlDefine.getTableName());
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
        return String.format(" select t.* from ( %s ) t where 1 = 2", sql);
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
        String sqlformat = "select t.* from ( %s ) t where t.%s = :id ";
        return String.format(sqlformat, sqlDefine.getSelectSql(), sqlDefine.getPri());
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
        StringBuffer checkSql = new StringBuffer(String.format(" select count(1) from  %s t ", sqlDefine.getTableName()));

        //新增
        Map<String, Object> checkParams = new HashedMap();
        if (insert) {
            String whereSql = String.format(" where  t.%s =:%s ", field.getField(), field.getField());
            checkSql.append(whereSql);
            checkParams.put(field.getField(), rowValue.get(field.getField()));
        } else {
            String whereSql = String.format(" where  t.%s <>:%s  and t.%s = :%s ", sqlDefine.getPri(), sqlDefine.getPri(),
                    field.getField(), field.getField());
            checkSql.append(whereSql);
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
        StringBuffer updateSql = new StringBuffer(String.format(" update  %s  set ", sqlDefine.getTableName()));
        Map<String, Object> paramMap = new HashMap<>();
        for (FieldModel fieldModel : checkResp.getResult()) {
            if (DataViewConstant.MODIFTY_ENABLE.equals(fieldModel.getUpdateType())) {
                if (this.unduplicated(fieldModel, sqlDefine, rowValue, false)) {
                    return Response.FAILURE(fieldModel.getTitle() + "数据重复");
                }
                updateSql.append(String.format(" %s = :%s ,", fieldModel.getField(), fieldModel.getField()));
                paramMap.put(fieldModel.getField(), rowValue.get(fieldModel.getField()));
            }
        }
        updateSql.deleteCharAt(updateSql.lastIndexOf(","));

        //sql条件处理
        StringBuffer whereSql = new StringBuffer(String.format(" where %s = :%s ", sqlDefine.getPri(), sqlDefine.getPri()));
        paramMap.put(sqlDefine.getPri(), rowValue.get(sqlDefine.getPri()));

        //获取参数配置
        OptionsModel optionsModel = JSON.parseObject(dataView.getOptions(), OptionsModel.class);

        //根据版本号更新
        if (StringUtils.isNotBlank(optionsModel.getVersion())) {
            int version = (Integer) rowValue.get(optionsModel.getVersion()) + 1;

            //修改版本号
            updateSql.append(String.format(", %s = :%s ", optionsModel.getVersion(), optionsModel.getVersion()));

            //where条件添加版本
            whereSql.append(String.format(" and %s < :%s ", optionsModel.getVersion(), optionsModel.getVersion()));
            paramMap.put(optionsModel.getVersion(), version);
        }
        updateSql = updateSql.append(whereSql);
        if (update(sqlDefine.getDatasource(), updateSql.toString(), paramMap)) {
            return Response.FAILURE(updateSql);
        }
        return Response.SUCCESS();
    }

    protected String getDeleteSql(SqlDefine sqlDefine) {
        return String.format(" delete from %s where %s = :%s ", sqlDefine.getTableName(), sqlDefine.getPri(), sqlDefine.getPri());
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
        StringBuffer createsql = new StringBuffer(String.format(" insert into %s ( ", sqlDefine.getTableName()));

        //列表达式
        StringBuffer expressionsql = new StringBuffer(" ) values ( ");

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

    public Response<BootstrapPageResult> getBootstrapTableResponse(Integer pageSize, Integer pageNumber,
                                                                   String searchText,
                                                                   String sortName, String sortOrder, Long sqlId,
                                                                   BootstrapSearchParam bootstrapSearchParam) {
        BootstrapPageResult pageResultForBootstrap = new BootstrapPageResult();
        SqlDefine sqlDefine = sqlDefineRepository.findOne(sqlId);
        DataFilter dataFilter = DataFilter.getInstance();
        dataFilter.setQuerySql(sqlDefine.getSelectSql());
        dataFilter.setSortName(sortName);
        dataFilter.setSortOrder(sortOrder);

        // 条件
        List<ConditionModel> conditionModelList = bootstrapSearchParam.getSearchArray();
        if (CollectionUtils.isEmpty(conditionModelList)) {
            conditionModelList = new ArrayList<>();
        }

        // 解析ztree
        ConditionModel ztreeConditionModel = this.getTreeNode(bootstrapSearchParam.getTreeOptions());
        if (null != ztreeConditionModel) {
            conditionModelList.add(ztreeConditionModel);
        }
        dataFilter.addCondition(conditionModelList);
        List<Map<String, Object>> list =
                this.query(sqlDefine.getDatasource(), dataFilter.createPager(pageNumber, pageSize), dataFilter.getParams(), buildColumnMapRowMapper());
        pageResultForBootstrap.setRows(list);

        // 查询总数
        Long count = this.queryForObject(sqlDefine.getDatasource(), dataFilter.countSql(), dataFilter.getParams(), Long.class);
        pageResultForBootstrap.setTotal(count);
        return Response.SUCCESS(pageResultForBootstrap);
    }

    /**
     * 获取树节点条件
     */
    private ConditionModel getTreeNode(TreeOptionsFilterModel treeOptionsModel) {
        if (null == treeOptionsModel || !treeOptionsModel.isVisible()) {
            return null;
        }

        //获取sqlDefine
        SqlDefine sqlDefine = findOne(treeOptionsModel.getSqlId());

        //默认是空字符串
        String idValue = StringUtils.isNotBlank(treeOptionsModel.getNodeValue()) ? treeOptionsModel.getNodeValue() : BLANK_STR;
        List<Map<String, Object>> result = new ArrayList();

        // 构建获取下级sql
        String sql = buildChildSql(sqlDefine.getSelectSql(), treeOptionsModel.getPidKey());
        Map<String, Object> paramMap = new HashMap();
        switch (treeOptionsModel.getScope()) {
            case TreeNodeHandleType.TREEHANDLETYPE_ALL:
                paramMap.put(treeOptionsModel.getPidKey(), idValue);
                List<Map<String, Object>> items = this.queryForList(sqlDefine.getDatasource(), sql, paramMap);
                result = RecursiveTools.forEachItems(items, (Map<String, Object> item) -> {
                    Map<String, Object> paraMap = new HashMap<>();
                    paraMap.put(treeOptionsModel.getPidKey(), item.get(treeOptionsModel.getIdKey()));
                    return this.queryForList(sqlDefine.getDatasource(), sql, paraMap);
                });
                result.addAll(items);
                break;
            case TreeNodeHandleType.TREEHANDLETYPE_CHILD:
                paramMap.put(treeOptionsModel.getPidKey(), idValue);
                result = this.queryForList(sqlDefine.getDatasource(), sql, paramMap);
                break;
            case TreeNodeHandleType.TREEHANDLETYPE_SELF:
                paramMap.put(treeOptionsModel.getPidKey(), idValue);
                result = this.queryForList(sqlDefine.getDatasource(), sql, paramMap);
                break;
            default:
        }

        ConditionModel conditionDto = new ConditionModel();
        conditionDto.setField(treeOptionsModel.getForeignKey());
        conditionDto.setExpression(SqlExpression.IN);
        conditionDto.setValue(appendIdIn(result, treeOptionsModel.getIdKey()));
        return conditionDto;
    }

    /**
     * 拼接in字符
     */
    private List appendIdIn(List<Map<String, Object>> mapList, String key) {
        List<Object> idIn = Lists.newArrayList();
        for (Map<String, Object> item : mapList) {
            idIn.add(item.get(key));
        }
        return idIn;
    }

    /**
     * tree查询SQL
     */
    private String buildChildSql(String sql, String field) {
        return String.format("select t.* from ( %s ) t where t.%s = :%s ", sql, field, field);
    }

    /**
     * 根据nodeId获取所有子节点
     */
    private List<Map<String, Object>> findAllNode(String sql, Object pId, TreeOptionsModel treeVo, Long dataSourceId) {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put(treeVo.getPidKey(), pId);
        List<Map<String, Object>> queryResult = Lists.newArrayList();
        List<Map<String, Object>> result = this.query(dataSourceId, sql, paramMap, buildColumnMapRowMapper());
        if (!CollectionUtils.isEmpty(result)) {
            List<Map<String, Object>> subResult = null;
            for (Map<String, Object> subMap : result) {
                subResult = findAllNode(sql, subMap.get(treeVo.getIdKey()), treeVo, dataSourceId);
                if (!CollectionUtils.isEmpty(subResult)) {
                    queryResult.addAll(subResult);
                }
            }
            queryResult.addAll(result);
        }
        return queryResult;
    }

    public Response ztree(ZtreeModel ztreeModel) {
        SqlDefine sqlDefine = findOne(ztreeModel.getSqlId());
        StringBuilder sqlBuilder = new StringBuilder(String.format("select t.* from ( %s ) t ", sqlDefine.getSelectSql()));
        Map<String, Object> paraMap = new HashedMap();

        //异步加载
        if (ztreeModel.isEnable()) {
            sqlBuilder.append(" where t.");
            if (null == ztreeModel.getId()) {
                sqlBuilder.append(ztreeModel.getPidKey()).append(" = ''");
            } else {
                sqlBuilder.append(ztreeModel.getPidKey()).append("= :parent");
                paraMap.put("parent", ztreeModel.getId());
            }
        }
        List<Map<String, Object>> dataList = query(sqlDefine.getDatasource(), sqlBuilder.toString(), paraMap, buildColumnMapRowMapper());

        //异步加载判断是否parent
        if (ztreeModel.isEnable()) {
            for (Map<String, Object> node : dataList) {
                node.put("isParent", this.isParent(node.get(ztreeModel.getIdKey()).toString(), sqlDefine,
                        ztreeModel));
            }
        }
        return Response.SUCCESS(dataList);
    }

    /**
     * TODO leaf 优化
     * 判断是否上级节点
     */
    private boolean isParent(String parent, SqlDefine sqlDefine, ZtreeModel ztreeModel) {
        String sql = String.format("select t.* from ( %s ) t where t.%s = :parent ", sqlDefine.getSelectSql(), ztreeModel.getPidKey());
        Map<String, Object> paraMap = new HashedMap();
        paraMap.put("parent", parent);
        List<Map<String, Object>> dataList = this.queryForList(sqlDefine.getDatasource(), sql, paraMap);
        return dataList.size() > 0;
    }

    public Response getTree(ZtreeModel ztreeModel) {
        SqlDefine sqlDefine = findOne(ztreeModel.getSqlId());
        String sql = String.format("select t.* from ( %s ) t ", sqlDefine.getSelectSql());
        Map<String, Object> paraMap = new HashedMap();
        List<Map<String, Object>> dataList = this.getNamedParameterJdbcTemplate(sqlDefine.getDatasource()).query(sql, paraMap, buildColumnMapRowMapper());
        List<TreeNodeModel> dataSource = Lists.newArrayList();
        List<TreeNodeModel> parentNodeItemList = Lists.newArrayList();
        TreeNodeModel nodeItemModel;
        for (Map<String, Object> item : dataList) {
            nodeItemModel = TreeNodeModel.builder()
                    .key(toString(item.get(ztreeModel.getIdKey())))
                    .title(toString(item.get(ztreeModel.getName())))
                    .pid(toString(item.get(ztreeModel.getPidKey())))
                    .id(toString(item.get(sqlDefine.getPri())))
                    .build();
            dataSource.add(nodeItemModel);
            if (nodeItemModel.getPid().equalsIgnoreCase(default_top)) {
                nodeItemModel.setExpanded(true);
                parentNodeItemList.add(nodeItemModel);
            }
        }
        List<TreeNodeModel> nodeModelList = RecursiveTools.forEachTreeItems(parentNodeItemList, (TreeNodeModel item) -> {
            List<TreeNodeModel> nodeItemModelList = getChildMapList(item.getId(), dataSource);
            item.setChildren(nodeItemModelList);
            if (CollectionUtils.isEmpty(nodeItemModelList)) {
                item.setLeaf(true);
            }
            return nodeItemModelList;
        });
        return Response.SUCCESS(nodeModelList);
    }

    private String toString(Object value) {
        if (null != value) {
            return value.toString();
        }
        return "";
    }

    private List<TreeNodeModel> getChildMapList(Object pid, List<TreeNodeModel> dataList) {
        List<TreeNodeModel> mapList = Lists.newArrayList();
        for (TreeNodeModel item : dataList) {
            if (StringUtils.isNotBlank(item.getPid()) && item.getPid().equals(pid)) {
                mapList.add(item);
            }
        }
        return mapList;
    }
}
