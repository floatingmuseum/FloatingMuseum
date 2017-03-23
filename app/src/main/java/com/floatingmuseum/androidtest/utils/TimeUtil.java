package com.floatingmuseum.androidtest.utils;


import android.text.format.DateUtils;

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
}
