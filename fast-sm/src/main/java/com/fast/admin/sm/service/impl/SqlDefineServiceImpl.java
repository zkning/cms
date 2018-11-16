package com.fast.admin.sm.service.impl;


import com.fast.admin.framework.response.Response;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.orm.repository.NamedParameterJdbcRepository;
import com.fast.admin.orm.utils.SQLHelper;
import com.fast.admin.rbac.model.SqlDefineSearchModel;
import com.fast.admin.sm.model.SqlDefineEditModel;
import com.fast.admin.sm.model.SqlDefineFetchModel;
import com.fast.admin.sm.repository.SqlDefineRepository;
import com.fast.admin.sm.domain.SqlDefine;
import com.fast.admin.sm.service.SqlDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by lenovo on 2017/8/30.
 */
@Service
public class SqlDefineServiceImpl implements SqlDefineService {

    @Autowired
    SqlDefineRepository sqlDefineRepository;
    @Autowired
    NamedParameterJdbcRepository namedParameterJdbcRepository;

    @Override
    public Response edit(SqlDefineEditModel sqlDefineEditModel) {
        SqlDefine sqlDefine = new SqlDefine();
        if (null != sqlDefineEditModel.getId()) {
            sqlDefine = sqlDefineRepository.findOne(sqlDefineEditModel.getId());
            if (null == sqlDefine) {
                return Response.FAILURE("记录不存在");
            }
        }
        sqlDefine.setState(sqlDefineEditModel.getState());
        sqlDefine.setSqlExtra(sqlDefineEditModel.getSqlExtra());
        sqlDefine.setRemark(sqlDefineEditModel.getRemark());
        sqlDefine.setDatasource(sqlDefineEditModel.getDatasource());
        sqlDefine.setIsCache(sqlDefineEditModel.getIsCache());
        sqlDefine.setVersion(sqlDefineEditModel.getVersion());
        sqlDefine.setSqlName(sqlDefineEditModel.getSqlName());
        sqlDefine.setTableName(sqlDefineEditModel.getTableName());
        sqlDefine.setPri(sqlDefineEditModel.getPri());
        sqlDefine.setSelectSql(sqlDefineEditModel.getSelectSql());
        sqlDefineRepository.save(sqlDefine);
        return Response.SUCCESS();
    }

    @Override
    public Response delete(Long id) {
        sqlDefineRepository.delete(id);
        return Response.SUCCESS();
    }

    @Override
    public Response<SqlDefineFetchModel> fetch(Long id) {
        SqlDefine entity = sqlDefineRepository.findOne(id);
        if (null == entity) {
            return Response.FAILURE("记录不存在");
        }
        SqlDefineFetchModel model = new SqlDefineFetchModel();
        model.setState(entity.getState());
        model.setSqlExtra(entity.getSqlExtra());
        model.setRemark(entity.getRemark());
        model.setDatasource(entity.getDatasource());
        model.setIsCache(entity.getIsCache());
        model.setVersion(entity.getVersion());
        model.setSqlName(entity.getSqlName());
        model.setTableName(entity.getTableName());
        model.setPri(entity.getPri());
        model.setSelectSql(entity.getSelectSql());
        model.setId(entity.getId());
        return Response.SUCCESS(model);
    }

    @Override
    public Pager<SqlDefineFetchModel> list(SqlDefineSearchModel sqlDefineSearchModel) {
        StringBuffer sqlbuffer = new StringBuffer(" select t.id as id, t.sql_name as sqlName , t.select_sql as selectSql, " +
                "t.sql_extra as sqlExtra , t.datasource as datasource , t.version as version , t.create_time as createTime, t.is_cache as isCache, " +
                "state as state, remark as remark,table_name as tableName,pri as pri from t_sm_sqldefine t where 1=1 ");

        SQLHelper.ConditionModel conditionModel = SQLHelper.getInstnce(sqlbuffer, new HashMap<>())
                .like("t", "id", sqlDefineSearchModel.getId())
                .like("t", "sql_name", sqlDefineSearchModel.getSqlName())
                .like("t", "table_name", sqlDefineSearchModel.getTableName())
                .getCondition();

        return namedParameterJdbcRepository.findAll(conditionModel.getSqlCondition().toString(), SqlDefineFetchModel.class,
                conditionModel.getParams(),
                sqlDefineSearchModel.getPageNo(),
                sqlDefineSearchModel.getPageSize());
    }
}
