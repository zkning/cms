package com.fast.admin.sm.service.impl;


import com.fast.admin.framework.response.Response;
import com.fast.admin.framework.util.SpringContextUtil;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.orm.repository.NamedParameterJdbcRepository;
import com.fast.admin.orm.utils.SQLHelper;
import com.fast.admin.rbac.constants.CacheableConstants;
import com.fast.admin.sm.domain.DataSource;
import com.fast.admin.sm.model.DataSourceEditModel;
import com.fast.admin.sm.model.DataSourceFetchModel;
import com.fast.admin.sm.model.DataSourceSearchModel;
import com.fast.admin.sm.repository.DataSourceRepository;
import com.fast.admin.sm.service.DataSourceService;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    NamedParameterJdbcRepository namedParameterJdbcRepository;

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

        StringBuffer sqlbuffer = new StringBuffer(" select t.id as id, t.driver_class_name as driverClassName , t.url as url, " +
                "t.db_username as dbUsername , t.db_password as dbPassword , t.version as version , t.create_time as createTime, t.name as name, " +
                "remark as remark from t_sm_datasource t where 1=1 ");

        SQLHelper.ConditionModel conditionModel = SQLHelper.getInstnce(sqlbuffer, new HashMap<>())
                .like("t", "id", dataSourceSearchModel.getId())
                .like("t", "name", dataSourceSearchModel.getName())
                .like("t", "db_username", dataSourceSearchModel.getDbUsername())
                .like("t", "url", dataSourceSearchModel.getUrl())
                .getCondition();

        return namedParameterJdbcRepository.findAll(conditionModel.getSqlCondition().toString(), DataSourceFetchModel.class,
                conditionModel.getParams(),
                dataSourceSearchModel.getPageNo(),
                dataSourceSearchModel.getPageSize());
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
