package com.fast.admin.sm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fast.admin.framework.exception.ServiceException;
import com.fast.admin.framework.response.Response;
import com.fast.admin.framework.util.FastJsonUtils;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.orm.repository.NamedParameterJdbcRepository;
import com.fast.admin.orm.utils.SQLHelper;
import com.fast.admin.rbac.model.ResourceEditModel;
import com.fast.admin.rbac.service.ResourcesService;
import com.fast.admin.sm.constant.DataViewConstant;
import com.fast.admin.sm.model.*;
import com.fast.admin.sm.repository.DataViewRepository;
import com.fast.admin.sm.domain.DataView;
import com.fast.admin.rbac.domain.Resources;
import com.fast.admin.sm.service.DataViewService;
import com.fast.admin.sm.service.SqlIdJdbcService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by zkning on 20da't17/8/13.
 */
@Service
public class DataViewServiceImpl implements DataViewService {
    /**
     * 查询视图菜单路由url
     */
    public static final String dataViewMenuPrefix = "/sm/dataviewlist/";
    /**
     * 查询视图列表功能
     */
    public static final String sqlId_listPrefix = "/sm/sqlId/list/";
    public static final String sqlId_deletePrefix = "/sm/sqlId/delete/";
    public static final String sqlId_modfityPrefix = "/sm/sqlId/modfity/";
    public static final String sqlId_createPrefix = "/sm/sqlId/create/";
    /**
     * 查询单个视图功能
     */
    public static final String sqlId_fetchPrefix = "/sm/sqlId/fetch/";
    /**
     * 查询视图tree功能
     */
    public static final String sqlId_getTreePrefix = "/sm/sqlId/getTree";


    @Autowired
    DataViewRepository dataViewRepository;
    @Autowired
    SqlIdJdbcService sqlIdJdbcService;
    @Autowired
    NamedParameterJdbcRepository namedParameterJdbcRepository;

    @Autowired
    ResourcesService resourcesService;

    @Override
    public Response edit(DataViewEditModel request, boolean isEdit) {
        DataView dataView = new DataView();

        // 修改
        if (isEdit) {
            dataView = dataViewRepository.findOne(request.getId());
        }
        dataView.setId(request.getId());
        dataView.setSqlId(request.getSqlId());
        dataView.setRemark(request.getRemark());
        dataView.setDataViewName(request.getDataViewName());
        dataView.setButtons(FastJsonUtils.toJSONString(request.getButtons()));
        dataView.setFields(FastJsonUtils.toJSONString(request.getFields()));
        dataView.setDataFilters(FastJsonUtils.toJSONString(request.getDataFilters()));
        dataView.setOptions(FastJsonUtils.toJSONString(request.getOptions()));
        dataView.setTreeOptions(FastJsonUtils.toJSONString(request.getTreeOptions()));
        dataView.setVersion(request.getVersion());
        dataViewRepository.save(dataView);
        return Response.SUCCESS();
    }

    @Override
    public Response delete(Long id) {
        dataViewRepository.delete(id);
        return Response.SUCCESS();
    }


    @Override
    public DataView findById(Long id) {
        return dataViewRepository.findOne(id);
    }

    @Override
    public DataViewFetchModel fetch(Long id) {
        DataView dataView = dataViewRepository.findOne(id);
        DataViewFetchModel dataViewFetchModel = new DataViewFetchModel();
        dataViewFetchModel.setId(dataView.getId());
        dataViewFetchModel.setSqlId(dataView.getSqlId());
        dataViewFetchModel.setRemark(dataView.getRemark());
        dataViewFetchModel.setDataViewName(dataView.getDataViewName());
        dataViewFetchModel.setButtons(JSONArray.parseArray(dataView.getButtons(), ButtonModel.class));
        dataViewFetchModel.setFields(JSONArray.parseArray(dataView.getFields(), FieldModel.class));
        dataViewFetchModel.setDataFilters(JSONArray.parseArray(dataView.getDataFilters(), DataFilterModel.class));
        dataViewFetchModel.setOptions(JSON.parseObject(dataView.getOptions(), OptionsModel.class));
        dataViewFetchModel.setTreeOptions(JSON.parseObject(dataView.getTreeOptions(), TreeOptionsModel.class));
        dataViewFetchModel.setVersion(dataView.getVersion());
        return dataViewFetchModel;
    }

    @Override
    public Pager<DataViewModel> list(DataViewSearchModel dataViewSearchModel) {
        StringBuffer sqlbuffer = new StringBuffer(" select t.id,t.sql_id as sqlId,t.data_view_name as dataViewName,t.remark,t.options, " +
                " t.fields,t.buttons,t.tree_options as treeOptions,t.data_filters as dataFilters,t.version,t.create_time as createTime,t.create_user as createUser,t.last_update_time,t.last_update_user " +
                " from t_sm_dataview t where 1=1 ");

        SQLHelper.ConditionModel conditionModel = SQLHelper.getInstnce(sqlbuffer, new HashMap<>())
                .like("t", "sql_id", dataViewSearchModel.getSqlId())
                .like("t", "data_view_name", dataViewSearchModel.getDataViewName())
                .desc("t", "create_time").getCondition();

        return namedParameterJdbcRepository.findAll(conditionModel.getSqlCondition().toString(), DataViewModel.class,
                conditionModel.getParams(),
                dataViewSearchModel.getPageNo(),
                dataViewSearchModel.getPageSize());
    }

    @Override
    @Transactional
    public Response toRes(ToResModel toResModel) {
        DataView dataView = dataViewRepository.findOne(toResModel.getDataViewId());
        if (null == dataView) {
            return Response.FAILURE("视图不存在！");
        }
        ResourceEditModel model = ResourceEditModel.builder()
                .link(dataViewMenuPrefix + toResModel.getDataViewId())
                        .code(dataView.getId().toString())
                        .pid(toResModel.getResPid())
                        .text(dataView.getDataViewName())
                        .resourceType(DataViewConstant.menu)
                        .build();
        Response<Resources> resp = resourcesService.edit(model);
        if (!resp.checkSuccess()) {
            return resp;
        }
        // 按钮
        List<ButtonModel> buttonModelList = JSONArray.parseArray(dataView.getButtons(), ButtonModel.class);
        if (CollectionUtils.isEmpty(buttonModelList)) {
            return Response.SUCCESS();
        }
        Response fucnResp;
        for (ButtonModel buttonModel : buttonModelList) {

            // 增，删，改
            fucnResp = resourcesService.edit(ResourceEditModel.builder()
                    .link(buttonModel.getUrl())
                    .code(buttonModel.getId())
                    .pid(resp.getResult().getId())
                    .text(buttonModel.getTitle())
                    .resourceType(DataViewConstant.func)
                    .build());
            if (!fucnResp.checkSuccess()) {
                throw new ServiceException("功能创建失败！");
            }
        }

        // 查询视图数据列表功能
        fucnResp = resourcesService.edit(ResourceEditModel.builder()
                .link(sqlId_listPrefix + dataView.getId())
                .code(UUID.randomUUID().toString()).pid(resp.getResult().getId()).text("查询视图列表").resourceType(DataViewConstant.func)
                .build());
        if (!fucnResp.checkSuccess()) {
            throw new ServiceException("视图查询列表功能创建失败！");
        }
        // 查询单个视图数据
        fucnResp = resourcesService.edit(ResourceEditModel.builder()
                .link(sqlId_fetchPrefix + dataView.getId())
                .code(UUID.randomUUID().toString()).pid(resp.getResult().getId()).text("查询单条数据").resourceType(DataViewConstant.serv)
                .build());
        if (!fucnResp.checkSuccess()) {
            throw new ServiceException("视图查询列表功能创建失败！");
        }

        // tree
        TreeOptionsModel treeOptionsModel = JSON.parseObject(dataView.getOptions(), TreeOptionsModel.class);
        if (null != treeOptionsModel && treeOptionsModel.isVisible()) {
            fucnResp = resourcesService.edit(ResourceEditModel.builder()
                    .link(sqlId_getTreePrefix)
                    .code(UUID.randomUUID().toString()).pid(resp.getResult().getId()).text("查询视图tree").resourceType(DataViewConstant.serv)
                    .build());
            if (!fucnResp.checkSuccess()) {
                throw new ServiceException("视图tree功能创建失败！");
            }
        }
        return Response.SUCCESS();
    }
}
