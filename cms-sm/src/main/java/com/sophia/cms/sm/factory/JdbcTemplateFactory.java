package com.sophia.cms.sm.factory;

import com.sophia.cms.sm.domain.DataSource;
import com.sophia.cms.sm.model.DataSourceCacheModel;
import com.sophia.cms.sm.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ningzuokun on 2018/2/27.
 */
@Component
public class JdbcTemplateFactory {

    private static final String placeholder = ">";

    @Autowired
    DataSourceService dataSourceService;

    // 缓存
    private Map<String, JdbcTemplate> jdbcTemplateCache = new ConcurrentHashMap();
    private Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateCache = new ConcurrentHashMap();
    private Map<String, DataSourceCacheModel> dataSourceCache = new ConcurrentHashMap<>();

    /**
     * 数据视图crud操作根据sqldefine配置的datasouce执行
     */
    public JdbcTemplate getJdbcTemplate(Long dataSourceId) {
        DataSourceCacheModel dataSourceCacheModel = getDataSource(dataSourceId);

        // 构建缓存id
        String cacheId = getCacheId(dataSourceId, dataSourceCacheModel.getVersion());
        if (jdbcTemplateCache.containsKey(cacheId)) {
            return jdbcTemplateCache.get(cacheId);
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourceCacheModel.getDriverManagerDataSource());
        jdbcTemplateCache.put(cacheId, jdbcTemplate);
        return jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(Long dataSourceId) {
        DataSourceCacheModel dataSourceCacheModel = getDataSource(dataSourceId);

        // 构建缓存id
        String cacheId = getCacheId(dataSourceId, dataSourceCacheModel.getVersion());
        if (namedParameterJdbcTemplateCache.containsKey(cacheId)) {
            return namedParameterJdbcTemplateCache.get(cacheId);
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(dataSourceCacheModel.getDriverManagerDataSource());
        namedParameterJdbcTemplateCache.put(cacheId, namedParameterJdbcTemplate);
        return namedParameterJdbcTemplate;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public DataSourceCacheModel getDataSource(Long dataSourceId) {

        // 根据编号获取数据源配置
        DataSource dataSourceConfig = dataSourceService.findById(dataSourceId);

        // 构建缓存id
        String cacheId = this.getCacheId(dataSourceId, dataSourceConfig.getVersion());
        if (dataSourceCache.containsKey(cacheId)) {
            return dataSourceCache.get(cacheId);
        }

        // 创建数据源
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
        dataSource.setUrl(dataSourceConfig.getUrl());
        dataSource.setUsername(dataSourceConfig.getDbUsername());
        dataSource.setPassword(dataSourceConfig.getDbPassword());

        // 缓存
        DataSourceCacheModel dataSourceCacheModel = DataSourceCacheModel.builder()
                .driverManagerDataSource(dataSource)
                .version(dataSourceConfig.getVersion()).build();
        dataSourceCache.put(cacheId, dataSourceCacheModel);
        return dataSourceCacheModel;
    }

    private String getCacheId(Long dataSourceId, Long version) {
        return dataSourceId + placeholder + version;
    }
}
