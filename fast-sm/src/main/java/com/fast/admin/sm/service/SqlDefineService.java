package com.fast.admin.sm.service;


import com.fast.admin.framework.response.Response;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.rbac.model.SqlDefineSearchModel;
import com.fast.admin.sm.model.SqlDefineEditModel;
import com.fast.admin.sm.model.SqlDefineFetchModel;

/**
 * Created by lenovo on 2017/8/30.
 */
public interface SqlDefineService {

    /**
     * 保存SQL
     *
     * @return
     */
    Response edit(SqlDefineEditModel sqlDefineEditModel);

    Pager<SqlDefineFetchModel> list(SqlDefineSearchModel sqlDefineSearchModel);

    Response delete(Long id);

    Response<SqlDefineFetchModel> fetch(Long id);
}
