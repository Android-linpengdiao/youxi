package com.quakoo.im.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DateUtil {

    /**
     * 功能描述: 时间自定义格式化输出
     *
     * @param date   java.util.Date 类型时间
     * @param format String类型 - 输入格式 如：yyyy-MM-dd / yyyy-MM-dd HH:mm:ss
     * @return 返回String 类型时间
     * @author Y(^ _ ^)Y  2014-8-21 下午10:07:40
     */
    public static String doFormatDate(Date date, String format) {
        return (new SimpleDateFormat(format)).format(date);
    }

    /**
     * 功能描述: String类型时间自定义格式化输出
     *
     * @param date   java.util.Date 类型时间
     * @param format String类型 - 输入格式 如：yyyy-MM-dd / yyyy-MM-dd HH:mm:ss
     * @return 返回 java.util.Date 类型时间
     * @author Y(^ _ ^)Y  2014-8-21 下午10:07:40
     */
    public static Date doFormatDate(String date, String format) {
        try {
            return (new SimpleDateFormat(format)).parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 功能描述:将时间转化为yyyy年MM月dd日E的字符串时间
     *
     * @param date   日期
     * @param format 日期格式 yyyy/yyy-MM/yyyy-mm-dd等
     * @return 如：2014年08月23日星期六
     * @author Y(^ _ ^)Y  2014-8-23 下午01:20:24
     */
    public static String date2YMDE(Date date, String format) {
        if (date == null) return "";
        try {
            return doFormatDate(date, format + " E");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 功能描述: 获得当前自定义格式化时间
     *
     * @param format String类型 - 输入格式 如：yyyy-MM-dd / yyyy-MM-dd HH:mm:ss ...
     * @return 返回String 类型时间
     * @author Y(^ _ ^)Y  2014-8-21 下午10:22:46
     */
    public static String getCurrentTime(String format) {
        return doFormatDate(new Date(), format);
    }


    /**
     * 功能描述:获得当前年份
     *
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 上午10:02:44
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 功能描述:获得当前月份
     *
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 上午10:02:44
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 功能描述: 时间 天数 加/减
     *
     * @param date java.util.Date 类型时间
     * @param day  需要加/减 的天数  ， day为负数：减去day天，day为整数：加上day天
     * @return java.util.Date 类型时间
     * @author Y(^ _ ^)Y  2014-8-21 下午10:25:58
     */
    public static Date getSubtractionDay(Date date, Integer day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();  //这个时间就是日期往后推一天的结果
        return date;
    }

/**
 * 功能描述: 时间 天数 加/减
 *
 * @author Y(^ _ ^)Y  2014-8-21 下午10:25:58
 *
 * @param date 字符串时间
 *
 * @param day 需要加/减 的天数  ， day为负数：减去day天，day为整数：加上day天
 *
 * @return java.util.Date 类型时间(如果异常返回当前java.util.Date时间)
 */
/*public static Date getSubtractionDay(String date,Integer day,String format){
try {
Date date1=new SimpleDateFormat(format).parse(date);
Calendar  calendar  =  new  GregorianCalendar();
  calendar.setTime(date1);
  calendar.add(calendar.DATE,day);//把日期往后增加一天.整数往后推,负数往前移动
  Date newDate=calendar.getTime();  //这个时间就是日期往后推一天的结果
  return newDate;
} catch (Exception e) {
e.printStackTrace();
return new Date();
}

}*/

    /**
     * 功能描述: 时间 天数 加/减
     *
     * @param date   java.util.Date 类型时间
     * @param day    需要加/减 的天数  ， day为负数：减去day天，day为整数：加上day天
     * @param format 时间格式
     * @return String 类型时间
     * @author Y(^ _ ^)Y  2014-8-21 下午10:25:58
     */
    public static String getSubtractionStringDay(Date date, Integer day, String format) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();  //这个时间就是日期往后推一天的结果
        return doFormatDate(date, format);
    }


    /**
     * 功能描述: 获取1970年至今的秒数（当前系统时间）
     *
     * @return Long类型秒数
     * @author Y(^ _ ^)Y  2014-8-21 下午10:34:55
     */
    public static Long getCurrentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 功能描述:1970年至今的秒数转换为java.util.Date日期
     *
     * @param currentSeconds 秒数-Long 类型
     * @return 返回java.util.Date类型时间
     * @author Y(^ _ ^)Y  2014-7-15 下午07:58:51
     */
    public static Date sceondsCastDate(Long currentSeconds) {
//前面的currentSeconds是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
        Date date = new Date(currentSeconds * 1000);
        return date;
    }

    /**
     * 功能描述:1970年至今的秒数转换为java.util.Date日期
     *
     * @param currentSeconds 秒数-String 类型
     * @return 返回java.util.Date类型时间
     * @author Y(^ _ ^)Y  2014-7-15 下午07:58:51
     */
    public static Date sceondsCastDate(String currentSeconds) {
//前面的currentSeconds是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
        Date date = new Date(Long.parseLong(currentSeconds) * 1000);
        return date;
    }


    /**
     * 功能描述:1970年至今的秒数转换为 String 类型日期
     *
     * @param currentSeconds 秒数-Long 类型
     * @param dateFmat       自定义时间格式
     * @return 返回String类型时间
     * @author Y(^ _ ^)Y  2014-7-15 下午07:58:51
     */
    public static String sceondsCastStringDate(Long currentSeconds, String dateFmat) {
        long msgCreateTime = currentSeconds * 1000L;
        DateFormat format = new SimpleDateFormat(dateFmat);
        return format.format(new Date(msgCreateTime));
    }

    /**
     * 功能描述:1970年至今的秒数转换为 String 类型日期
     *
     * @param currentSeconds 秒数-String 类型
     * @param dateFmat       自定义时间格式
     * @return 返回String类型时间
     * @author Y(^ _ ^)Y  2014-7-15 下午07:58:51
     */
    public static String sceondsCastStringDate(String currentSeconds, String dateFmat) {
        long msgCreateTime = Long.parseLong(currentSeconds) * 1000L;
        DateFormat format = new SimpleDateFormat(dateFmat);
        return format.format(new Date(msgCreateTime));
    }

    /**
     * 功能描述:获得当前时间的小时和分钟
     *
     * @return HH：mm
     * @author Y(^ _ ^)Y  2014-8-22 下午09:12:58
     */
    public static String getCurrentHoursMin() {
        return doFormatDate(new Date(), "HH:mm");
    }

    /**
     * 功能描述:获得今天日期
     *
     * @param pattern 自定义格式
     * @return String 日期
     * @author Y(^ _ ^)Y  2014-8-22 下午09:17:33
     */
    public static String getToday(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    /**
     * 功能描述:获得昨天的日期
     *
     * @param pattern 日期格式
     * @return String 日期
     * @author Y(^ _ ^)Y  2014-8-22 下午09:20:11
     */
    public static String getYesterday(String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(cal.getTime());
    }


    /**
     * 功能描述:获得昨天的日期-默认格式为yyyy-MM-dd
     *
     * @param pattern 日期格式
     * @return String 日期
     * @author Y(^ _ ^)Y  2014-8-22 下午09:20:11
     */
    public static String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(cal.getTime());
    }

    /**
     * 功能描述:获得昨天的日期-默认格式为yyyy-MM-dd
     *
     * @param pattern 日期格式
     * @return java.util.Date 日期
     * @author Y(^ _ ^)Y  2014-8-22 下午09:20:11
     */
    public static Date getYesterdayOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 功能描述:获得明天的日期
     *
     * @param pattern 日期格式
     * @return String 日期
     * @author Y(^ _ ^)Y  2014-8-22 下午09:20:11
     */
    public static String getTomorrow(String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(cal.getTime());
    }

    /**
     * 得到指定日期的上一天的日期
     *
     * @param curday
     */
    public static String getPreDayStr(String curday) {
        Date date = doFormatDate(curday, "yyyy-MM-dd");
        return doFormatDate(new Date(date.getTime() - 24 * 3600 * 1000), "yyyy-MM-dd");


    }


    /**
     * 得到指定日期的下一天的日期
     *
     * @param curday
     */
    public static String getNextDayStr(String curday) {
        Date date = doFormatDate(curday, "yyyy-MM-dd");
        return doFormatDate(new Date(date.getTime() + 24 * 3600 * 1000), "yyyy-MM-dd");
    }

    /**
     * 功能描述:根据一个日期返回是星期几
     *
     * @param date 日期 String
     * @return 星期几 如：星期一
     * @author Y(^ _ ^)Y  2014-8-23 上午11:35:12
     */
    public static String getWeekStr(String date) {
// 再转换为时间
        Date date1 = doFormatDate(date, "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
// int hour=c.get(Calendar.DAY_OF_WEEK);
// hour中存的就是星期几了，其范围 1~7
// 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    /**
     * 功能描述:得到一年中共有多少周
     *
     * @param year 年份 ---2013
     * @return int
     * @author Y(^ _ ^)Y 2014-1-25 下午04:30:37
     */

    public static int getCountWeeksOfYear(int year) {
        int week = 0;
        int days = 365;
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {// 判断是否闰年，闰年366天
            days = 366;
        }
// 得到一年所有天数然后除以7
        int day = days % 7;// 得到余下几天
        week = days / 7;// 得到多少周
        return week;
    }

    /**
     * 功能描述: 获得下一周的第一天（指星期一）的日期 ---如2014-01-27
     *
     * @param date 当前日期
     * @return String 日期
     * @author Y(^ _ ^)Y 2014-1-25 下午03:56:05
     */
    public static String getNextWeekFirstDay(Date date) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(getNextWeekFirstDate(date));

    }

    /**
     * 功能描述: 获得下一周的第一天（指星期一）的日期 ---如2014-01-27
     *
     * @param date 当前日期
     * @return date日期
     * @author Y(^ _ ^)Y 2014-1-25 下午03:56:05
     */
    public static Date getNextWeekFirstDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(date);
        int dayOfWeek = 0;


        for (int i = 1; i <= 7; i++) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 2)
                return c.getTime();


        }
        return date;


    }

    /**
     * 功能描述:获得下一周的最后一天（指星期天）的日期
     *
     * @param date java.util.Date日期
     * @return java.util.Date 日期
     * @author Y(^ _ ^)Y 2014-1-25 下午03:57:48
     */
    public static Date getNextWeekLastDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(getNextWeekFirstDate(date));
        int dayOfWeek = 0;
        for (int i = 1; i <= 7; i++) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1)
                return c.getTime();

        }
        return date;
    }

    /**
     * 功能描述:获得下一周的最后一天（指星期天）的日期
     *
     * @param date java.util.Date日期
     * @return string 日期
     * @author Y(^ _ ^)Y 2014-1-25 下午03:57:48
     */
    public static String getNextWeekLastDay(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(getNextWeekLastDate(date));

    }

    /**
     * 功能描述:获得本周第一天日期(周一)
     *
     * @param date 日期
     * @return date日期
     * @author Y(^ _ ^)Y  2014-8-23 下午02:34:06
     */
    public static Date getWeekFirstDay(Date date) {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(date);
        int dayOfWeek = 0;


        for (int i = 1; i <= 7; i++) {
            c.add(Calendar.DAY_OF_MONTH, -1);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 2)
                return c.getTime();


        }
        return date;
    }

    /**
     * 功能描述:获得本周第一天日期(周一)
     *
     * @param date 日期
     * @return string日期
     * @author Y(^ _ ^)Y  2014-8-23 下午02:34:06
     */
    public static String getWeekFirstDayStr(Date date) {
        return doFormatDate(getWeekFirstDay(date), "yyyy-MM-dd");
    }

    /**
     * 功能描述:获得本周最后一天日期(周日)
     *
     * @param date 日期
     * @return date日期
     * @author Y(^ _ ^)Y  2014-8-23 下午02:34:06
     */
    public static Date getWeekLastDay(Date date) {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(getWeekFirstDay(date));
        int dayOfWeek = 0;
        for (int i = 1; i <= 7; i++) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1)
                return c.getTime();

        }
        return date;
    }

    /**
     * 功能描述:获得本周最后一天日期(周日)
     *
     * @param date 日期
     * @return string日期
     * @author Y(^ _ ^)Y  2014-8-23 下午02:34:06
     */
    public static String getWeekLastDayStr(Date date) {
        return doFormatDate(getWeekLastDay(date), "yyyy-MM-dd");
    }


    /**
     * 功能描述:获得距离今天半年后的日期
     *
     * @return date日期
     * @author Y(^ _ ^)Y  2014-8-23 下午02:31:01
     */
    public static Date getHalfYear() {
        Calendar.getInstance().add(Calendar.DAY_OF_YEAR, 183);
        return Calendar.getInstance().getTime();
    }

    /**
     * 功能描述:获得距离今天半年后的日期
     *
     * @return String日期
     * @author Y(^ _ ^)Y  2014-8-23 下午02:31:01
     */
    public static String getHalfYearStr() {
        return doFormatDate(getHalfYear(), "yyyy-MM-dd");
    }


    /**
     * 功能描述:获得距离指定日期的上/下月的日期
     *
     * @param offset       偏移量 offset = -1 取得上一个月 offset = 0 取得本月 offset = 1 取得下一个月
     * @param pattern      返回日期格式
     * @param currentMonth 指定日期 如：yyyy-MM-dd
     * @return String 日期
     * @author Y(^ _ ^)Y  2014-8-22 下午09:32:13
     */
    public static String getMonthDay(int offset, String pattern, String currentMonth) {
        String yearMonth = "197101";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
// SimpleDateFormat formatMM = new SimpleDateFormat("MM");
        try {
            Date startDate = format.parse(currentMonth);
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTime(startDate);
            Calendar tomorrowCal = Calendar.getInstance();
            tomorrowCal.setTime(startDate);
            tomorrowCal.add(Calendar.DAY_OF_MONTH, 1);
            if (tomorrowCal.get(Calendar.MONTH) != nowCal.get(Calendar.MONTH)) {
                tomorrowCal.add(Calendar.MONTH, offset);
                tomorrowCal.add(Calendar.DAY_OF_MONTH, -1);
                yearMonth = format.format(tomorrowCal.getTime());
            } else {
                nowCal.add(Calendar.MONTH, offset);
                yearMonth = format.format(nowCal.getTime());
            }
        } catch (ParseException e) {
            System.out.println("d");
        }
        return yearMonth;
    }


    /**
     * 功能描述:获得指定日期在一周中是第几天
     *
     * @param date 日期
     * @return int 1-7
     * @author Y(^ _ ^)Y  2014-8-22 下午09:38:21
     */
    public static int getDayOfWeek(Date date) {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.setTime(date);
        int x = aCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (x == 0)
            x = 7;
        return x;
    }

    /**
     * 功能描述:获得指定日期在一周中是第几天
     *
     * @param date 日期
     * @return int 1-7
     * @author Y(^ _ ^)Y  2014-8-22 下午09:38:21
     */
    public static int getDayOfWeek(String date) {
        return getDayOfWeek(doFormatDate(date, "yyyy-MM-dd"));
    }

    /**
     * 功能描述:获得今天在本年度中是第几天
     *
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 上午10:04:38
     */
    public static int getDayOfYear() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }


    /**
     * 功能描述:获得当前时间在本月是第几周
     *
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 上午10:06:28
     */
    public static int getWeekOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 功能描述:获得当前时间在本年度是第几周
     *
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 上午10:10:30
     */
    public static int getWeekOfYear() {
        int index = 0;
        try {
// Date startDate = date;
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTime(new Date());
            index = nowCal.get(Calendar.WEEK_OF_YEAR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    /**
     * 功能描述:获得指定时间在一年中是第几周
     *
     * @param day String时间
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 下午12:57:21
     */
    public int getWeekOfYear(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int index = 0;
        try {
            Date startDate = format.parse(date);
            index = getWeekIndexOfYear(startDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    /**
     * 功能描述:获得指定时间在一年中是第几周
     *
     * @param day java.unit.date时间
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 下午12:57:21
     */
    public static int getWeekIndexOfYear(Date date) {
        int index = 0;
        try {
            Date startDate = date;
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTime(startDate);
            index = nowCal.get(Calendar.WEEK_OF_YEAR);
        } catch (Exception e) {
// TODO: handle exception
        }
        return index;
    }


    /**
     * 功能描述:获得指定日期在当前月中是第几天
     *
     * @param date 日期
     * @return int
     * @author Y(^ _ ^)Y  2014-8-22 下午09:43:55
     */
    public static int getDayOfMonth(Date date) {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.setTime(date);
        int x = aCalendar.get(Calendar.DAY_OF_MONTH);
        return x;
    }

    /**
     * 功能描述:获得当前月共有多少天
     *
     * @return int
     * @author Y(^ _ ^)Y 2014-1-25 下午03:52:07
     */
    public static int getCountDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int lastday = aCalendar.getActualMaximum(Calendar.DATE);
        return lastday;
    }

    /**
     * 功能描述:获得本年共有多少天
     *
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 上午10:38:29
     */
    public static int getCountDayOfYear() {
        Calendar cd = Calendar.getInstance();
        cd.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天
        cd.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天。
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
        return MaxYear;
    }

    /**
     * 功能描述:获得本月第一天日期
     *
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-22 下午09:50:39
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }

    /**
     * 功能描述:获得本月的第一天的日期
     *
     * @return String
     * @author Y(^ _ ^)Y 2014-1-25 下午03:59:28
     */
    public static String getFirstDayOfMonthStr(Date date) {
        return doFormatDate(getFirstDayOfMonth(date), "yyyy-MM-dd");


    }

    /**
     * 功能描述:获得本月最后一天日期
     *
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-22 下午10:07:18
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cDay.getTime();
    }

    /**
     * 功能描述:获得本月最后一天
     *
     * @return String
     * @author Y(^ _ ^)Y  2014-8-22 下午10:10:42
     */
    public static String getLastDayOfMonthStr(Date date) {
        return doFormatDate(getLastDayOfMonth(date), "yyyy-MM-dd");
    }

    /**
     * 功能描述:获得上月第一天
     *
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:16:38
     */
    public static Date getLastMonthFirstDay(Date date) {
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(date);
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        lastDate.add(Calendar.MONTH, -1);// 减一个月，变为下月的1号
        // lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天
        return lastDate.getTime();
    }

    /**
     * 功能描述:获得上月第一天
     *
     * @return String
     * @author Y(^ _ ^)Y  2014-8-23 上午10:16:38
     */
    public static String getLastMonthFirstDayStr(Date date) {
        return doFormatDate(getLastMonthFirstDay(date), "yyyy-MM-dd");
    }

    /**
     * 功能描述:获得上月最后一天
     *
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:21:31
     */
    public static Date getLastMonthLastDay(Date date) {
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(date);//指定时间
        lastDate.add(Calendar.MONTH, -1);// 减一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
        return lastDate.getTime();
    }

    /**
     * 功能描述:获得上月最后一天
     *
     * @return String
     * @author Y(^ _ ^)Y  2014-8-23 上午10:21:31
     */
    public static String getLastMonthLastDayStr(Date date) {
        return doFormatDate(getLastMonthLastDay(date), "yyyy-MM-dd");
    }

    /**
     * 功能描述:获得下月第一天
     *
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:24:15
     */
    public static Date getNextMonthFirstDay(Date date) {
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(date);//设置时间
        lastDate.add(Calendar.MONTH, 1);// 加一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        return lastDate.getTime();
    }

    /**
     * 功能描述:获得下月第一天
     *
     * @return String
     * @author Y(^ _ ^)Y  2014-8-23 上午10:24:15
     */
    public static String getNextMonthFirstDayStr(Date date) {
        return doFormatDate(getNextMonthFirstDay(date), "yyyy-MM-dd");
    }

    /**
     * 功能描述:获得下月最后一天
     *
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:26:21
     */
    public static Date getNextMonthLastDay(Date date) {
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(date);//设置时间
        lastDate.add(Calendar.MONTH, 1);// 加一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
        return lastDate.getTime();
    }

    /**
     * 功能描述:获得下月最后一天
     *
     * @return String
     * @author Y(^ _ ^)Y  2014-8-23 上午10:26:21
     */
    public static String getNextMonthLastDayStr(Date date) {
        return doFormatDate(getNextMonthLastDay(date), "yyyy-MM-dd");
    }

    /**
     * 功能描述: 获得本季度第一天日期
     *
     * @param date java.util.Date
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:44:02
     */
    public static Date getFirstDayOfQuarter(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        int curMonth = cDay.get(Calendar.MONTH);
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH) {
            cDay.set(Calendar.MONTH, Calendar.JANUARY);
        }
        if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE) {
            cDay.set(Calendar.MONTH, Calendar.APRIL);
        }
        if (curMonth >= Calendar.JULY && curMonth <= Calendar.AUGUST) {
            cDay.set(Calendar.MONTH, Calendar.JULY);
        }
        if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {
            cDay.set(Calendar.MONTH, Calendar.OCTOBER);
        }
        cDay.set(Calendar.DAY_OF_MONTH, cDay
                .getActualMinimum(Calendar.DAY_OF_MONTH));
        return cDay.getTime();
    }

    /**
     * 功能描述: 获得本季度第一天日期
     *
     * @param date java.util.Date
     * @return String
     * @author Y(^ _ ^)Y  2014-8-23 上午10:44:02
     */
    public static String getFirstDayOfQuarterStr(Date date) {
        Date firstDate = getFirstDayOfQuarter(date);
        return doFormatDate(firstDate, "yyyy-MM-dd");
    }

    /**
     * 功能描述:获得本季度最后一天日期
     *
     * @param date date java.util.Date
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:50:21
     */
    public static Date getLastDayOfQuarter(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        int curMonth = cDay.get(Calendar.MONTH);
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH) {
            cDay.set(Calendar.MONTH, Calendar.MARCH);
        }
        if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE) {
            cDay.set(Calendar.MONTH, Calendar.JUNE);
        }
        if (curMonth >= Calendar.JULY && curMonth <= Calendar.AUGUST) {
            cDay.set(Calendar.MONTH, Calendar.AUGUST);
        }
        if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {
            cDay.set(Calendar.MONTH, Calendar.DECEMBER);
        }
        cDay.set(Calendar.DAY_OF_MONTH, cDay
                .getActualMaximum(Calendar.DAY_OF_MONTH));
        System.out.println(cDay.getTime());
        return cDay.getTime();
    }

    /**
     * 功能描述:获得本季度最后一天日期
     *
     * @param date date java.util.Date
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:50:21
     */
    public static String getLastDayOfQuarterStr(Date date) {
        Date lastDate = getLastDayOfQuarter(date);
        return doFormatDate(lastDate, "yyyy-MM-dd");
    }


    /**
     * 功能描述:获得明年第一天日期(yyyy-MM-dd)
     *
     * @param date date java.util.Date
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:30:58
     */
    public static Date getNextYearFirstDay(Date date) {
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(date);
        lastDate.add(Calendar.YEAR, 1);// 加一个年
        lastDate.set(Calendar.DAY_OF_YEAR, 1);
        return lastDate.getTime();
    }

    /**
     * 功能描述:获得明年第一天日期(yyyy-MM-dd)
     *
     * @param date date java.util.Date
     * @return String
     * @author Y(^ _ ^)Y  2014-8-23 上午10:30:58
     */
    public static String getNextYearFirstDayStr(Date date) {
        return doFormatDate(getNextYearFirstDay(date), "yyyy-MM-dd");

    }

    /**
     * 功能描述:获取明年最后一天日期
     *
     * @param date date java.util.Date
     * @return Date
     * @author Y(^ _ ^)Y  2014-8-23 上午10:28:45
     */
    public static Date getNextYearLastDay(Date date) {
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(date);
        lastDate.add(Calendar.YEAR, 1);// 加一个年
        lastDate.set(Calendar.DAY_OF_YEAR, 1);
        lastDate.roll(Calendar.DAY_OF_YEAR, -1);
        return lastDate.getTime();
    }

    /**
     * 功能描述:获取明年最后一天日期
     *
     * @param date date java.util.Date
     * @return String
     * @author Y(^ _ ^)Y  2014-8-23 上午10:28:45
     */
    public static String getNextYearLastDayStr(Date date) {
        return doFormatDate(getNextYearLastDay(date), "yyyy-MM-dd");
    }

    /**
     * 功能描述: 获得两个日期相差多少分钟
     *
     * @param date1 开始日期java.util.Date
     * @param date2 结束日期java.util.Date
     * @author Y(^ _ ^)Y  2014-8-23 上午11:20:06
     * @return　int
     */
    public static int getDiffMinutes(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        return (int) (c1.getTimeInMillis() - c2.getTimeInMillis()) / (60 * 1000);
    }

    /**
     * 功能描述: 获得两个日期相差多少分钟
     *
     * @param date1 开始日期java.util.Date
     * @param date2 结束日期java.util.Date
     * @author Y(^ _ ^)Y  2014-8-23 上午11:20:06
     * @return　int
     */
    public static int getDiffMinutes(String date1, String date2) {
        if (date1.length() <= 10) {
            date1 = date1 + " 00:00:00";
        }
        if (date2.length() <= 10) {
            date2 = date2 + " 00:00:00";
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(doFormatDate(date1, "yyyy-MM-dd HH:mm:ss"));
        Calendar c2 = Calendar.getInstance();
        c2.setTime(doFormatDate(date2, "yyyy-MM-dd HH:mm:ss"));
        return (int) (c1.getTimeInMillis() - c2.getTimeInMillis()) / (60 * 1000);
    }

    /**
     * 功能描述:获得两个日期相差的天数
     *
     * @param date1 开始日期java.util.Date
     * @param date2 结束日期java.util.Date
     * @return int
     * @author Y(^ _ ^)Y  2014-8-23 上午11:13:00
     */
    public static int getDiffDays(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        return (int) Math.abs((c1.getTimeInMillis() - c2.getTimeInMillis()) / (24 * 3600 * 1000));
    }

    /**
     * 功能描述:获得两个日期相差的天数
     *
     * @param date1 　开始日期　String
     * @param date2 　结束日期　String
     * @return long
     * @author Y(^ _ ^)Y  2014-8-23 上午11:16:04
     */
    public static Long getDiffDays(String date1, String date2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(date1);
            Date mydate = myFormatter.parse(date2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0L;
        }
        return day;
    }

    /**
     * 功能描述: 得到 指定小时 之内的时间，根据minute循环 增加分钟
     *
     * @param startHour 开始小时 如：8:00
     * @param endHour   结束小时 如: 10:00
     * @param minute    循环递增 数 如： 10
     * @return List<Map                               <                               String                               ,                                                               String>> 如：[{HOUR=8:10}, {HOUR=8:20},......]
     * @author Y(^ _ ^)Y  2014-8-23 下午12:01:09
     */
    public static List<Map<String, String>> getHourList(String startHour,
                                                        String endHour, int minute) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date startDate = format.parse(startHour);
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);


            Date endDate = format.parse(endHour);
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);


            while (startCal.compareTo(endCal) <= 0) {
                Hashtable<String, String> hash = new Hashtable<String, String>();
                String hour = format.format(startCal.getTime());
                hash.put("HOUR", hour);
                list.add(hash);
                startCal.add(Calendar.MINUTE, minute);
            }
        } catch (ParseException e) {
        }
        return list;
    }

    /**
     * 功能描述:得到一年中某周的第一天（开始于星期日）日期，
     *
     * @param year       当前年份
     * @param week_index 当前周
     * @param patten     返回的日期格式
     * @return String日期 ：yyyy-MM-dd
     */
    public static String getFirstDayOfWeek(String year, String week_index,
                                           String patten) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy w");
        Date date = new Date();
        try {
            date = formatter.parse(year + " " + week_index);
        } catch (ParseException e) {
            return "";
        }


        SimpleDateFormat formatter2 = new SimpleDateFormat(patten);
        return formatter2.format(date);


    }

    /**
     * 功能描述:得到一年中某周的最后一天日期，
     *
     * @param year       当前年份
     * @param week_index 当前周
     * @param patten     返回的日期格式
     * @return String日期 ：yyyy-MM-dd
     */
    public static String getLastDayOfWeek(String year, String week_index, String patten) {
        String first_day = getFirstDayOfWeek(year, String.valueOf(Integer.valueOf(week_index) + 1), patten);
        return getOffsetDay(first_day, -1, patten);

    }

    /**
     * 功能描述:通过传入的时间获得，多少天后的日期
     *
     * @param currentDay 2014-1-1
     * @param offset     多少天
     * @param pattern    得到格式 yyyy-MM-dd
     * @return
     */
    public static String getOffsetDay(String date, Integer day, String format) {
        try {
            SimpleDateFormat pattern = new SimpleDateFormat(format);
            String index = "";
            Date startDate = pattern.parse(date);
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);
            startCal.add(Calendar.DAY_OF_MONTH, day - 1);
            index = pattern.format(startCal.getTime());
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * 功能描述:获得一年中所有周的信息 (只取了周一到周五)
     *
     * @param date java.unit.Date日期
     * @return List
     * @author Y(^ _ ^)Y  2014-8-23 下午12:05:20
     */
    public static List getWeekListOfYear(Date date) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(date);
        int year = nowCal.get(Calendar.YEAR);
        int weekCnt = getCountWeeksOfYear(year);
        String patten = "yyyy年MM月dd号";
        Map map = null;
        List list = new ArrayList();
        for (int i = 0; i < weekCnt; i++) {
            int weekIndex = i + 1;
            String beginDate = getOffsetDay(getFirstDayOfWeek(String.valueOf(year), String.valueOf(weekIndex), patten), 2, patten);
            String endDate = getLastDayOfWeek(String.valueOf(year), String.valueOf(weekIndex), patten);
            String yearWeek = year + "-" + weekIndex;
            map = new HashMap();
            map.put("yearWeek", yearWeek);
            map.put("weekIndex", weekIndex);
            map.put("weekDay", String.valueOf(year) + "年第" + weekIndex + "周（" + beginDate + "-" + endDate + "）");
            list.add(map);
        }
        return list;
    }

    /**
     * 功能描述: 得到指定时间段内的时间列表  如： getDayList("2014-01-01","2014-12-30","yyyy-MM-dd")
     *
     * @param startMonth 开始月份
     * @param endMonth   结束月份
     * @param pattern    格式：如yyyy-MM
     * @return [{DAY=2014-01-01}, {DAY=2014-01-02},....., {DAY=2014-12-30}]
     */
    public static List<Map<String, String>> getDayList(String startday,
                                                       String endday, String pattern) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date startDate = format.parse(startday);
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);


            Date endDate = format.parse(endday);
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            if (startCal.compareTo(endCal) <= 0) {
                while (startCal.compareTo(endCal) <= 0) {
                    Hashtable<String, String> hash = new Hashtable<String, String>();
                    String month = format.format(startCal.getTime());
                    hash.put("DAY", month);
                    list.add(hash);
                    startCal.add(Calendar.DAY_OF_MONTH, 1);
                }
            } else {
                while (startCal.compareTo(endCal) >= 0) {
                    Hashtable<String, String> hash = new Hashtable<String, String>();
                    String month = format.format(startCal.getTime());
                    hash.put("DAY", month);
                    list.add(hash);
                    startCal.add(Calendar.DAY_OF_MONTH, -1);
                }
            }
        } catch (ParseException e) {
        }
        return list;
    }

    /**
     * 功能描述:获得指定时间所在周的周一到周日的日期
     *
     * @param date 字符串时间
     * @return List
     * @author Y(^ _ ^)Y  2014-8-23 下午02:22:54
     */
    public static List<String> getListDayOfWeek(String date) {
        List<String> list = new ArrayList<String>();
//当前时间所在周的周一日期
        String monDate = getWeekFirstDayStr(doFormatDate(date, "yyyy-MM-dd"));
//当前时间所在周的周日 日期
        String sunDate = getWeekLastDayStr(doFormatDate(date, "yyyy-MM-dd"));
        List<Map<String, String>> listMap = getDayList(monDate, sunDate, "yyyy-MM-dd");
        for (int i = 0; i < listMap.size(); i++) {
            Map<String, String> map = listMap.get(i);
            list.add(map.get("DAY"));
        }
        return list;
    }

    /**
     * 功能描述:获得当前时间所在月的月初到月末的时间
     *
     * @param date 日期
     * @return List
     * @author Y(^ _ ^)Y  2014-8-23 下午03:22:17
     */
    public static List<String> getListDayOfMonth(String date) {
        List<String> list = new ArrayList<String>();
//获得当前时间所在月的月初时间
        String firstDate = getFirstDayOfMonthStr(doFormatDate(date, "yyyy-MM-dd"));
//获得当前时间所在月的月末时间
        String endDate = getLastDayOfMonthStr(doFormatDate(date, "yyyy-MM-dd"));
        List<Map<String, String>> listMap = getDayList(firstDate, endDate, "yyyy-MM-dd");

        for (int i = 0; i < listMap.size(); i++) {
            Map<String, String> map = listMap.get(i);
            list.add(map.get("DAY"));
        }
        return list;
    }


    /**
     * 功能描述: 是否是闰年
     *
     * @param year 年份（int）
     * @return boolean true/是，false/不是
     * @author Y(^ _ ^)Y  2014-8-23 上午10:41:50
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 功能描述: 是否是闰年
     *
     * @param year 年份（int）
     * @return boolean true/是，false/不是
     * @author Y(^ _ ^)Y  2014-8-23 上午10:41:50
     */
    public static boolean isLeapYear2(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }

    /**
     * 功能描述:判断指定时间是否是双休日
     *
     * @param date 日期
     * @return boolean true/是，false/不是
     * @author Y(^ _ ^)Y  2014-8-23 下午01:58:35
     */
    public static boolean isWeekEnd(String date) {
        if (getDayOfWeek(date) == 6 || getDayOfWeek(date) == 7) {
            return true;
        }
        return false;
    }

    /**
     * 功能描述:判断指定时间是否是双休日
     *
     * @param date 日期
     * @return boolean true/是，false/不是
     * @author Y(^ _ ^)Y  2014-8-23 下午01:58:35
     */
    public static boolean isWeekEnd(Date date) {
        if (getDayOfWeek(date) == 6 || getDayOfWeek(date) == 7) {
            return true;
        }
        return false;
    }


    public static Date getDateForWeekDay(int weekDay, Date basisDate) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(basisDate);
        int basisWeekDay = currentCalendar.get(Calendar.DAY_OF_WEEK);
        currentCalendar.add(Calendar.DATE, weekDay - basisWeekDay);
        return currentCalendar.getTime();
    }


}
