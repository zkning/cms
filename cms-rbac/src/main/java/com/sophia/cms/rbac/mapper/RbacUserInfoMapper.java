package com.sophia.cms.rbac.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.rbac.model.RbacUserInfoFetchModel;
import com.sophia.cms.rbac.model.RbacUserInfoSearchModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RbacUserInfoMapper {

    List<RbacUserInfoFetchModel> list(Page page, @Param("params") RbacUserInfoSearchModel rbacUserInfoSearchModel);
}
