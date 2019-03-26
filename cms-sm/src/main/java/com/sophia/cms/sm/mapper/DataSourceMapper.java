package com.sophia.cms.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.sm.domain.DataSource;
import com.sophia.cms.sm.model.DataSourceFetchModel;
import com.sophia.cms.sm.model.DataSourceSearchModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DataSourceMapper extends BaseMapper<DataSource> {
    List<DataSourceFetchModel> list(Page page, @Param("params") DataSourceSearchModel dataSourceSearchModel);

    @Select("select * from t_sm_datasource")
    List<DataSource> findAll();
}
