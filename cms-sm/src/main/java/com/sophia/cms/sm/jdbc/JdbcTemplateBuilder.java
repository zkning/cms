package com.sophia.cms.sm.jdbc;

import com.sophia.cms.sm.domain.DataSource;
import com.sophia.cms.sm.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

/**
 * Created by ningzuokun on 2018/2/27.
 */
@Component
public class JdbcTemplateBuilder {

    @Autowired
    DataSourceService dataSourceService;

    public JdbcTemplate getJdbcTemplate(Long dataSourceId) {
        DriverManagerDataSource dataSource = getDataSource(dataSourceId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(Long dataSourceId) {
        DriverManagerDataSource dataSource = getDataSource(dataSourceId);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return namedParameterJdbcTemplate;
    }

    private DriverManagerDataSource getDataSource(Long dataSourceId) {

        // 根据编号获取数据源配置
        DataSource dataSourceConfig = dataSourceService.findById(dataSourceId);

        // 创建数据源
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
        dataSource.setUrl(dataSourceConfig.getUrl());
        dataSource.setUsername(dataSourceConfig.getDbUsername());
        dataSource.setPassword(dataSourceConfig.getDbPassword());
        return dataSource;
    }
}
