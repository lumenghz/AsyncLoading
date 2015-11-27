package com.winchance.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unuse")
public class DateUtil {
    private static final ThreadLocal<SimpleDateFormat> monthFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> timeFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private static final ThreadLocal<Calendar> calendarThreadLocal = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    };

    public static String toStringMonth(Date month) {
        return monthFormatThreadLocal.get().format(month);
    }

    public static String toStringDate(Date time) {
        return dateFormatThreadLocal.get().format(time);
    }

    public static String toStringTime(Date time) {
        return timeFormatThreadLocal.get().format(time);
    }

    public static Date toMonth(Date time) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTime(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date toMonth(String time)
            throws ParseException {
        return monthFormatThreadLocal.get().parse(time);
    }

    public static Date toDate(Date time) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date toDate(String date)
            throws ParseException {
        return dateFormatThreadLocal.get().parse(date);
    }

    public static Date toTime(Date time) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTime(time);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date toTime(String time)
            throws ParseException {
        return timeFormatThreadLocal.get().parse(time);
    }

    public static Date getNowMonth() {
        return toMonth(new Date());
    }

    public static Date getNowDate() {
        return toDate(new Date());
    }

    public static Date getNowTime() {
        return toTime(new Date());
    }

    public static Date timeAfterDays(Date time, int days) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTime(time);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date timeBeforeDays(Date time, int days) {
        return timeAfterDays(time, -1 * days);
    }

    public static Date timeAfterSeconds(Date time, int seconds) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTime(time);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public static Date timeBeforeSeconds(Date time, int seconds) {
        return timeAfterSeconds(time, -1 * seconds);
    }

    public static void sleep(int sec)
            throws InterruptedException {
        Thread.sleep(sec * 1000);
    }
}
