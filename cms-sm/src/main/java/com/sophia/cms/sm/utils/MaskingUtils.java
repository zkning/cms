package com.sophia.cms.sm.utils;

import com.sophia.cms.sm.constant.MaskingTypeEnum;
import org.apache.commons.lang3.StringUtils;

public class MaskingUtils {


    public static String masking(Integer maskingType, String val) {
        MaskingTypeEnum mt = MaskingTypeEnum.get(maskingType);
        switch (mt) {
            case EMAIL:
                return email(val);
            case BANK_CARD:
                return bankCard(val);
            case MOBILE_PHONE:
                return mobilePhone(val);
            case CHINESE_NAME:
                return chineseName(val);
            case ID_CARD:
                return idCardNum(val);
            default:
                return val;
        }
    }

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李*昇>
     */
    public static String chineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        return StringUtils.left(fullName, 1).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(fullName, 1), StringUtils.length(fullName), "*"), "*"));
    }

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：150422********5762>
     */
    public static String idCardNum(final String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }
        return StringUtils.left(id, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(id, 4), StringUtils.length(id), "*"), "******"));
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏<例子:138****1234>
     */
    public static String mobilePhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));

    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
     */
    public static String email(final String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        final int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*")
                    .concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号<例子:6222600**********1234>
     */
    public static String bankCard(final String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(
                StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"),
                "******"));
    }

}
