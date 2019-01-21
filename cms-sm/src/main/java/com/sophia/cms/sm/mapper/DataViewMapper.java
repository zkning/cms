package com.sophia.cms.sm.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.sm.model.DataViewModel;
import com.sophia.cms.sm.model.DataViewSearchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataViewMapper {
    List<DataViewModel> list(Page page, @Param("params") DataViewSearchModel dataViewSearchModel);
}
