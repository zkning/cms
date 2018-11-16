package com.fast.admin.sm.service;

import com.fast.admin.framework.response.Response;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.sm.model.*;
import com.fast.admin.sm.domain.DataView;

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
