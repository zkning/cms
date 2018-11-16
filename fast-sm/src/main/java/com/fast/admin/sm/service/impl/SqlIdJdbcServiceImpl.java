package com.fast.admin.sm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.response.Response;
import com.fast.admin.rbac.model.TreeNodeModel;
import com.fast.admin.sm.constant.SqlDefineStatusEnum;
import com.fast.admin.sm.constant.SqlExpression;
import com.fast.admin.sm.model.*;
import com.fast.admin.sm.repository.SqlDefineRepository;
import com.fast.admin.sm.domain.SqlDefine;
import com.fast.admin.sm.factory.JdbcTemplateFactory;
import com.fast.admin.sm.constant.TreeNodeHandleType;
import com.fast.admin.sm.service.DataViewService;
import com.fast.admin.sm.service.SqlIdJdbcService;
import com.fast.admin.rbac.utils.RecursiveTools;
import com.fast.admin.sm.utils.DataFilter;
import com.fast.admin.sm.utils.SimpleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zkning
 */
@Slf4j
@Service
public class SqlIdJdbcServiceImpl extends AbsDatabasehandle implements SqlIdJdbcService {

    @Autowired
    DataViewService dataViewService;

    @Autowired
    SqlDefineRepository sqlDefineRepository;

    @Autowired
    JdbcTemplateFactory jdbcTemplateFactory;


    //默认空字符串
    private static final String BLANK_STR = "-1";
    private static final String default_top = "0";

    @Override
    public List<FieldModel> showFullColumnsBySqlId(Long sqlId) throws Exception {
        SqlDefine sqlDefine = sqlDefineRepository.findOne(sqlId);

        //获取主表实际列用来过滤
        String resultSql = new StringBuffer("select * from ").append(sqlDefine.getTableName()).append(" t where 1=2 ").toString();

        // 获取table sqlRowSet信息
        SqlRowSet resultSet = getSqlRowSet(sqlDefine.getDatasource(), resultSql, new HashMap<>());

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
    public Response fetch(Long sqlId, Long recordId) {
        return super.fetch(sqlId, recordId);
    }

    @Override
    public Response<BootstrapPageResult>
    getBootstrapTableResponse(BootstrapSearchParam bootstrapSearchParam, Long sqlId) {
        return this.getBootstrapTableResponse(bootstrapSearchParam.getPageSize(), bootstrapSearchParam.getPageNumber()
                , bootstrapSearchParam.getSearchText(), bootstrapSearchParam.getSortName()
                , bootstrapSearchParam.getSortOrder(), sqlId
                , bootstrapSearchParam);
    }

    /**
     * 默认获取bootstrapTable服务
     */
    @Override
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
        return new StringBuffer("select t.* from (").append(sql).append(") t where t.")
                .append(field)
                .append(" =:")
                .append(field).toString();
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

    @Override
    public Response getTree(ZtreeModel ztreeModel) {
        SqlDefine sqlDefine = findOne(ztreeModel.getSqlId());
        StringBuilder sqlBuilder = new StringBuilder("select t.* from (");
        sqlBuilder.append(sqlDefine.getSelectSql()).append(") t ");
        Map<String, Object> paraMap = new HashedMap();
        List<Map<String, Object>> dataList = this.getNamedParameterJdbcTemplate(sqlDefine.getDatasource())
                .query(sqlBuilder.toString(), paraMap, buildColumnMapRowMapper());

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

    private List<TreeNodeModel> getChildMapList(Object pid, List<TreeNodeModel> dataList) {
        List<TreeNodeModel> mapList = Lists.newArrayList();
        for (TreeNodeModel item : dataList) {
            if (StringUtils.isNotBlank(item.getPid()) && item.getPid().equals(pid)) {
                mapList.add(item);
            }
        }
        return mapList;
    }

    private String toString(Object value) {
        if (null != value) {
            return value.toString();
        }
        return "";
    }

    @Override
    public Response ztree(ZtreeModel ztreeModel) {
        SqlDefine sqlDefine = findOne(ztreeModel.getSqlId());
        StringBuilder sqlBuilder = new StringBuilder("select t.* from (");
        sqlBuilder.append(sqlDefine.getSelectSql()).append(") t ");
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
        String sql = new StringBuilder("select t.* from (")
                .append(sqlDefine.getSelectSql())
                .append(") t where t.")
                .append(ztreeModel.getPidKey())
                .append("= :parent").toString();
        Map<String, Object> paraMap = new HashedMap();
        paraMap.put("parent", parent);
        List<Map<String, Object>> dataList = this.queryForList(sqlDefine.getDatasource(), sql, paraMap);
        return dataList.size() > 0;
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
