package com.sophia.cms.sm.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.framework.util.SpringContextUtil;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.rbac.constants.CacheableConst;
import com.sophia.cms.sm.domain.DataSource;
import com.sophia.cms.sm.mapper.DataSourceMapper;
import com.sophia.cms.sm.model.DataSourceEditModel;
import com.sophia.cms.sm.model.DataSourceFetchModel;
import com.sophia.cms.sm.model.DataSourceSearchModel;
import com.sophia.cms.sm.service.DataSourceService;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zkning
 * 数据源
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    DataSourceMapper dataSourceMapper;

    @Override
    public Response edit(DataSourceEditModel dataSourceEditModel) {
        DataSource dataSource = new ModelMapper().map(dataSourceEditModel, DataSource.class);
        if (null != dataSourceEditModel.getId()) {
            dataSourceMapper.updateById(dataSource);

            //删除缓存
            DataSourceService dataSourceService = SpringContextUtil.getBean(DataSourceServiceImpl.class);
            dataSourceService.deleteCacheId(dataSourceEditModel.getId());
        } else {
            dataSourceMapper.insert(dataSource);
        }
        return Response.SUCCESS();
    }

    @Override
    public Response delete(Long id) {
        dataSourceMapper.deleteById(id);

        //删除缓存
        DataSourceService dataSourceService = SpringContextUtil.getBean(DataSourceServiceImpl.class);
        dataSourceService.deleteCacheId(id);
        return Response.SUCCESS();
    }

    @Override
    public Response<DataSourceFetchModel> fetch(Long id) {
        DataSource entity = dataSourceMapper.selectById(id);
        if (null == entity) {
            return Response.FAILURE("记录不存在");
        }
        DataSourceFetchModel dataSourceFetchModel = new DataSourceFetchModel();
        new ModelMapper().map(entity, dataSourceFetchModel);
        return Response.SUCCESS(dataSourceFetchModel);
    }

    @Override
    public Pager<DataSourceFetchModel> list(DataSourceSearchModel dataSourceSearchModel) {
        Page page = new Page(dataSourceSearchModel.getPageNo(), dataSourceSearchModel.getPageSize());
        List<DataSourceFetchModel> list = dataSourceMapper.list(page, dataSourceSearchModel);
        Pager<DataSourceFetchModel> pager = new Pager<>();
        pager.setPageNo(page.getCurrent());
        pager.setPageSize(page.getSize());
        pager.setTotalElements(page.getTotal());
        pager.setContent(list);
        return pager;
    }

    @Override
    public List<DataSourceFetchModel> findAll() {
        List<DataSourceFetchModel> result = Lists.newArrayList();
        List<DataSource> list = dataSourceMapper.findAll();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                result.add(new ModelMapper().map(item, DataSourceFetchModel.class));
            });
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = CacheableConst.CacheableName, unless = "#result != null")
    public DataSource findById(Long id) {
        return dataSourceMapper.selectById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheableConst.CacheableName, key = "#id")
    public void deleteCacheId(Long id) {
    }
}
