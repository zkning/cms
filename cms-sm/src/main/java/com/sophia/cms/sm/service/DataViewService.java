package com.sophia.cms.sm.service;

import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.sm.model.*;
import com.sophia.cms.sm.domain.DataView;

/**
 * 数据视图服务
 * Created by zkning on 2017/8/13.
 */
public interface DataViewService {

    Response edit(DataViewEditModel request, boolean isEdit);

    Response delete(Long id);

    /**
     * //TODO 缓存视图数据
     * 根据编号获取视图信息
     *
     * @param dataViewCode
     * @return
     */
    DataView findById(Long id);

    DataViewFetchModel fetch(Long id);

    Pager<DataViewModel> list(DataViewSearchModel dataViewSearchModel);

    Response toRes(ToResModel toResModel);
}
