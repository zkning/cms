package com.sophia.cms.sm.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.sm.model.SqlDefineSearchModel;
import com.sophia.cms.sm.model.SqlDefineFetchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SqlDefineMapper {
    List<SqlDefineFetchModel> list(Page page, @Param("params") SqlDefineSearchModel sqlDefineSearchModel);
}
