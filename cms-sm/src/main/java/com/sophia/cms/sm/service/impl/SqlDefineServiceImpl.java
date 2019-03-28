package com.sophia.cms.sm.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.sm.domain.SqlDefine;
import com.sophia.cms.sm.mapper.SqlDefineMapper;
import com.sophia.cms.sm.model.SqlDefineEditModel;
import com.sophia.cms.sm.model.SqlDefineFetchModel;
import com.sophia.cms.sm.model.SqlDefineSearchModel;
import com.sophia.cms.sm.service.SqlDefineService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017/8/30.
 */
@Service
public class SqlDefineServiceImpl implements SqlDefineService {

    @Autowired
    SqlDefineMapper sqlDefineMapper;

    @Override
    public Response edit(SqlDefineEditModel sqlDefineEditModel) {
        SqlDefine sqlDefine = new SqlDefine();
        Boolean flag = null != sqlDefineEditModel.getId();
        if (flag) {
            sqlDefine = sqlDefineMapper.selectById(sqlDefineEditModel.getId());
            if (null == sqlDefine) {
                return Response.FAILURE("记录不存在");
            }
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(sqlDefineEditModel, sqlDefine);
        sqlDefine.setLastUpdateTime(new Date());
        if (flag) {
            sqlDefineMapper.updateById(sqlDefine);
        } else {
            sqlDefine.setCreateTime(new Date());
            sqlDefineMapper.insert(sqlDefine);
        }
        return Response.SUCCESS();
    }

    @Override
    public Response delete(Long id) {
        sqlDefineMapper.deleteById(id);
        return Response.SUCCESS();
    }

    @Override
    public Response<SqlDefineFetchModel> fetch(Long id) {
        SqlDefine entity = sqlDefineMapper.selectById(id);
        if (null == entity) {
            return Response.FAILURE("记录不存在");
        }
        SqlDefineFetchModel model = new SqlDefineFetchModel();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(entity, model);
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
