package com.sophia.cms.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.sm.domain.DataView;
import com.sophia.cms.sm.model.DataViewModel;
import com.sophia.cms.sm.model.DataViewSearchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataViewMapper extends BaseMapper<DataView> {
    List<DataViewModel> list(Page page, @Param("params") DataViewSearchModel dataViewSearchModel);
}
