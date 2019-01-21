package com.sophia.cms.framework.util;

import java.util.*;

/**
 * Created by root on 2015/10/22.
 */
public class SignUtil {

    /**
     * 创建签名
     * @param parameters
     * @param characterEncoding 编码
     * @return
     */
    public static String sign(SortedMap<String,String> parameters){
        String str = createLinkString(parameters);
        String sign =  MD5Utils.string2MD5(str).toUpperCase();
        return sign;
    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }


}
