package com.fast.admin.orm.utils;

import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.util.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ningzuokun
 */
@Slf4j
public class SQLHelper {
    private StringBuffer sqlBuffer = new StringBuffer();
    private StringBuffer orderByBuffer = new StringBuffer();
    private StringBuffer groupByBuffer = new StringBuffer();
    private StringBuffer limitBuffer = new StringBuffer();
    private boolean count;
    private Map<String, Object> params = new HashMap<>();
    private static final String ORDER_BY = " order by ";
    private static final String GROUP_BY = " group by ";
    private static final String DESC = " desc";

    public SQLHelper() {
    }

    public SQLHelper(StringBuffer sql, Map<String, Object> params) {
        this.sqlBuffer = new StringBuffer(sql);
        this.params = params;
    }

    public static SQLHelper getInstnce() {
        return new SQLHelper();
    }

    public static SQLHelper getInstnce(StringBuffer sql, Map<String, Object> params) {
        return new SQLHelper(sql, params);
    }

    public static SQLHelper getInstnce(String sql, Map<String, Object> params) {
        return new SQLHelper(new StringBuffer(sql), params);
    }

    public SQLHelper whereEqual(String alias, String field, Object in) {
        if (BeanUtils.isEmpty(in)) {
            return this;
        }
        sqlBuffer.append(" where ")
                .append(alias)
                .append(".")
                .append(field)
                .append("=:").append(field);
        this.params.put(field, in);
        return this;
    }

    public SQLHelper equal(String alias, String field, Object in) {
        if (BeanUtils.isEmpty(in)) {
            return this;
        }
        sqlBuffer.append(" and ")
                .append(alias)
                .append(".")
                .append(field)
                .append("=:").append(field);
        this.params.put(field, in);
        return this;
    }

    public SQLHelper like(String alias, String field, Object in) {
        if (BeanUtils.isEmpty(in)) {
            return this;
        }
        sqlBuffer.append(" and ")
                .append(alias)
                .append(".")
                .append(field)
                .append(" like :").append(field);
        this.params.put(field, "%" + in + "%");
        return this;
    }

    public SQLHelper greaterThan(String alias, String field, Object in) {
        return this.greaterThan(alias, field, in, null);
    }

    public SQLHelper greaterThan(String alias, String field, Object in, String paramName) {
        if (BeanUtils.isEmpty(in)) {
            return this;
        }
        sqlBuffer.append(" and ")
                .append(alias)
                .append(".")
                .append(field);
        this.setParams(field, in, paramName, ">");
        return this;
    }

    public SQLHelper greaterThanEqual(String alias, String field, Object in) {
        return this.greaterThanEqual(alias, field, in, null);
    }

    public SQLHelper greaterThanEqual(String alias, String field, Object in, String paramName) {
        if (BeanUtils.isEmpty(in)) {
            return this;
        }
        sqlBuffer.append(" and ")
                .append(alias)
                .append(".")
                .append(field);
        this.setParams(field, in, paramName, ">=");
        return this;
    }

    public SQLHelper lessThan(String alias, String field, Object in) {
        return this.lessThan(alias, field, in, null);
    }

    public SQLHelper lessThan(String alias, String field, Object in, String paramName) {
        if (BeanUtils.isEmpty(in)) {
            return this;
        }
        sqlBuffer.append(" and ")
                .append(alias)
                .append(".")
                .append(field);
        this.setParams(field, in, paramName, "<");
        return this;
    }

    public SQLHelper lessThanEqual(String alias, String field, Object in) {
        return this.lessThanEqual(alias, field, in, null);
    }

    public SQLHelper lessThanEqual(String alias, String field, Object in, String paramName) {
        if (BeanUtils.isEmpty(in)) {
            return this;
        }
        sqlBuffer.append(" and ")
                .append(alias)
                .append(".")
                .append(field);
        this.setParams(field, in, paramName, "<=");
        return this;
    }

    private void setParams(String field, Object in, String paramName, String expression) {
        if (StringUtils.isNotBlank(paramName)) {
            sqlBuffer.append(expression).append(" :").append(paramName);
            this.params.put(paramName, in);
        } else {
            sqlBuffer.append(expression).append(" :").append(field);
            this.params.put(field, in);
        }
    }

    public SQLHelper in(String alias, String field, List<?> in) {
        sqlBuffer.append(" and ");
        if (CollectionUtils.isEmpty(in)) {
            sqlBuffer.append(" 1 = 0 ");
            return this;
        }
        sqlBuffer.append(alias)
                .append(".")
                .append(field)
                .append(" in (:").append(field).append(")");
        this.params.put(field, in);
        return this;
    }

    public SQLHelper notIn(String alias, String field, List<?> in) {
        sqlBuffer.append(" and ");
        if (CollectionUtils.isEmpty(in)) {
            sqlBuffer.append(" 0 = 1 ");
            return this;
        }
        sqlBuffer.append(alias)
                .append(".")
                .append(field)
                .append(" not in (:").append(field).append(")");
        this.params.put(field, in);
        return this;
    }

    public SQLHelper asc(String alias, String field) {
        if (StringUtils.isNotBlank(orderByBuffer)) {
            orderByBuffer.append(" , ");
        }
        orderByBuffer.append(alias).append(".").append(field);
        return this;
    }

    public SQLHelper desc(String alias, String field) {
        if (StringUtils.isNotBlank(orderByBuffer)) {
            orderByBuffer.append(" , ");
        }
        orderByBuffer.append(alias).append(".").append(field).append(DESC);
        return this;
    }

    public SQLHelper groupBy(String alias, String field) {
        if (StringUtils.isNotBlank(groupByBuffer)) {
            groupByBuffer.append(" , ");
        }
        groupByBuffer.append(alias).append(".").append(field);
        return this;
    }

    public SQLHelper limit(int pageNo, int pageSize) {
        if (StringUtils.isNotBlank(limitBuffer)) {
            return this;
        }
        limitBuffer.append(" limit :_pageno,:_pagesize ");
        this.params.put("_pageno", ((pageNo - 1) * pageSize));
        this.params.put("_pagesize", pageSize);
        return this;
    }

    public SQLHelper count() {
        this.count = true;
        return this;
    }

    public ConditionModel getCondition() {
        if (StringUtils.isNotBlank(orderByBuffer)) {
            this.sqlBuffer.append(ORDER_BY).append(orderByBuffer);
        }
        if (StringUtils.isNotBlank(groupByBuffer)) {
            this.sqlBuffer.append(GROUP_BY).append(groupByBuffer);
        }
        if (StringUtils.isNotBlank(limitBuffer)) {
            this.sqlBuffer = new StringBuffer("select _t.* from (").append(sqlBuffer).append(") _t ")
                    .append(limitBuffer);
        }
        if (count) {
            this.sqlBuffer = new StringBuffer("select count(1) as cnt from (").append(this.sqlBuffer).append(") _t ");
        }
        return ConditionModel.builder().params(this.params).sqlCondition(this.sqlBuffer).build();
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionModel {
        private StringBuffer sqlCondition;
        private Map<String, Object> params;
    }

    public static void main(String[] args) {
        StringBuffer sqlBuffer = new StringBuffer("select t.* from t_xs_bids t join t_xs_status_flow f on t.bid_id = f.work_id ");
        sqlBuffer.append(" where f.state = :flowState and f.auditor_id = :auditorId ");
        Map<String, Object> params = new HashMap<>();
        params.put("flowState", "1");
        params.put("auditorId", "3333");
        ConditionModel conditionModel = SQLHelper.getInstnce(sqlBuffer, params)
                .equal("t", "username", "111")
                .like("f", "sex", "1")
                .in("c", "age", Lists.newArrayList(new String[]{"1", "2", "3"}))
                .asc("c", "name")
                .desc("t", "create_time")
                .groupBy("t", "user_name")
                .groupBy("c", "user_address")
                .getCondition();
        log.info("SQL条件:{}", conditionModel.getSqlCondition());
        log.info("SQL条件参数:{}", JSONObject.toJSONString(conditionModel.getParams()));
    }
}
