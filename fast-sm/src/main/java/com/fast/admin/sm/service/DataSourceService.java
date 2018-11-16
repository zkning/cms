package com.fast.admin.sm.service;

import com.fast.admin.framework.response.Response;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.sm.domain.DataSource;
import com.fast.admin.sm.model.DataSourceFetchModel;
import com.fast.admin.sm.model.DataSourceSearchModel;
import com.fast.admin.sm.model.DataSourceEditModel;

import java.util.List;

public interface DataSourceService {

    Response edit(DataSourceEditModel dataSourceEditModel);

    Response delete(Long id);

    Response<DataSourceFetchModel> fetch(Long id);

    Pager<DataSourceFetchModel> list(DataSourceSearchModel dataSourceSearchModel);

    List<DataSourceFetchModel> findAll();

    DataSource findById(Long id);

    void deleteCacheId(Long id);
}
