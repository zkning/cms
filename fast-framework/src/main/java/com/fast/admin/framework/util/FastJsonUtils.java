package com.fast.admin.framework.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
 * @author zkning
 */
public class FastJsonUtils {

    /**
     * WriteNullListAsEmpty 将Collection类型字段的字段空值输出为[]
     * WriteNullStringAsEmpty 将字符串类型字段的空值输出为空字符串 ""
     * WriteNullNumberAsZero 将数值类型字段的空值输出为0
     * WriteNullBooleanAsFalse 将Boolean类型字段的空值输出为false
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
//                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.PrettyFormat);
    }
}
