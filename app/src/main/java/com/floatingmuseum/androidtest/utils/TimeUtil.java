package com.floatingmuseum.androidtest.utils;


import android.icu.text.UFormat;
import android.text.format.DateUtils;

import com.floatingmuseum.androidtest.App;
import com.liulishuo.filedownloader.i.IFileDownloadIPCCallback;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Floatingmuseum on 2017/3/17.
 */

public class TimeUtil {

    /**
     * 一天起始时间 00:00:00
     */
    public static Date getTodayStartTime() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTime();
    }

    /**
     * 一天结束时间 23:59:59
     */
    public static Date getTodayEndTime() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return currentDate.getTime();
    }

    public static void getTime(long time) {
        Date date = new Date();
        DateUtils.formatDateTime(App.context, time, DateUtils.FORMAT_SHOW_TIME);
    }

    private static long hourMillis = 1000 * 60 * 60;
    private static long minuteMillis = 1000 * 60;
    private static long secondMillis = 1000;

    public static String getUsingTime(long time) {
        String usingTime = "";

        boolean biggerThanHour = false;
        boolean biggerThanMinute = false;
        boolean biggerThanSecond = false;

        if (time >= hourMillis) {
            biggerThanHour = true;
            usingTime = time / hourMillis + "小时";
        }

        if (time >= minuteMillis) {
            biggerThanMinute = true;
            if (biggerThanHour) {
                usingTime += time % hourMillis / minuteMillis + "分钟";
            } else {
                usingTime += time / minuteMillis + "分钟";
            }
        }

        if (time >= secondMillis) {
            biggerThanSecond = true;
            if (biggerThanHour && biggerThanMinute) {
                usingTime += time % hourMillis % minuteMillis / secondMillis + "秒";
            } else if (biggerThanMinute) {
                usingTime += time % minuteMillis / secondMillis + "秒";
            } else {
                usingTime += time / secondMillis + "秒";
            }
        }

        if (time > 0) {
            if (biggerThanHour && biggerThanMinute && biggerThanSecond) {
                usingTime += time % hourMillis % minuteMillis % secondMillis + "毫秒";
            } else if (biggerThanMinute && biggerThanSecond) {
                usingTime += time % minuteMillis % secondMillis + "毫秒";
            } else if (biggerThanSecond) {
                usingTime += time % secondMillis + "毫秒";
            } else {
                usingTime += time + "毫秒";
            }
        }

        return usingTime;
    }
}
