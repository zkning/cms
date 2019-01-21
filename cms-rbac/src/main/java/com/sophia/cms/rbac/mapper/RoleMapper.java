package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.rbac.model.DictFetchModel;
import com.sophia.cms.rbac.model.DictSearchModel;
import com.sophia.cms.rbac.model.RoleFetchModel;
import com.sophia.cms.rbac.model.RoleSearchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {

    List<RoleFetchModel> list(@Param("params") RoleSearchModel roleSearchModel);
}
