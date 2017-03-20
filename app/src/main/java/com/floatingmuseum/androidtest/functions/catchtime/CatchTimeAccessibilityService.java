package com.floatingmuseum.androidtest.functions.catchtime;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.DateUtils;
import android.util.TimeUtils;
import android.view.accessibility.AccessibilityEvent;

import com.floatingmuseum.androidtest.utils.RealmManager;
import com.floatingmuseum.androidtest.utils.TimeUtil;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

import io.realm.RealmModel;


/**
 * Created by Floatingmuseum on 2017/3/17.
 */

public class CatchTimeAccessibilityService extends AccessibilityService {

    private String currentPackageName;
    private AppTimeUsingInfo currentAppTimeUsingInfo;

    @Override
    public void onCreate() {
        Logger.d("CatchTimeAccessibilityService...onCreate");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        startService(new Intent(this, CatchTimeService.class));
        Logger.d("CatchTimeAccessibilityService...onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence csPackageName = event.getPackageName();
        CharSequence csClassName = event.getClassName();
        Logger.d("CatchTimeAccessibilityService...当前包名1:" + csPackageName + "...类名:" + event.getClassName());
        String newPackageName;
        if (csPackageName != null && csClassName != null) {
            newPackageName = csPackageName.toString();
            String className = csClassName.toString();
            if (!newPackageName.equals(currentPackageName) && hasActivity(newPackageName, className)) {
                packageNameChanged(newPackageName);
                Logger.d("CatchTimeAccessibilityService...当前包名2:" + currentPackageName + "...类名:" + event.getClassName());
            }
        }
    }

    /**
     * 当桌面文件夹中的应用被启动时,下一秒会出现launcher包名带着一个layout的class类名,导致计算不准确
     * 所以要判断一下当前应用所包含的activities是否包含此类名
     */
    public boolean hasActivity(String newPackageName, String className) {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(newPackageName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] info = packageInfo.activities;
            for (ActivityInfo activityInfo : info) {
                Logger.d("CatchTimeAccessibilityService...包名:" + newPackageName + "...所含类名:" + activityInfo.name + "..." + activityInfo.toString());
                if (className.equals(activityInfo.name)) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void packageNameChanged(String newPackageName) {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(newPackageName, 0);
            String appName = applicationInfo.loadLabel(pm).toString();
            if (currentPackageName == null) {//first time
                currentAppTimeUsingInfo = new AppTimeUsingInfo(appName, newPackageName, TimeUtil.getTodayStartTime().getTime(), System.currentTimeMillis(), 0, 0);
            } else {
                //不等于null说明,数据库中存在未结算使用时间的应用,先结算,再记录新应用
                long endTime = System.currentTimeMillis();
                long usingTime = endTime - currentAppTimeUsingInfo.getStartTime();
                currentAppTimeUsingInfo.setEndTime(endTime);
                currentAppTimeUsingInfo.setUsingTime(usingTime);
                RealmManager.insertOrUpdate(currentAppTimeUsingInfo);
                Date today = TimeUtil.getTodayStartTime();
                currentAppTimeUsingInfo = new AppTimeUsingInfo(appName, newPackageName, TimeUtil.getTodayStartTime().getTime(), System.currentTimeMillis(), 0, 0);
                RealmManager.insertOrUpdate(currentAppTimeUsingInfo);
                Date newToday = TimeUtil.getTodayStartTime();
                Logger.d("CatchTimeAccessibilityService...时间:" + today.before(newToday) + "..." + today.after(newToday) + "..." + DateUtils.isToday(today.getTime()) + "..." + DateUtils.isToday(newToday.getTime()));
            }

            Logger.d("CatchTimeAccessibilityService...当前应用名:" + appName + "..." + TimeUtil.getTodayStartTime().getTime() + "..." + TimeUtil.getTodayStartTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentPackageName = newPackageName;
    }

    @Override
    public void onInterrupt() {
        Logger.d("CatchTimeAccessibilityService...onInterrupt");
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, CatchTimeService.class));
        Logger.d("CatchTimeAccessibilityService...onDestroy");
    }
}
