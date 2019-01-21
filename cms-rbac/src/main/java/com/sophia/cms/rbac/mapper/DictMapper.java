package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.rbac.model.DictFetchModel;
import com.sophia.cms.rbac.model.DictSearchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictMapper {

    List<DictFetchModel> list(Page page, @Param("params") DictSearchModel dictSearchModel);
}
