package com.floatingmuseum.androidtest.utils;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Floatingmuseum on 2017/3/17.
 */

public class TimeUtil {

    public static Date getTodayStartTime() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTime();
    }

    public static Date getTodayEndTime() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return currentDate.getTime();
    }
}
