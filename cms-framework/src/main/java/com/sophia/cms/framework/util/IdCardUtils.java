package com.sophia.cms.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * 身份证工具类
 *
 * @author zhangshengzhou
 */
@Slf4j
public class IdCardUtils {

    /**
     * 标识18位身份证号码
     */
    private static final int EIGHTEEN_ID_CARD = 18;
    /**
     * 标识15位身份证号码
     */
    private static final int FIFTEEN_ID_CARD = 15;
    /**
     * 大陆地区地域编码最大值
     */
    private static final int MAX_MAINLAND_AREACODE = 659004;
    /**
     * 大陆地区地域编码最小值
     */
    private static final int MIN_MAINLAND_AREACODE = 110000;
    /**
     * 香港地域编码值
     */
    private static final int HONGKONG_AREACODE = 810000;
    /**
     * 台湾地域编码值
     */
    private static final int TAIWAN_AREACODE = 710000;
    /**
     * 澳门地域编码值
     */
    private static final int MACAO_AREACODE = 820000;
    /**
     * 大月的最多天数
     */
    private static final int BIG_MONTH_MAX_DAY = 31;
    /**
     * 小月的最多天数
     */
    private static final int SMALL_MONTH_MAX_DAY = 30;
    /**
     * 闰年2月的最多天数
     */
    private static final int LEAP_YEAR_FEB_MAX_DAY = 29;
    /**
     * 非闰年2月的最多天数
     */
    private static final int NORMAL_YEAR_FEB_MAX_DAY = 28;

    private static final String YYYYMMDD = "yyyyMMdd";

    /**
     * 储存18位身份证校验码
     */
    private static final String[] SORTCODES = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    private static final Integer[] a = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 验证身份证主方法
     */
    public static boolean checkIdCard(String idCardInput) {
        if (StringUtils.isBlank(idCardInput)) {
            log.error("身份证号码为必填");
            return false;
        }

        if (idCardInput.length() != EIGHTEEN_ID_CARD && idCardInput.length() != FIFTEEN_ID_CARD) {
            log.error("身份证号码位数不符");
            return false;
        }

        if (idCardInput.length() == FIFTEEN_ID_CARD) {
            return checkIdCard15(idCardInput);
        }
        return checkIdCard18(idCardInput);
    }

    /**
     * 验证15位身份证号码
     */
    private static boolean checkIdCard15(String idCardInput) {
        return checkNumber(FIFTEEN_ID_CARD, idCardInput) && checkArea(idCardInput) && checkBirthDate(FIFTEEN_ID_CARD, idCardInput) && checkCheckCode(FIFTEEN_ID_CARD, idCardInput);
    }

    /**
     * 验证18位身份证号码
     */
    private static boolean checkIdCard18(String idCardInput) {
        return checkNumber(EIGHTEEN_ID_CARD, idCardInput) && checkArea(idCardInput) && checkBirthDate(EIGHTEEN_ID_CARD, idCardInput) && checkCheckCode(EIGHTEEN_ID_CARD, idCardInput);
    }

    /**
     * 验证身份证的地域编码是符合规则
     */
    private static boolean checkArea(String idCardInput) {
        String subStr = idCardInput.substring(0, 6);
        int areaCode = Integer.parseInt(subStr);

        if (areaCode != HONGKONG_AREACODE && areaCode != TAIWAN_AREACODE && areaCode != MACAO_AREACODE
                && (areaCode > MAX_MAINLAND_AREACODE || areaCode < MIN_MAINLAND_AREACODE)) {
            log.error("输入的身份证号码地域编码不符合大陆和港澳台规则");
            return false;
        }
        return true;
    }

    /**
     * 验证身份证号码数字字母组成是否符合规则
     */
    private static boolean checkNumber(int idCardType, String idCard) {

        if (idCard == null) {
            return false;
        }

        char[] chars = idCard.toCharArray();

        if (idCardType == FIFTEEN_ID_CARD) {
            for (char c : chars) {
                if (c > '9') {
                    log.error(idCardType + "位身份证号码中不能出现字母");
                    return false;
                }
            }
        } else {
            for (int i = 0; i < chars.length; i++) {

                if (i < chars.length - 1) {

                    if (chars[i] > '9') {
                        log.error(EIGHTEEN_ID_CARD + "位身份证号码中前" + (EIGHTEEN_ID_CARD - 1) + "不能出现字母");
                        return false;
                    }
                } else {
                    if (chars[i] > '9' && chars[i] != 'X') {
                        log.error(idCardType + "位身份证号码中最后一位只能是数字0~9或字母X");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 验证身份证号码出生日期是否符合规则
     */
    private static boolean checkBirthDate(int idCardType, String idCardInput) {
        return checkBirthYear(idCardType, idCardInput) && checkBirthMonth(idCardType, idCardInput) && checkBirthDay(idCardType, idCardInput);
    }

    /**
     * 验证身份证号码出生日期年份是否符合规则
     */
    private static boolean checkBirthYear(int idCardType, String idCardInput) {
        if (idCardType == FIFTEEN_ID_CARD) {
            int year = Integer.parseInt(idCardInput.substring(6, 8));

            if (year < 0 || year > 99) {
                log.error(idCardType + "位的身份证号码年份须在00~99内");
                return false;
            }
        } else {
            int year = Integer.parseInt(idCardInput.substring(6, 10));
            int yearNow = getYear();

            if (year < 1900 || year > yearNow) {
                log.error(idCardType + "位的身份证号码年份须在1900~" + yearNow + "内");
                return false;
            }
        }
        return true;
    }

    /**
     * 验证身份证号码出生日期月份是否符合规则
     */
    private static boolean checkBirthMonth(int idCardType, String idCardInput) {
        int month;
        if (idCardType == FIFTEEN_ID_CARD) {
            month = Integer.parseInt(idCardInput.substring(8, 10));
        } else {
            month = Integer.parseInt(idCardInput.substring(10, 12));
        }
        if(month < 1 || month > 12){
            log.error("身份证号码月份须在01~12内");
            return false;
        }
        return true;
    }

    /**
     * 验证身份证号码出生日期天数是否符合规则
     */
    private static boolean checkBirthDay(int idCardType, String idCardInput) {
        boolean isLeapYear = false;
        int year, month, day;

        if (idCardType == FIFTEEN_ID_CARD) {
            year = Integer.parseInt("19" + idCardInput.substring(6, 8));
            month = Integer.parseInt(idCardInput.substring(8, 10));
            day = Integer.parseInt(idCardInput.substring(10, 12));
        } else {
            year = Integer.parseInt(idCardInput.substring(6, 10));
            month = Integer.parseInt(idCardInput.substring(10, 12));
            day = Integer.parseInt(idCardInput.substring(12, 14));
        }

        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            isLeapYear = true;
        }

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day < 1 || day > BIG_MONTH_MAX_DAY) {
                    log.error("身份证号码大月日期须在1~31之间");
                    return false;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day < 1 || day > SMALL_MONTH_MAX_DAY) {
                    log.error("身份证号码小月日期须在1~30之间");
                    return false;
                }
                break;
            case 2:
                if (isLeapYear) {
                    if (day < 1 || day > LEAP_YEAR_FEB_MAX_DAY) {
                        log.error("身份证号码闰年2月日期须在1~29之间");
                        return false;
                    }
                } else {
                    if (day < 1 || day > NORMAL_YEAR_FEB_MAX_DAY) {
                        log.error("身份证号码非闰年2月日期年份须在1~28之间");
                        return false;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 验证身份证号码顺序码是否符合规则,男性为奇数数,女性为偶数
     */
    private static boolean checkSortCode(int idCardType, int sex, String idCardInput) {
        int sortCode;
        if (idCardType == FIFTEEN_ID_CARD) {
            sortCode = Integer.parseInt(idCardInput.substring(12, 15));
        } else {
            sortCode = Integer.parseInt(idCardInput.substring(14, 17));
        }
        sortCode %= 2;
        sex %= 2;
        return sortCode == sex;
    }

    /**
     * 验证18位身份证号码校验码是否符合规则
     */
    private static boolean checkCheckCode(int idCardType, String idCard) {
        if (idCardType == EIGHTEEN_ID_CARD) {
            int sigma = 0;

            for (int i = 0; i < 17; i++) {
                int ai = Integer.parseInt(idCard.substring(i, i + 1));
                int wi = a[i];
                sigma += ai * wi;
            }

            int number = sigma % 11;
            String checkNumber = SORTCODES[number];

            if (!checkNumber.equals(idCard.substring(17))) {
                log.error("身份中的校验码不正确");
                return false;
            }
        }
        return true;
    }

    /**
     * 返回当前年份
     */
    private static int getYear() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat(YYYYMMDD);
        String nowStr = format.format(now);
        return Integer.parseInt(nowStr.substring(0, 4));
    }

    /**
     * 根据身份编号获取年龄
     *
     * @param idCard 身份编号
     * @return 年龄
     */
    public static Optional<Integer> getAgeByIdCard(String idCard) {
        if (!checkIdCard(idCard)) {
            return Optional.empty();
        }
        String year;
        if (idCard.length() == FIFTEEN_ID_CARD) {
            year = idCard.substring(6, 8);
        } else {
            year = idCard.substring(6, 10);
        }
        Calendar cal = Calendar.getInstance();
        int iCurrYear = cal.get(Calendar.YEAR);
        Integer age = iCurrYear - Integer.valueOf(year);
        return Optional.of(age);
    }

    /**
     * 根据身份编号获取生日
     *
     * @param idCard 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static Optional<String> getBirthByIdCard(String idCard) {
        if (!checkIdCard(idCard)) {
            return Optional.empty();
        }
        if (idCard.length() == FIFTEEN_ID_CARD) {
            return Optional.of("19" + idCard.substring(6, 12));
        } else {
            return Optional.of(idCard.substring(6, 14));
        }
    }

    /**
     * 根据身份编号获取生日
     *
     * @param idCard 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static Optional<Date> getBirth(String idCard) {
        if (!checkIdCard(idCard)) {
            return Optional.empty();
        }
        Date d;
        String birth;
        if (idCard.length() == FIFTEEN_ID_CARD) {
            birth = "19" + idCard.substring(6, 12);
        } else {
            birth = idCard.substring(6, 14);
        }
        if (BeanUtils.isEmpty(birth)) {
            return Optional.empty();
        } else {
            try {
                d = new SimpleDateFormat(YYYYMMDD).parse(birth);
            } catch (ParseException pex) {
                log.error("日期解析格式错误");
                return Optional.empty();
            }
        }
        return Optional.ofNullable(d);
    }

    /**
     * 根据身份编号获取生日年
     *
     * @param idCard 身份编号
     * @return 生日(yyyy)
     */
    public static Optional<String> getYearByIdCard(String idCard) {

        if (!checkIdCard(idCard)) {
            return Optional.empty();
        }
        if (idCard.length() == FIFTEEN_ID_CARD) {
            return Optional.of("19" + idCard.substring(6, 8));
        } else {
            return Optional.of(idCard.substring(6, 10));
        }
    }

    /**
     * 根据身份编号获取生日月
     *
     * @param idCard 身份编号
     * @return 生日(MM)
     */
    public static Optional<String> getMonthByIdCard(String idCard) {
        if (!checkIdCard(idCard)) {
            return Optional.empty();
        }
        if (idCard.length() == FIFTEEN_ID_CARD) {
            return Optional.of(idCard.substring(8, 10));
        } else {
            return Optional.of(idCard.substring(10, 12));
        }
    }


    /**
     * 根据身份编号获取生日天
     *
     * @param idCard 身份编号
     * @return 生日(dd)
     */
    public static Optional<String> getDateByIdCard(String idCard) {
        if (!checkIdCard(idCard)) {
            return Optional.empty();
        }
        if (idCard.length() == FIFTEEN_ID_CARD) {
            return Optional.of(idCard.substring(10, 12));
        } else {
            return Optional.of(idCard.substring(12, 14));
        }
    }

    /**
     * 根据身份编号获取性别
     *
     * @param idCard 身份编号
     * @return 性别(1 男 2 女 3 未知)
     */
    public static Optional<String> getGenderByIdCard(String idCard) {
        if (!checkIdCard(idCard)) {
            return Optional.empty();
        }
        String sCardNum;
        if (FIFTEEN_ID_CARD == idCard.length()) {
            sCardNum = idCard.substring(15);
        } else {
            sCardNum = idCard.substring(16, 17);
        }
        return Optional.of(Integer.parseInt(sCardNum) % 2 != 0 ? "1" : "2");
    }

    public static void main(String[] args) {
        String idNumber = "441424199206206992";
        int age = IdCardUtils.getAgeByIdCard(idNumber).orElse(0);
        Date birth = IdCardUtils.getBirth(idNumber).orElse(null);
        String gender = IdCardUtils.getGenderByIdCard(idNumber).orElse(null);
        System.out.println("年龄：" + age + "岁，生日：" + birth + "，性别：" + gender);
    }
}
