package com.sophia.cms.framework.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * @author zhangshengzhou
 */
public class PinyinUtils {

    /**
     * 获取汉字的大写拼音全拼
     * @param src
     * @return
     */
    public static String getPingYinUppercase(String src) {
        char[] charArray = src.toCharArray();
        String[] strArray;

        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
        hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        String pinYin = "";
        try {
            for (int i = 0; i < charArray.length; i++) {
                // 判断是否为汉字字符
                if (java.lang.Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    // 将汉字的几种全拼都存到strArray数组中
                    strArray = PinyinHelper.toHanyuPinyinStringArray(charArray[i], hanyuPinyinOutputFormat);
                    // 取出该汉字全拼的第一种读音并连接到字符串pinYin后
                    pinYin += strArray[0];
                } else {
                    // 如果不是汉字字符，直接取出字符并连接到字符串pinYin后
                    pinYin += java.lang.Character.toString(charArray[i]);
                }
            }
            return pinYin;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return pinYin;
    }
}
