package com.sophia.cms.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.sm.domain.SqlDefine;
import com.sophia.cms.sm.model.SqlDefineFetchModel;
import com.sophia.cms.sm.model.SqlDefineSearchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SqlDefineMapper extends BaseMapper<SqlDefine> {
    List<SqlDefineFetchModel> list(Page page, @Param("params") SqlDefineSearchModel sqlDefineSearchModel);
}
