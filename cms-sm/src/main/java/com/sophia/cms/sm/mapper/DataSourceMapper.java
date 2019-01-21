package com.sophia.cms.sm.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.sm.model.DataSourceFetchModel;
import com.sophia.cms.sm.model.DataSourceSearchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataSourceMapper {
    List<DataSourceFetchModel> list(Page page, @Param("params") DataSourceSearchModel dataSourceSearchModel);
}
