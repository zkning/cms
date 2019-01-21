package com.sophia.cms.sm.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.sm.domain.SqlDefine;
import com.sophia.cms.sm.mapper.SqlDefineMapper;
import com.sophia.cms.sm.model.SqlDefineEditModel;
import com.sophia.cms.sm.model.SqlDefineFetchModel;
import com.sophia.cms.sm.model.SqlDefineSearchModel;
import com.sophia.cms.sm.repository.SqlDefineRepository;
import com.sophia.cms.sm.service.SqlDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lenovo on 2017/8/30.
 */
@Service
public class SqlDefineServiceImpl implements SqlDefineService {

    @Autowired
    SqlDefineRepository sqlDefineRepository;
    @Autowired
    SqlDefineMapper sqlDefineMapper;

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
        sqlDefine.setManipulate(sqlDefineEditModel.getManipulate());
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
        model.setManipulate(entity.getManipulate());
        model.setSelectSql(entity.getSelectSql());
        model.setId(entity.getId());
        return Response.SUCCESS(model);
    }

    @Override
    public Pager<SqlDefineFetchModel> list(SqlDefineSearchModel sqlDefineSearchModel) {
        Page page = new Page(sqlDefineSearchModel.getPageNo(), sqlDefineSearchModel.getPageSize());
        List<SqlDefineFetchModel> list = sqlDefineMapper.list(page, sqlDefineSearchModel);
        Pager<SqlDefineFetchModel> pager = new Pager<>();
        pager.setPageNo(page.getCurrent());
        pager.setPageSize(page.getSize());
        pager.setTotalElements(page.getTotal());
        pager.setContent(list);
        return pager;
    }
}
