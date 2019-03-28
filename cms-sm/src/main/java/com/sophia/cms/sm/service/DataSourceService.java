package com.sophia.cms.sm.service;

import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.sm.domain.DataSource;
import com.sophia.cms.sm.model.DataSourceFetchModel;
import com.sophia.cms.sm.model.DataSourceSearchModel;
import com.sophia.cms.sm.model.DataSourceEditModel;
import org.springframework.cache.annotation.Cacheable;

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
