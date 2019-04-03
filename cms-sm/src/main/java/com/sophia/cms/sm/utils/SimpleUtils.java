package com.sophia.cms.sm.utils;

import com.sophia.cms.sm.model.SchemaColumnModel;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zkning
 */
public class SimpleUtils {
    private static final Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");

    /**
     * 下划线转驼峰
     *
     * @param line
     * @param smallCamel 首字母是否小写
     * @return
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        if (StringUtils.isEmpty(line)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String buildQuerySql(String tableName, List<SchemaColumnModel> schemaColumnModels) {
        StringBuffer sqlBuffer = new StringBuffer("select ");
        schemaColumnModels.forEach((SchemaColumnModel schemaColumnModel) -> {
            sqlBuffer.append("t.").append(schemaColumnModel.getColumnName()).append(",");
        });
        sqlBuffer.deleteCharAt(sqlBuffer.lastIndexOf(","));
        sqlBuffer.append(" from ").append(tableName).append(" t ");
        return sqlBuffer.toString().toUpperCase();
    }

    public static Object[] linkedHashMapToValues(LinkedHashMap linkedHashMap) {
        return linkedHashMap.values().toArray();
    }
}
