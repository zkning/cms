package com.sophia.cms.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public final static String YYYY = "yyyy";
    public final static String MM = "MM";
    public final static String DD = "dd";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";
    public final static String YYYY_MM = "yyyy-MM";
    public final static String HH_MM_SS = "HH:mm:ss";
    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    public static String formatStr_yyyyMMddHHmmssS = "yyyy-MM-dd HH:mm:ss.S";
    public static String formatStr_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static String formatStr_yyyyMMddHHmmss1 = "yyyyMMddHHmmss";
    public static String formatStr_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
    public static String formatStr_yyyyMMddHH = "yyyy-MM-dd HH";
    public static String formatStr_yyyyMMdd = "yyyy-MM-dd";
    public static String[] formatStr = {formatStr_yyyyMMddHHmmss, formatStr_yyyyMMddHHmm, formatStr_yyyyMMddHH, formatStr_yyyyMMdd};

    /**
     * 各星座开始日期数组
     */
    private final static int[] COMSTELLATION_DAY_START_ARR = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    /**
     * 星座数组
     */
    private final static String[] COMSTELLATION_ARR = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};

    /**
     * 初始年份
     */
    private final static int INITIAL_YEAR = 1900;

    /**
     * 十二生肖数组
     */
    private final static String[] THE_CHINESE_ZODIAC = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊",
            "猴", "鸡", "狗", "猪"};


    /**
     * 日期格式化－将<code>Date</code>类型的日期格式化为<code>String</code>型
     *
     * @param date    待格式化的日期
     * @param pattern 时间样式
     * @return 一个被格式化了的<code>String</code>日期
     */
    public static String format(Date date, String pattern) {
        if (date == null)
            return "";
        else
            return getFormatter(pattern).format(date);
    }

    /**
     * 默认把日期格式化成yyyy-mm-dd格式
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        if (date == null)
            return "";
        else
            return getFormatter(YYYY_MM_DD).format(date);
    }

    /**
     * 把字符串日期默认转换为yyyy-mm-dd格式的Data对象
     *
     * @param strDate
     * @return
     */
    public static Date format(String strDate) {
        Date d = null;
        if (strDate == "")
            return null;
        else
            try {
                d = getFormatter(YYYY_MM_DD).parse(strDate);
            } catch (ParseException pex) {
                return null;
            }
        return d;
    }

    /**
     * 把字符串日期转换为f指定格式的Data对象
     *
     * @param strDate ,f
     * @return
     */
    public static Date format(String strDate, String f) {
        Date d = null;
        if (strDate == "")
            return null;
        else
            try {
                d = getFormatter(f).parse(strDate);
            } catch (ParseException pex) {
                return null;
            }
        return d;
    }

    /**
     * 日期解析－将<code>String</code>类型的日期解析为<code>Date</code>型
     *
     * @param strDate 待格式化的日期
     * @param pattern 日期样式
     * @return 一个被格式化了的<code>Date</code>日期
     * @throws ParseException 如果所给的字符串不能被解析成一个日期
     */
    public static Date parse(String strDate, String pattern) throws ParseException {
        try {
            return getFormatter(pattern).parse(strDate);
        } catch (ParseException pe) {
            throw new ParseException("Method parse in Class DateUtil err: parse strDate fail.", pe.getErrorOffset());
        }
    }

    /**
     * 获取当前日期
     *
     * @return 一个包含年月日的<code>Date</code>型日期
     */
    public static synchronized Date getCurrDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 获取当前日期
     *
     * @return 一个包含年月日的<code>String</code>型日期，但不包含时分秒。yyyy-mm-dd
     */
    public static String getCurrDateStr() {
        return format(getCurrDate(), YYYY_MM_DD);
    }

    /**
     * 获取当前时间
     *
     * @return 一个包含年月日时分秒的<code>String</code>型日期。hh:mm:ss
     */
    public static String getCurrTimeStr() {
        return format(getCurrDate(), HH_MM_SS);
    }

    /**
     * 获取当前完整时间,样式: yyyy－MM－dd hh:mm:ss
     *
     * @return 一个包含年月日时分秒的<code>String</code>型日期。yyyy-MM-dd hh:mm:ss
     */
    public static String getCurrDateTimeStr() {
        return format(getCurrDate(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取与当前时间相差某值的时间
     */
    public static String getAddDateTimeStr(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);
        return format(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取当前年分 样式：yyyy
     *
     * @return 当前年分
     */
    public static String getYear() {
        return format(getCurrDate(), YYYY);
    }

    /**
     * 获取当前月分 样式：MM
     *
     * @return 当前月分
     */
    public static String getMonth() {
        return format(getCurrDate(), MM);
    }

    /**
     * 获取当前日期号 样式：dd
     *
     * @return 当前日期号
     */
    public static String getDay() {
        return format(getCurrDate(), DD);
    }

    /**
     * 按给定日期样式判断给定字符串是否为合法日期数据
     *
     * @param strDate 要判断的日期
     * @param pattern 日期样式
     * @return true 如果是，否则返回false
     */
    public static boolean isDate(String strDate, String pattern) {
        try {
            parse(strDate, pattern);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 判断给定字符串是否为特定格式年份（格式：yyyy）数据
     *
     * @param strDate 要判断的日期
     * @return true 如果是，否则返回false
     */
    public static boolean isYYYY(String strDate) {
        try {
            parse(strDate, YYYY);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    public static boolean isYYYY_MM(String strDate) {
        try {
            parse(strDate, YYYY_MM);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 判断给定字符串是否为特定格式的年月日（格式：yyyy-MM-dd）数据
     *
     * @param strDate 要判断的日期
     * @return true 如果是，否则返回false
     */
    public static boolean isYYYY_MM_DD(String strDate) {
        try {
            parse(strDate, YYYY_MM_DD);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 判断给定字符串是否为特定格式年月日时分秒（格式：yyyy-MM-dd HH:mm:ss）数据
     *
     * @param strDate 要判断的日期
     * @return true 如果是，否则返回false
     */
    public static boolean isYYYY_MM_DD_HH_MM_SS(String strDate) {
        try {
            parse(strDate, YYYY_MM_DD_HH_MM_SS);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 判断给定字符串是否为特定格式时分秒（格式：HH:mm:ss）数据
     *
     * @param strDate 要判断的日期
     * @return true 如果是，否则返回false
     */
    public static boolean isHH_MM_SS(String strDate) {
        try {
            parse(strDate, HH_MM_SS);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 获取一个简单的日期格式化对象
     *
     * @return 一个简单的日期格式化对象
     */
    private static SimpleDateFormat getFormatter(String parttern) {
        return new SimpleDateFormat(parttern);
    }

    /**
     * 获取给定日前的后intevalDay天的日期
     *
     * @param refenceDate 给定日期（格式为：yyyy-MM-dd）
     * @param intevalDays 间隔天数
     * @return 计算后的日期
     */
    public static String getNextDate(String refenceDate, int intevalDays) {
        try {
            return getNextDate(parse(refenceDate, YYYY_MM_DD), intevalDays);
        } catch (Exception ee) {
            return "";
        }
    }

    public static String getDateString(Date date) {
        SimpleDateFormat myFormatter = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        String theDate = myFormatter.format(date);
        return theDate;

    }

    /**
     * 获取给定日前的后intevalDay天的日期
     *
     * @param refenceDate Date 给定日期
     * @param intevalDays int 间隔天数
     * @return String 计算后的日期
     */
    public static String getNextDate(Date refenceDate, int intevalDays) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(refenceDate);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + intevalDays);
            return format(calendar.getTime(), YYYY_MM_DD);
        } catch (Exception ee) {
            return "";
        }
    }

    public static long getIntevalDays(String startDate, String endDate) {
        try {
            return getIntevalDays(parse(startDate, YYYY_MM_DD), parse(endDate, YYYY_MM_DD));
        } catch (Exception ee) {
            return 0l;
        }
    }

    public static long getIntevalDays(Date startDate, Date endDate) {
        try {
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();

            startCalendar.setTime(startDate);
            endCalendar.setTime(endDate);
            long diff = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();

            return (diff / (1000 * 60 * 60 * 24));
        } catch (Exception ee) {
            return 0l;
        }
    }

    /**
     * 求当前日期和指定字符串日期的相差天数
     *
     * @param startDate
     * @return
     */
    public static long getTodayIntevalDays(String startDate) {
        try {
            // 当前时间
            Date currentDate = new Date();

            // 指定日期
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date theDate = myFormatter.parse(startDate);

            // 两个时间之间的天数
            long days = (currentDate.getTime() - theDate.getTime()) / (24 * 60 * 60 * 1000);

            return days;
        } catch (Exception ee) {
            return 0l;
        }
    }

    public static Date parseToDate(String dateTimeStr) {
        if (dateTimeStr == null)
            return null;
        Date d = null;
        int formatStrLength = formatStr.length;
        for (int i = 0; i < formatStrLength; i++) {
            d = parseToDate2(dateTimeStr, formatStr[i]);
            if (d != null) {
                break;
            }
        }
        return d;
    }

    private static Date parseToDate2(String dateTimeStr, String formatString) {
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat(formatString);
        try {
            d = sdf.parse(dateTimeStr);
        } catch (ParseException pe) {

        }
        return d;
    }

    public static String dateTimeToString(Date datetime) {
        // dateTime=dateTime.substring(0,4)+dateTime.substring(5,7)+dateTime.substring(8,10)+dateTime.substring(11,13)+dateTime.substring(14,16)+dat
        // return dateTime;

        java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
        calendar.setTime(datetime);
        String dateTime = calendar.get(Calendar.YEAR) + "" + (calendar.get(Calendar.MONTH) + 1 > 9 ? "" : "0") + (calendar.get(Calendar.MONTH) + 1) + ""
                + (calendar.get(Calendar.DATE) > 9 ? "" : "0") + calendar.get(Calendar.DATE) + "" + (calendar.get(Calendar.HOUR_OF_DAY) > 9 ? "" : "0")
                + calendar.get(Calendar.HOUR_OF_DAY) + "" + (calendar.get(Calendar.MINUTE) > 9 ? "" : "0") + calendar.get(Calendar.MINUTE) + ""
                + (calendar.get(Calendar.SECOND) > 9 ? "" : "0") + calendar.get(Calendar.SECOND);
        return dateTime;
    }

    /**
     * 由年、月份，获得当前月的最后一天
     *
     * @param year month 月份 01 02 11 12
     * @return
     * @throws ParseException
     */
    public static String getLastDayOfMonth(String year, String month) throws ParseException {
        String LastDay = "";
        Calendar cal = Calendar.getInstance();
        Date date_;
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-14");
        cal.setTime(date);
        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, value);
        date_ = cal.getTime();
        LastDay = new SimpleDateFormat("yyyy-MM-dd").format(date_);
        return LastDay;
    }

    /**
     * 根据生日计算出年龄
     *
     * @param birthday 生日
     * @return
     */
    public static int getAgeByBirth(Date birthday) {
        if (BeanUtils.isEmpty(birthday)) {
            return 0;
        }
        int age = 0;
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        //如果传入的时间，在当前时间的后面，返回0岁
        if (BeanUtils.isEmpty(birthday) || !birth.after(now)) {
            age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age -= 1;
            }
        }
        return age;
    }

    /**
     * 根据生日获取星座
     *
     * @param birthday 生日
     * @return
     */
    public static String getConstellationByBirth(Date birthday) {
        if (BeanUtils.isEmpty(birthday)) {
            return "";
        }
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        return birth.get(Calendar.DAY_OF_MONTH) < COMSTELLATION_DAY_START_ARR[birth.get(Calendar.MONTH)] ? COMSTELLATION_ARR[birth.get(Calendar.MONTH)]
                : COMSTELLATION_ARR[birth.get(Calendar.MONTH)];
    }

    /**
     * 根据生日获取生肖
     *
     * @param birthday 生日
     * @return
     */
    public static String getChineseZodiacByBirth(Date birthday) {
        if (BeanUtils.isEmpty(birthday)) {
            return "";
        }
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        if (birth.get(Calendar.YEAR) < INITIAL_YEAR) {
            return "";
        }
        return THE_CHINESE_ZODIAC[(birth.get(Calendar.YEAR) - INITIAL_YEAR) % THE_CHINESE_ZODIAC.length];
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 日期加上月数的时间
     *
     * @param date
     * @param month
     * @return
     */
    public static Date dateAddMonth(Date date, int month) {
        return add(date, Calendar.MONTH, month);
    }

    private static Date add(Date date, int type, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, value);
        return calendar.getTime();
    }

    /**
     * yyyy-MM-dd HH:mm:ss 指定日期开始时间
     *
     * @return
     */
    public static Date dateToStart(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            Date dateTime = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 当天开始时间
     *
     * @return
     */
    public static Date dateToStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * yyyy-MM-dd HH:mm:ss 指定日期结束时间
     *
     * @param date
     * @return
     */
    public static Date dateToEnd(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToStart(date));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 当天结束时间
     *
     * @return
     */
    public static Date dateToEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * debug
     */
    public static void main(String[] args) {
        try {
//			System.out.println("当前日期：" + DateUtil.getCurrDateStr());
//			System.out.println("日期格式化：" + DateUtil.format(new Date(), DateUtil.formatStr_yyyyMMddHHmmss1));
//			System.out.println("短日期：" + DateUtil.format(new Date()));
//			System.out.println("长日期：" + DateUtil.getCurrDateTimeStr());
//			System.out.println("日：" + DateUtil.getDay());
//			System.out.println("月：" + DateUtil.getMonth());
//			System.out.println("年：" + DateUtil.getYear());
//			System.out.println("月未最后一天：" + DateUtil.getLastDayOfMonth("2010", "08"));
//			System.out.println("相差几天：" + DateUtil.getIntevalDays("2010-08-01", "2010-08-21"));
//			System.out.println("当前日期后的几天：" + DateUtil.getNextDate("2010-08-01", -3));
//			System.out.println("与今天相差几天：" + DateUtil.getTodayIntevalDays("2010-08-01"));
//            System.out.println("日期开始时间:" + dateToStart("2018-05-26 19:45:14"));
//            System.out.println("日期结束时间:" + dateToEnd("2018-05-26 19:45:14"));
//            System.out.println("日期开始时间:" + dateToStart());
//            System.out.println("日期结束时间:" + dateToEnd());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}