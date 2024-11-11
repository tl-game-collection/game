package com.xiuxiu.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.xiuxiu.core.log.Logs;

public final class TimeUtil {
    // 3个月
    public static final long THREE_MONTH_MS = 3 * 30 * 24 * 60 * 60 * 1000L;
    // 1个月
    public static final long ONE_MONTH_MS = 1 * 30 * 24 * 60 * 60 * 1000L;
    // 半小时
    public static final long HALF_HOUR_MS = 1 * 30 * 60 * 1000L;
    // 一小时
    public static final long ONE_HOUR_MS = 1 * 60 * 60 * 1000L;
    // 八小时
    public static final long EIGHT_HOUR_MS = 8 * 60 * 60 * 1000L;
    // 一天
    public static final long ONE_DAY_MS = 24 * 60 * 60 * 1000L;
    public static final long ONE_DAY = 24 * 60 * 60L;
    // 七天
    public static final long SAVEN_DAY_MS = 7 * 24 * 60 * 60 * 1000L;
    // 一分钟
    public static final long ONE_MINUTE_MS = 60 * 1000;
    // 五分钟
    public static final long FIVE_MINUTE_MS = 5 * 60 * 1000;
    // 4年天数
    public static final int FOUR_YEARS = 365 * 3 + 366;

    // 平年每月天数
    private static int NOR_MOTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    // 闰年每月天数
    private static int LEAP_MOTH[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static boolean isSameDay(long day1, long day2) {
        Date d1 = new Date(day1);
        Date d2 = new Date(day2);
        return d1.getYear() == d2.getYear() &&
                d1.getMonth() == d2.getMonth() &&
                d1.getDate() == d2.getDate();
    }

    public static String format(long timestamp) {
        return format("yyyy-MM-dd HH:mm:ss.SSS", timestamp);
    }
    
    /**
     * 时间转化
     * 
     * @return
     */
    private static Calendar dateToCalendar(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }
    
    /**
     * 是否是同一天
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isOnDay(Date time1,Date time2){
        Calendar time1Cal = dateToCalendar(time1);
        Calendar time2Cal = dateToCalendar(time2);
        if(time1Cal.get(Calendar.DAY_OF_YEAR) == time2Cal.get(Calendar.DAY_OF_YEAR)){
            return true;
        }
        return false;
    }
    
    /**
     * 是否是同年同一月
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isOnMonth(Date time1,Date time2){
        Calendar time1Cal = dateToCalendar(time1);
        Calendar time2Cal = dateToCalendar(time2);
        if(time1Cal.get(Calendar.YEAR) == time2Cal.get(Calendar.YEAR) && time1Cal.get(Calendar.MONTH) == time2Cal.get(Calendar.MONTH)){
            return true;
        }
        return false;
    }

    /**
     * 使用ThreadLocal以空间换时间解决SimpleDateFormat线程安全问题
     * @param fmt
     * @param timestamp
     * @return
     */
    public static String format(String fmt, long timestamp) {
//        return (new SimpleDateFormat(fmt)).format(new Date(timestamp));
        return getFormat(fmt).format(new Date(timestamp));
    }
    
    public static final String TIME_SDF1 = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_SDF2 = "MM-dd HH:mm";
    public static final String TIME_SDF3 = "yyyy/MM/dd";
    public static final String TIME_SDF4 = "HH:mm:ss";
    public static final String TIME_SDF5 = "yyyy-MM-dd";
    public static final String TIME_SDF6 = "yyyyMM";
    public static final String TIME_SDF7 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String TIME_SDF8 = "yyyyMMddHHmmssSSS";
    public static final String TIME_SDF9 = "yyyy_MM_dd_HH_mm_ss";
    
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF1 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF1);         
        }     
    }; 
    
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF2 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF2);         
            }     
    }; 
    
    
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF3 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF3);         
            }     
    }; 
    
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF4 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF4);         
            }     
    };
    
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF5 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF5);         
            }     
    };
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF6 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF6);         
            }     
    };
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF7 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF7);         
            }     
    };
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF8 = new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF8);         
            }     
    };
    @SuppressWarnings("rawtypes")
    private static ThreadLocal SDF9= new ThreadLocal(){         
        protected synchronized Object initialValue() {             
            return new SimpleDateFormat(TIME_SDF9);         
            }     
    };
    
    private static DateFormat getFormat(String time){
        if(time.equals(TIME_SDF1)){
            return (DateFormat) SDF1.get();
        }
        if(time.equals(TIME_SDF2)){
            return (DateFormat) SDF2.get();
        }
        if(time.equals(TIME_SDF3)){
            return (DateFormat) SDF3.get();
        }
        if(time.equals(TIME_SDF4)){
            return (DateFormat) SDF4.get();
        }
        if(time.equals(TIME_SDF5)){
            return (DateFormat) SDF5.get();
        }
        if(time.equals(TIME_SDF6)){
            return (DateFormat) SDF6.get();
        }
        if(time.equals(TIME_SDF7)){
            return (DateFormat) SDF7.get();
        }
        if(time.equals(TIME_SDF8)){
            return (DateFormat) SDF8.get();
        }
        if(time.equals(TIME_SDF9)){
            return (DateFormat) SDF9.get();
        }
        return null;   
    } 
    
    /**
     * 返回<pre>yyyy-MM-dd HH:mm:ss</pre>格式日期字符串
     * @param date
     * @return
     */
    public static String getStringDate(Date date) {
        return getFormat(TIME_SDF1).format(date);
    }

    /**
     * 返回<pre>yyyy-MM-dd HH:mm:ss</pre>格式日期字符串
     * @param date
     * @return
     */
    public static String getNowStringDate() {
        return getFormat(TIME_SDF1).format(new Date());
    }

    /**
     * 返回<pre>yyyy-MM-dd HH:mm:ss</pre>格式日期字符串
     * @param date
     * @return
     */
    public static Date getDateByString(String date) {
        try {
            return getFormat(TIME_SDF1).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Logs.CORE.error("{}日期格式有误{}", date, "yyyy-MM-dd HH:mm:ss");
            return null;
        }
    }

    public static long getZeroTimestamp(long now) {
        return now - (now + EIGHT_HOUR_MS) % ONE_DAY_MS;
    }

    public static long getZeroTimestampWithToday() {
        long now = System.currentTimeMillis();
        return now - (now + EIGHT_HOUR_MS) % ONE_DAY_MS;
    }

    public static String getTimeFormat(long time) {
        int temp = (int) (time / ONE_MINUTE_MS);
        int min = temp % 60;
        int hour = (temp / 60) % 60;
        int day = (temp / 3600) % 24;
        return String.format("%2d天%02d小时%02d分", day, hour, min);
    }

    public static long getCurMothZeroTimestamp(long now) {
        long localNow = now + TimeZone.getDefault().getRawOffset();
        int nowSec = (int) (localNow / 1000);
        int day = (int) (nowSec / ONE_DAY + (0 != (nowSec % ONE_DAY) ? 1 : 0));
        int remain = day % FOUR_YEARS;
        boolean leapYear = false;
        if (remain < 365) {

        } else if (remain < 365 * 2) {
            remain -= 365;
        } else if (remain < 365 * 3) {
            remain -= 365 * 2;
        } else {
            remain -= 365 * 3;
            leapYear = true;
        }
        int mothAndDay = getMonthAndDay(leapYear, remain);
        int decDay = mothAndDay & 0xFFFF;
        long zero = getZeroTimestamp(now);
        return zero - (decDay - 1) * ONE_DAY_MS;
    }

    private static int getMonthAndDay(boolean leapYear, int days) {
        int moth[] = leapYear ? LEAP_MOTH : NOR_MOTH;
        int temp = 0;
        int m = 0;
        int d = 0;
        for (int i = 0; i < 12; ++i) {
            temp = days - moth[i];
            if (temp <= 0) {
                m = i + 1;
                if (0 == temp) {
                    d = moth[i];
                } else {
                    d = days;
                }
                break;
            }
            days = temp;
        }
        return m << 16 | d;
    }
}
