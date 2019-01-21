package com.sophia.cms.sm.service;


import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.sm.model.SqlDefineSearchModel;
import com.sophia.cms.sm.model.SqlDefineEditModel;
import com.sophia.cms.sm.model.SqlDefineFetchModel;

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
