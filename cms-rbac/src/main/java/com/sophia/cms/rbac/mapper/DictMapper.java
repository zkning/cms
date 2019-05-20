package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.rbac.domain.Dict;
import com.sophia.cms.rbac.model.DictFetchModel;
import com.sophia.cms.rbac.model.DictSearchModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    List<DictFetchModel> list(Page page, @Param("params") DictSearchModel dictSearchModel);

    List<Dict> findByPidOrderBySortDesc(@Param("pid") Long pid);

    Dict findByPid(@Param("pid") Long pid);

    Dict findByValue(@Param("value") String value);
}
