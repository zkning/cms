package com.sophia.cms.sm.service.impl;

import com.sophia.cms.sm.factory.JdbcTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
public class DataSourceCrudhandle {


    @Autowired
    JdbcTemplateFactory jdbcTemplateFactory;

    public <T> List<T> query(Long dataSourceId, String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        return this.getNamedParameterJdbcTemplate(dataSourceId)
                .query(sql, paramMap, rowMapper);
    }


    public <T> T queryForObject(Long datasourceId, String sql, Map<String, ?> paramMap, Class<T> requiredType) throws DataAccessException {
        return this.getNamedParameterJdbcTemplate(datasourceId)
                .queryForObject(sql, paramMap, requiredType);
    }

    public <T> T queryForObject(Long dataSourceId, String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        return this.getNamedParameterJdbcTemplate(dataSourceId).queryForObject(sql, paramMap, rowMapper);
    }


    public boolean update(Long dataSourceId, String sql, Map<String, Object> paramMap) {
        return this.getNamedParameterJdbcTemplate(dataSourceId).update(sql, paramMap) < 1;
    }

    public SqlRowSet getSqlRowSet(Long dataSourceId, String sql, Map<String, Object> paraMap) {
        return this.getNamedParameterJdbcTemplate(dataSourceId).queryForRowSet(sql, paraMap);
    }

    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(Long dataSourceId) {
        return jdbcTemplateFactory.getNamedParameterJdbcTemplate(dataSourceId);
    }

    protected JdbcTemplate getJdbcTemplate(Long dataSourceId) {
        return jdbcTemplateFactory.getJdbcTemplate(dataSourceId);
    }

    public List<Map<String, Object>> queryForList(Long datasourceId, String sql, Map<String, ?> paramMap) throws DataAccessException {
        return this.getNamedParameterJdbcTemplate(datasourceId).queryForList(sql, paramMap);
    }

    /**
     * 释放数据库连接
     **/
    protected void destroyConnection(Connection connection) {
        try {
            if (null != connection) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("数据库连接释放异常", e);
        }
    }

}
