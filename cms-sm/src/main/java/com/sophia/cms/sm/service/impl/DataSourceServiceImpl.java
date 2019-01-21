package com.sophia.cms.sm.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.framework.util.SpringContextUtil;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.rbac.constants.CacheableConstants;
import com.sophia.cms.sm.domain.DataSource;
import com.sophia.cms.sm.mapper.DataSourceMapper;
import com.sophia.cms.sm.model.DataSourceEditModel;
import com.sophia.cms.sm.model.DataSourceFetchModel;
import com.sophia.cms.sm.model.DataSourceSearchModel;
import com.sophia.cms.sm.repository.DataSourceRepository;
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
    DataSourceRepository dataSourceRepository;

    @Autowired
    DataSourceMapper dataSourceMapper;

    @Override
    public Response edit(DataSourceEditModel dataSourceEditModel) {
        DataSource dataSource = new ModelMapper().map(dataSourceEditModel, DataSource.class);
        dataSourceRepository.save(dataSource);
        if (null != dataSourceEditModel.getId()) {

            //删除缓存
            DataSourceService dataSourceService = SpringContextUtil.getBean(DataSourceServiceImpl.class);
            dataSourceService.deleteCacheId(dataSourceEditModel.getId());
        }
        return Response.SUCCESS();
    }

    @Override
    public Response delete(Long id) {
        dataSourceRepository.delete(id);

        //删除缓存
        DataSourceService dataSourceService = SpringContextUtil.getBean(DataSourceServiceImpl.class);
        dataSourceService.deleteCacheId(id);
        return Response.SUCCESS();
    }

    @Override
    public Response<DataSourceFetchModel> fetch(Long id) {
        DataSource entity = dataSourceRepository.findOne(id);
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
        List<DataSource> list = dataSourceRepository.findAll();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                result.add(new ModelMapper().map(item, DataSourceFetchModel.class));
            });
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = CacheableConstants.CacheableName, unless = "#result != null")
    public DataSource findById(Long id) {
        return dataSourceRepository.findOne(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheableConstants.CacheableName, key = "#id")
    public void deleteCacheId(Long id) {
    }
}
