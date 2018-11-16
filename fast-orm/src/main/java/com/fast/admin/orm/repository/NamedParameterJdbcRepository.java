package com.fast.admin.orm.repository;

import com.fast.admin.orm.model.Pager;
import com.fast.admin.orm.utils.SQLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2018/6/5.
 */
@Repository
public class NamedParameterJdbcRepository {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public <T> Pager<T> findAll(String sql, Class<T> clazz, int pageNo, int pageSize) {
        return this.findAll(sql, clazz, new HashMap<>(), pageNo, pageSize);
    }

    public <T> Pager<T> findAll(String sql, Class<T> clazz, Map<String, Object> paraMap, int pageNo, int pageSize) {
        SQLHelper.ConditionModel countConditionModel = SQLHelper.getInstnce(sql, paraMap).count().getCondition();

        //获取总记录数
        Long totalCount = namedParameterJdbcTemplate.queryForObject(countConditionModel.getSqlCondition().toString(), countConditionModel.getParams(), Long.class);

        //分页查询
        SQLHelper sqlHelper = SQLHelper.getInstnce(sql, paraMap).limit(pageNo, pageSize);
        SQLHelper.ConditionModel conditionModel = sqlHelper.getCondition();
        BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(clazz);
        List<T> list = namedParameterJdbcTemplate.query(conditionModel.getSqlCondition().toString(), conditionModel.getParams(), rowMapper);

        //设置分页信息
        Pager<T> pager = new Pager<>();
        pager.setContent(list);
        pager.setPageNo(pageNo);
        pager.setPageSize(pageSize);
        pager.setTotalElements(totalCount);
        return pager;
    }

    public <T> List<T> findAll(String sql, Class<T> clazz, Map<String, Object> paraMap) {
        BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(clazz);
        return namedParameterJdbcTemplate.query(sql, paraMap, rowMapper);
    }

    public Long count(String sql, Map<String, Object> paraMap) {
        return namedParameterJdbcTemplate.queryForObject(sql, paraMap, Long.class);
    }
}
