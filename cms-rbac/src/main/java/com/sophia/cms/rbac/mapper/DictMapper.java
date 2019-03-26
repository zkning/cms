package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.rbac.domain.Dict;
import com.sophia.cms.rbac.model.DictFetchModel;
import com.sophia.cms.rbac.model.DictSearchModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DictMapper extends BaseMapper<Dict> {

    List<DictFetchModel> list(Page page, @Param("params") DictSearchModel dictSearchModel);

    @Select("select * from t_rbac_dict where pid = #{pid} order by sort desc")
    List<Dict> findByPidOrderBySortDesc(@Param("pid") Long pid);

    @Select("select * from t_rbac_dict where value = #{value}")
    Dict findByValue(@Param("value") String value);
}
