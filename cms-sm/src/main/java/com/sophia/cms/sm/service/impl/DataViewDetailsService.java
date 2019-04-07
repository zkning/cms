package com.sophia.cms.sm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.rbac.model.TreeNodeModel;
import com.sophia.cms.rbac.service.DictService;
import com.sophia.cms.rbac.utils.RecursiveTools;
import com.sophia.cms.sm.constant.*;
import com.sophia.cms.sm.domain.DataView;
import com.sophia.cms.sm.domain.SqlDefine;
import com.sophia.cms.sm.jdbc.CustomJdbcTemplate;
import com.sophia.cms.sm.mapper.SqlDefineMapper;
import com.sophia.cms.sm.model.*;
import com.sophia.cms.sm.service.DataViewService;
import com.sophia.cms.sm.utils.DataFilter;
import com.sophia.cms.sm.utils.MaskingUtils;
import com.sophia.cms.sm.utils.SimpleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class DataViewDetailsService extends CustomJdbcTemplate {

    //默认空字符串
    private static final String BLANK_STR = "-1";
    private static final String default_top = "0";

    @Autowired
    SqlDefineMapper sqlDefineMapper;

    @Autowired
    DataViewService dataViewService;

    @Autowired
    DictService dictService;

    public SqlDefine findOne(Long sqlId) {
        return sqlDefineMapper.selectById(sqlId);
    }


    // 获取字段SQL
    public String getFieldSql(SqlDefine sqlDefine) {
        if (SQLTypeEnum.QUERY.getCode().equals(sqlDefine.getSqlType())) {
            return String.format("select _t.* from ( %s ) as _t  where 1=2 ", sqlDefine.getSelectSql());
        }
        return String.format("select * from  %s  where 1=2 ", sqlDefine.getTableName());
    }


    public Map<String, Object> getFieldUpperCaseMap(SqlRowSet resultSet) {
        SqlRowSetMetaData srsmd = resultSet.getMetaData();
        Map<String, Object> fmap = new HashMap<>();
        for (int index = 1; index <= srsmd.getColumnCount(); index++) {
            fmap.put(srsmd.getColumnLabel(index).toUpperCase(), srsmd.getColumnLabel(index));
        }
        return fmap;
    }

    public Map<String, String> getFieldUpperCommentMap(SqlDefine sqlDefine, SqlRowSet resultSet) throws Exception {
        Map<String, String> map = new HashMap<>();
        List<SchemaColumnModel> columnModels = getInformationSchemaColumns(sqlDefine.getTableName(), sqlDefine.getDatasource());
        columnModels.forEach(item -> {
            map.put(item.getColumnName().toUpperCase(), item.getColumnComment());
        });
        return map;
    }

    public String getInformationSchemaColumnsSql() {
        return "SELECT t.column_name,t.column_key,t.column_comment FROM information_schema.COLUMNS t where t.table_name = ? and t.table_schema = ? ";
    }

    public List<SchemaColumnModel> getInformationSchemaColumns(String tablename, Long datasource) throws Exception {
        Connection conn = null;
        try {
            JdbcTemplate jdbcTemplate = this.getJdbcTemplate(datasource);
            conn = jdbcTemplate.getDataSource().getConnection();
            LinkedHashMap<String, Object> pm = new LinkedHashMap();
            pm.put("tableName", tablename);
            pm.put("tableschema", conn.getCatalog());
            List<SchemaColumnModel> dataList = jdbcTemplate.query(this.getInformationSchemaColumnsSql(), SimpleUtils.linkedHashMapToValues(pm),
                    (ResultSet resultSet, int i) -> {
                        SchemaColumnModel ret = new SchemaColumnModel();
                        ret.setColumnName(resultSet.getString("column_name"));
                        ret.setColumnKey(resultSet.getString("column_key").toUpperCase());
                        ret.setColumnComment(resultSet.getString("column_comment"));
                        return ret;
                    });
            return dataList;
        } finally {
            this.destroyConnection(conn);
        }
    }

    public String getInformationSchemaTableSql() {
        return "select table_name,table_comment from information_schema.tables " +
                "where table_schema= ?  and TABLE_NAME like ? ";
    }

    public List<SchemaTableModel> getInformationSchemaTable(SchemaTableSearchModel schemaTableSearchModel) throws Exception {
        Connection conn = null;
        try {
            List<SchemaTableModel> dataList = new ArrayList<>();
            if (StringUtils.isBlank(schemaTableSearchModel.getTablename())) {
                return dataList;
            }
            JdbcTemplate jdbcTemplate = this.getJdbcTemplate(schemaTableSearchModel.getDatasource());
            conn = jdbcTemplate.getDataSource().getConnection();
            LinkedHashMap<String, Object> param = new LinkedHashMap();

            //get dbname
            param.put("tableschema", conn.getCatalog());
            param.put("tableName", schemaTableSearchModel.getTablename() + '%');
            dataList = jdbcTemplate.query(getInformationSchemaTableSql(), SimpleUtils.linkedHashMapToValues(param),
                    (ResultSet resultSet, int i) -> {
                        SchemaTableModel ret = new SchemaTableModel();
                        ret.setTableName(resultSet.getString("table_name"));
                        ret.setTableComment(resultSet.getString("table_comment"));
                        return ret;
                    });
            return dataList;
        } finally {
            this.destroyConnection(conn);
        }
    }

    public String getShowFullColumnsSql(String sql) {
        return String.format(" select t.* from ( %s ) t where 1 = 2", sql);
    }

    public List<FieldModel> showFullColumns(SqlDefine sqlDefine, Map<String, Object> fieldMap, Map<String, String> commentMap)
            throws Exception {
        Connection conn = null;
        try {

            //获取sql查询所有列
            DataSource dataSource = this.getJdbcTemplate(sqlDefine.getDatasource()).getDataSource();
            conn = dataSource.getConnection();
            ResultSet resultSet = conn.prepareStatement(getShowFullColumnsSql(sqlDefine.getSelectSql())).executeQuery();
            List<FieldModel> list = Lists.newArrayList();

            // 获取sql元数据
            ResultSetMetaData srsmd = resultSet.getMetaData();
            for (int index = 1; index <= srsmd.getColumnCount(); index++) {
                FieldModel field = this.getColumnModel(srsmd, index);
                this.fieldFormat(field, fieldMap, commentMap);
                field.setSort(index);
                list.add(field);
            }
            return list;
        } finally {
            this.destroyConnection(conn);
        }
    }

    public FieldModel getColumnModel(ResultSetMetaData srsmd, int index) throws SQLException {
        FieldModel fm = new FieldModel();

        //  as 后的值 ，getColumnName 原始值
        fm.setField(srsmd.getColumnLabel(index));
        fm.setMaxlength(srsmd.getPrecision(index));
        fm.setDataType(srsmd.getColumnTypeName(index));
        fm.setFieldType(FieldTypeEnum.TEXT.getValue());
        fm.setAlign(DataViewConst.align_center);
        fm.setHalign(DataViewConst.align_center);
        fm.setDuplicated(false);
        fm.setSort(index);
        return fm;
    }

    public void fieldFormat(FieldModel field, Map<String, Object> fieldMap, Map<String, String> commentMap) {

        // 表包含的SQL查询列,属性特殊处理
        if (fieldMap.containsKey(field.getField())) {
            field.setUpdateType(DataViewConst.MODIFTY_HIDE);
            field.setInsert(true);
            field.setVisible(true);
            field.setView(true);
        }

        // 设置title
        if (commentMap.containsKey(field.getField())) {
            field.setTitle(commentMap.get(field.getField()));
        } else {
            field.setTitle(field.getField());
        }
    }

    protected String getFetchSql(SqlDefine sqlDefine) {
        String sqlformat = "select t.* from ( %s ) t where t.%s = :id ";
        return String.format(sqlformat, sqlDefine.getSelectSql(), sqlDefine.getPri());
    }

    public Response fetch(Long dataViewId, String recordId) {
        DataView dataView = this.findByDataViewId(dataViewId);
        SqlDefine sqlDefine = sqlDefineMapper.selectById(dataView.getSqlId());
        Map<String, Object> paraMap = new HashedMap();
        paraMap.put(sqlDefine.getPri().toLowerCase(), recordId);

        //查询指定数据库的数据
        return Response.SUCCESS(this.queryForObject(sqlDefine.getDatasource(), getFetchSql(sqlDefine), paraMap, getColumnMapRowMapper()));
    }

    protected ColumnMapRowMapper getColumnMapRowMapper() {
        return new ColumnMapRowMapper() {

            @Override
            protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
                Object cv = JdbcUtils.getResultSetValue(rs, index);
                if (null == cv) {
                    return cv;
                }
                if (cv instanceof Long) {
                    return cv.toString();
                }
                return cv;
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
        List<FieldModel> dvf = JSON.parseArray(dataView.getFields(), FieldModel.class);
        if (CollectionUtils.isEmpty(dvf)) {
            return Response.FAILURE("字段集合不能为空,sqlId:" + dataView.getSqlId());
        }
        return Response.SUCCESS(dvf);
    }

    /**
     * 数据唯一校验
     */
    private boolean unduplicated(FieldModel field, SqlDefine sqlDefine, JSONObject rowValue, boolean isAdd) {
        if (!field.isDuplicated()) {
            return false;
        }
        StringBuffer sql = new StringBuffer(String.format(" select count(1) from  %s t ", sqlDefine.getTableName()));

        //新增
        Map<String, Object> param = new HashedMap();
        if (isAdd) {
            String wsql = String.format(" where  t.%s =:%s ", field.getField(), field.getField());
            sql.append(wsql);
            param.put(field.getField(), rowValue.get(field.getField()));
        } else {
            String wsql = String.format(" where  t.%s <>:%s  and t.%s = :%s ", sqlDefine.getPri(), sqlDefine.getPri(),
                    field.getField(), field.getField());
            sql.append(wsql);
            param.put(sqlDefine.getPri(), rowValue.get(sqlDefine.getPri()));
            param.put(field.getField(), rowValue.get(field.getField()));
        }
        return queryForObject(sqlDefine.getDatasource(), sql.toString(), param, Long.class) > 0;
    }

    public Response updateByDataViewId(Long id, JSONObject rv) {
        DataView dv = findByDataViewId(id);
        SqlDefine sd = sqlDefineMapper.selectById(dv.getSqlId());
        Response<List<FieldModel>> checkResp = checkSqlDefineConfig(sd, dv);
        if (!checkResp.checkSuccess()) {
            return checkResp;
        }

        //update SQL
        StringBuffer udsql = new StringBuffer(String.format(" update  %s  set ", sd.getTableName()));
        Map<String, Object> param = new HashMap<>();
        for (FieldModel fieldModel : checkResp.getResult()) {
            if (DataViewConst.MODIFTY_ENABLE.equals(fieldModel.getUpdateType())) {
                if (this.unduplicated(fieldModel, sd, rv, false)) {
                    return Response.FAILURE(fieldModel.getTitle() + "数据重复");
                }
                udsql.append(String.format(" %s = :%s ,", fieldModel.getField(), fieldModel.getField()));
                param.put(fieldModel.getField(), rv.get(fieldModel.getField()));
            }
        }
        udsql.deleteCharAt(udsql.lastIndexOf(","));

        //sql条件处理
        StringBuffer wsql = new StringBuffer(String.format(" where %s = :%s ", sd.getPri(), sd.getPri()));
        param.put(sd.getPri(), rv.get(sd.getPri()));

        //获取参数配置
        OptionsModel optm = JSON.parseObject(dv.getOptions(), OptionsModel.class);

        //根据版本号更新
        if (StringUtils.isNotBlank(optm.getVersion())) {
            int version = (Integer) rv.get(optm.getVersion()) + 1;

            //修改版本号
            udsql.append(String.format(", %s = :%s ", optm.getVersion(), optm.getVersion()));

            //where条件添加版本
            wsql.append(String.format(" and %s < :%s ", optm.getVersion(), optm.getVersion()));
            param.put(optm.getVersion(), version);
        }
        udsql = udsql.append(wsql);
        if (update(sd.getDatasource(), udsql.toString(), param)) {
            return Response.FAILURE(udsql);
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

    public Response createByDataViewId(Long id, JSONObject rv) {
        DataView dv = findByDataViewId(id);
        SqlDefine sd = findOne(dv.getSqlId());
        Response<List<FieldModel>> checkResponse = checkSqlDefineConfig(sd, dv);
        if (!checkResponse.checkSuccess()) {
            return checkResponse;
        }

        // create sql
        StringBuffer csql = new StringBuffer(String.format(" insert into %s ( ", sd.getTableName()));

        // 列表达式
        StringBuffer exsql = new StringBuffer(" ) values ( ");

        //参数绑定
        Map<String, Object> paramMap = new HashMap<>();
        for (FieldModel fieldModel : checkResponse.getResult()) {
            if (!fieldModel.isInsert()) {
                continue;
            }
            if (this.unduplicated(fieldModel, sd, rv, true)) {
                return Response.FAILURE(fieldModel.getTitle() + "数据重复");
            }
            csql.append(fieldModel.getField()).append(",");
            exsql.append(":").append(fieldModel.getField()).append(",");
            paramMap.put(fieldModel.getField(), rv.get(fieldModel.getField()));
        }
        csql = csql.deleteCharAt(csql.lastIndexOf(","));
        exsql = exsql.deleteCharAt(exsql.lastIndexOf(",")).append(")");

        //end sql
        String sql = csql.append(exsql).toString();
        if (update(sd.getDatasource(), sql, paramMap)) {
            return Response.FAILURE(sql);
        }
        return Response.SUCCESS();
    }

    public Response<BootstrapPageResult> getBootstrapTableDataResponse(Integer pageSize,
                                                                       Integer pageNumber,
                                                                       String searchText,
                                                                       String sortName, String sortOrder, Long dataViewId,
                                                                       BootstrapSearchParam sp) {
        DataView dv = this.findByDataViewId(dataViewId);
        BootstrapPageResult ret = new BootstrapPageResult();
        SqlDefine sd = sqlDefineMapper.selectById(dv.getSqlId());
        if (SQLDefineStateEnum.UN_ISSUE.getCode().equals(sd.getState())) {
            return Response.FAILURE(sd.getSqlName() + "待发布");
        }
        DataFilter df = DataFilter.getInstance();
        df.setQuerySql(sd.getSelectSql());
        df.setSortName(sortName);
        df.setSortOrder(sortOrder);

        // 条件
        List<ConditionModel> cms = sp.getSearchArray();
        if (CollectionUtils.isEmpty(cms)) {
            cms = new ArrayList<>();
        }

        // parse tree
        ConditionModel cm = this.getTreeNode(sp.getTreeOptions());
        if (null != cm) {
            cms.add(cm);
        }
        df.addCondition(cms);
        Map<String, Integer> maskingMap = new HashMap<>();
        Map<String, Long> dictMap = new HashMap<>();
        List<FieldModel> fms = JSONArray.parseArray(dv.getFields(), FieldModel.class);
        for (FieldModel fm : fms) {
            if (null != fm.getMasking()) {
                maskingMap.put(fm.getField(), fm.getMasking());
            }
            if (StringUtils.isNotBlank(fm.getDict())) {
                dictMap.put(fm.getField(), Long.valueOf(fm.getDict()));
            }
        }
        List<Map<String, Object>> rows = this.query(sd.getDatasource(),
                df.createPager(pageNumber, pageSize),
                df.getParams(),
                new ColumnMapRowMapper() {

                    @Override
                    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
                        Object cv = JdbcUtils.getResultSetValue(rs, index);
                        if (null == cv) {
                            return cv;
                        }

                        // 脱敏
                        String cn = rs.getMetaData().getColumnName(index);
                        if (!maskingMap.isEmpty() && maskingMap.containsKey(cn)) {
                            return MaskingUtils.masking(maskingMap.get(cn), cv.toString());
                        }

                        // 字典转换
                        if (!dictMap.isEmpty() && dictMap.containsKey(cn)) {
                            return dictService.getTextByPid(dictMap.get(cn)).get(cv.toString());
                        }
                        if (cv instanceof Long) {
                            return cv.toString();
                        }
                        return cv;
                    }
                });
        ret.setRows(rows);
        Long count = this.queryForObject(sd.getDatasource(), df.countSql(), df.getParams(), Long.class);
        ret.setTotal(count);
        return Response.SUCCESS(ret);
    }

    /**
     * 获取树节点条件
     */
    private ConditionModel getTreeNode(TreeOptionsFilterModel tm) {
        if (null == tm || !tm.isVisible()) {
            return null;
        }

        //获取sqlDefine
        SqlDefine sd = findOne(tm.getSqlId());

        //默认是空字符串
        String idValue = StringUtils.isNotBlank(tm.getNodeValue()) ? tm.getNodeValue() : BLANK_STR;
        List<Map<String, Object>> ret = new ArrayList();

        // 构建获取下级sql
        String sql = getChildSql(sd.getSelectSql(), tm.getPidKey());
        Map<String, Object> param = new HashMap();
        switch (tm.getScope()) {
            case TreeNodeTypeConst.TREEHANDLETYPE_ALL:
                param.put(tm.getPidKey(), idValue);
                List<Map<String, Object>> items = this.queryForList(sd.getDatasource(), sql, param);
                ret = RecursiveTools.forEachItems(items, (Map<String, Object> item) -> {
                    Map<String, Object> paraMap = new HashMap<>();
                    paraMap.put(tm.getPidKey(), item.get(tm.getIdKey()));
                    return this.queryForList(sd.getDatasource(), sql, paraMap);
                });
                ret.addAll(items);
                break;
            case TreeNodeTypeConst.TREEHANDLETYPE_CHILD:
                param.put(tm.getPidKey(), idValue);
                ret = this.queryForList(sd.getDatasource(), sql, param);
                break;
            case TreeNodeTypeConst.TREEHANDLETYPE_SELF:
                param.put(tm.getPidKey(), idValue);
                ret = this.queryForList(sd.getDatasource(), sql, param);
                break;
            default:
        }
        ConditionModel cd = new ConditionModel();
        cd.setField(tm.getForeignKey());
        cd.setExpression(SQLExprConst.IN);
        cd.setValue(appendIdIn(ret, tm.getIdKey()));
        return cd;
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
    private String getChildSql(String sql, String field) {
        return String.format("select t.* from ( %s ) t where t.%s = :%s ", sql, field, field);
    }

    /**
     * 根据nodeId获取所有子节点
     */
    private List<Map<String, Object>> findAllNode(String sql, Object pId, TreeOptionsModel tm, Long dataSourceId) {
        Map<String, Object> param = new HashMap();
        param.put(tm.getPidKey(), pId);
        List<Map<String, Object>> qret = Lists.newArrayList();
        List<Map<String, Object>> ret = this.query(dataSourceId, sql, param, getColumnMapRowMapper());
        if (!CollectionUtils.isEmpty(ret)) {
            List<Map<String, Object>> subResult = null;
            for (Map<String, Object> subMap : ret) {
                subResult = findAllNode(sql, subMap.get(tm.getIdKey()), tm, dataSourceId);
                if (!CollectionUtils.isEmpty(subResult)) {
                    qret.addAll(subResult);
                }
            }
            qret.addAll(ret);
        }
        return qret;
    }


    /**
     * TODO leaf 优化
     * 判断是否上级节点
     */
    private boolean isParent(String parent, SqlDefine sqlDefine, ZtreeModel ztreeModel) {
        String sql = String.format("select t.* from ( %s ) t where t.%s = :parent ", sqlDefine.getSelectSql(), ztreeModel.getPidKey());
        Map<String, Object> param = new HashedMap();
        param.put("parent", parent);
        List<Map<String, Object>> dataList = this.queryForList(sqlDefine.getDatasource(), sql, param);
        return dataList.size() > 0;
    }

    public Response getTree(ZtreeModel ztreeModel) {
        SqlDefine sd = findOne(ztreeModel.getSqlId());
        String sql = String.format("select t.* from ( %s ) t ", sd.getSelectSql());
        Map<String, Object> param = new HashedMap();
        List<Map<String, Object>> dataList = this.getNamedParameterJdbcTemplate(sd.getDatasource()).query(sql, param, getColumnMapRowMapper());
        List<TreeNodeModel> tnms = Lists.newArrayList();
        List<TreeNodeModel> pns = Lists.newArrayList();
        TreeNodeModel tnm;
        for (Map<String, Object> item : dataList) {
            tnm = TreeNodeModel.builder()
                    .key(fomartVal(item.get(ztreeModel.getIdKey())))
                    .title(fomartVal(item.get(ztreeModel.getName())))
                    .pid(fomartVal(item.get(ztreeModel.getPidKey())))
                    .id(fomartVal(item.get(sd.getPri())))
                    .build();
            tnms.add(tnm);
            if (tnm.getPid().equalsIgnoreCase(default_top)) {
                tnm.setExpanded(true);
                pns.add(tnm);
            }
        }
        List<TreeNodeModel> nml = RecursiveTools.forEachTreeItems(pns, (TreeNodeModel item) -> {
            List<TreeNodeModel> tnl = getChildMapList(item.getId(), tnms);
            item.setChildren(tnl);
            if (CollectionUtils.isEmpty(tnl)) {
                item.setLeaf(true);
            }
            return tnl;
        });
        return Response.SUCCESS(nml);
    }

    private String fomartVal(Object value) {
        if (null != value) {
            return value.toString();
        }
        return "";
    }

    private List<TreeNodeModel> getChildMapList(Object pid, List<TreeNodeModel> dataList) {
        List<TreeNodeModel> ml = Lists.newArrayList();
        for (TreeNodeModel item : dataList) {
            if (StringUtils.isNotBlank(item.getPid()) && item.getPid().equals(pid)) {
                ml.add(item);
            }
        }
        return ml;
    }
}
