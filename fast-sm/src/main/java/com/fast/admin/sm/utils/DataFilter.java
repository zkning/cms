package com.fast.admin.sm.utils;

import com.fast.admin.sm.constant.SqlExpression;
import com.fast.admin.sm.model.ConditionModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SQL过滤
 *
 * @author zkning
 */
public class DataFilter {
    private static String WHERE = " WHERE ";
    private String querySql = null;
    private StringBuffer condition = new StringBuffer();
    private StringBuffer orderBy = new StringBuffer();
    private Map<String, Object> params = new HashMap<>();
    private Integer PAGENO = 1;
    private Integer PAGESIZE = 50;

    //兼容bootstraptable排序
    private String sortOrder;
    private String sortName;

    public DataFilter(List<ConditionModel> conditionVoList) {
        addCondition(conditionVoList);
    }

    public DataFilter() {
    }

    public static DataFilter getInstance() {
        return new DataFilter();
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public void addCondition(List<ConditionModel> conditionModelList) {
        if (conditionModelList == null) {
            return;
        }

        for (ConditionModel cond : conditionModelList) {

            //排序字段
            addCondition(cond.getField(), cond.getExpression(), cond.getValue(), cond.getOrder());
        }
    }

    public void addCondition(ConditionModel cond) {
        addCondition(cond.getField(), cond.getExpression(), cond.getValue(), cond.getOrder());
    }

    public void EQ(String alias, String value) {
        addCondition(alias, SqlExpression.EQ, value, null);
    }

    public void addCondition(String alias, String expression, Object value, String sort) {
        String uuid = UUID.randomUUID().toString();
        String paramStr = alias + uuid.substring(0, uuid.indexOf("-"));

        // 排序
        addSort(alias, sort);

        // 构造条件sql
        switch (expression) {
            case SqlExpression.IN:
            case SqlExpression.NOT_IN:
                List idIn = (List) value;
                if (CollectionUtils.isNotEmpty(idIn)) {
                    getCondition().append(" t.").append(alias)
                            .append(" " + expression + " (").append(":").append(paramStr).append(") ");
                    params.put(paramStr, idIn);
                } else {
                    getCondition().append(" 1 = 2 ");
                }
                return;
            case SqlExpression.NULL:
            case SqlExpression.NOT_NULL:
                getCondition().append(" t.").append(alias).append(expression);
                return;
            default:
                if (null == value || StringUtils.isBlank(value.toString())) {
                    return;
                }
                getCondition().append(" t.").append(alias).append(" " + expression + " ")
                        .append(":").append(paramStr);
                if (SqlExpression.LIKE.equalsIgnoreCase(expression)) {
                    params.put(paramStr, value + "%");
                } else {
                    params.put(paramStr, value);
                }
        }
    }

    private StringBuffer getCondition() {
        if (StringUtils.isNotBlank(condition)) {
            condition.append(" AND ");
        }
        return condition;
    }

    /**
     * 增加排序
     *
     * @param alias
     * @param sort
     */
    public void addSort(String alias, String sort) {
        if (StringUtils.isBlank(sort) || StringUtils.isBlank(alias)) {
            return;
        }
        if (StringUtils.isBlank(orderBy)) {
            orderBy.append(" ORDER BY ");
        } else {
            orderBy.append(" ,");
        }
        orderBy.append(alias).append(" ").append(sort);
    }

    /**
     * count sql
     *
     * @return
     */
    public String countSql() {
        StringBuffer storeSql = new StringBuffer();
        storeSql.append("select count(1) from (").append(this.querySql).append(") t ");
        if (StringUtils.isNotBlank(condition)) {
            storeSql.append(WHERE).append(condition);
        }
        return storeSql.toString();
    }

    /**
     * SQL
     *
     * @return
     */
    public String getSql() {
        StringBuffer storeSql = new StringBuffer();
        storeSql.append("select t.* from (").append(querySql).append(") t ");
        if (StringUtils.isNotBlank(condition)) {
            storeSql.append(WHERE).append(condition);
        }
        this.addSort(this.sortName, this.sortOrder);
        return storeSql.append(orderBy).toString();
    }

    /**
     * 分页SQL
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public String createPager(Integer pageNo, Integer pageSize) {
        if (null == pageNo) {
            pageNo = PAGENO;
        }
        if (null == pageSize) {
            pageSize = PAGESIZE;
        }
        StringBuffer storeSql = new StringBuffer();
        storeSql.append("select t.* from (").append(querySql).append(") t ");
        if (StringUtils.isNotBlank(condition)) {
            storeSql.append(WHERE).append(condition);
        }
        this.addSort(this.sortName, this.sortOrder);
        storeSql.append(orderBy);
        if (null != pageNo && null != pageSize) {
            storeSql.append(" LIMIT ")
                    .append((pageNo - 1) * pageSize)
                    .append(",")
                    .append(pageSize);
        }
        return storeSql.toString();
    }

    /**
     * condition sql
     *
     * @return
     */
    public String conditionSql() {
        return condition.append(orderBy).toString();
    }

    /**
     * 获取参数
     *
     * @return
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * 添加参数
     *
     * @param alis
     * @param value
     */
    public void put(String alis, Object value) {
        params.put(alis, value);
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

}
