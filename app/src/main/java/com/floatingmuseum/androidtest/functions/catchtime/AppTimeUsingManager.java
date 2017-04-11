package com.floatingmuseum.androidtest.functions.catchtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

import com.floatingmuseum.androidtest.App;
import com.floatingmuseum.androidtest.utils.RealmManager;
import com.floatingmuseum.androidtest.utils.TimeUtil;
import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/4/11.
 */

public class AppTimeUsingManager {

    private static AppTimeUsingManager appTimeUsingManager;

    private String currentPackageName;
    private AppTimeUsingInfo currentAppTimeUsingInfo;
    private ScreenReceiver screenReceiver;

    private AppTimeUsingManager() {
//        App.context.startService(new Intent(App.context, CatchTimeService.class));
        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_ON);//亮屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);//熄屏
//        filter.addAction(Intent.ACTION_USER_PRESENT);//解锁
        screenReceiver = new ScreenReceiver();
        App.context.registerReceiver(screenReceiver, filter);
        Logger.d("AppTimeUsingManager...onServiceConnected");
    }

    public static AppTimeUsingManager getInstance() {
        if (appTimeUsingManager == null) {
            synchronized (AppTimeUsingManager.class) {
                if (appTimeUsingManager == null) {
                    appTimeUsingManager = new AppTimeUsingManager();
                }
            }
        }
        return appTimeUsingManager;
    }

    public void countingAppUsingTime(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        CharSequence csPackageName = event.getPackageName();
        CharSequence csClassName = event.getClassName();
        Logger.d("AppTimeUsingManager...当前包名(捕捉所有):" + csPackageName + "...类名:" + event.getClassName());
        String newPackageName;
        if (csPackageName != null && csClassName != null) {
            newPackageName = csPackageName.toString();
            String className = csClassName.toString();
            if (!newPackageName.equals(currentPackageName) && hasActivity(newPackageName, className)) {
                packageNameChanged(newPackageName);
                Logger.d("AppTimeUsingManager...当前包名(捕捉需要):" + currentPackageName + "...类名:" + event.getClassName());
            }
        }
    }

    /**
     * 当桌面文件夹中的应用被启动时,下一秒会出现launcher包名带着一个layout的class类名,导致计算不准确
     * 所以要判断一下当前应用所包含的activities是否包含此类名
     */
    public boolean hasActivity(String newPackageName, String className) {
        PackageManager pm = App.context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(newPackageName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] info = packageInfo.activities;
            Logger.d("AppTimeUsingManager...ActivityInfo:" + info);
            if (info != null) {
                for (ActivityInfo activityInfo : info) {
//                Logger.d("AppTimeUsingManager...包名:" + newPackageName + "...所含类名:" + activityInfo.name + "..." + activityInfo.toString());
                    if (className.equals(activityInfo.name)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 包名变换时，结算应用时间，更新当前包名
     */
    private void packageNameChanged(String newPackageName) {
        PackageManager pm = App.context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(newPackageName, 0);
            String appName = applicationInfo.loadLabel(pm).toString();
            if (currentPackageName != null) {//first time
                countingUsingTime();
            }
            prepareNewAppTimeUsingInfo(appName, newPackageName);
            Logger.d("AppTimeUsingManager...当前应用名:" + appName + "..." + TimeUtil.getTodayStartTime().getTime() + "..." + TimeUtil.getTodayStartTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentPackageName = newPackageName;
    }

    /**
     * 结算时间，插入数据库
     */
    private void countingUsingTime() {
        if (!isTooShort()) {
            Logger.d("AppTimeUsingManager...时间充足...进行计算应用名:" + currentAppTimeUsingInfo.getAppName() + "..." + currentAppTimeUsingInfo.getPackageName());
            //结算数据
            long endTime = System.currentTimeMillis();
            long usingTime = endTime - currentAppTimeUsingInfo.getStartTime();
            currentAppTimeUsingInfo.setEndTime(endTime);
            currentAppTimeUsingInfo.setUsingTime(usingTime);
            //插入到数据库
            RealmManager.insertOrUpdate(currentAppTimeUsingInfo);
        } else {
            Logger.d("AppTimeUsingManager...间隔过短...忽略计算应用名:" + currentAppTimeUsingInfo.getAppName() + "..." + currentAppTimeUsingInfo.getPackageName());
        }
    }

    /**
     * 包名切换间隔是否过短
     */
    private boolean isTooShort() {
        return System.currentTimeMillis() - currentAppTimeUsingInfo.getStartTime() < 3000;
    }

    /**
     * 更新currentAppTimeUsingInfo
     */
    private void prepareNewAppTimeUsingInfo(String appName, String packageName) {
        currentAppTimeUsingInfo = new AppTimeUsingInfo(appName, packageName, TimeUtil.getTodayStartTime().getTime(), System.currentTimeMillis(), 0, 0);
    }

    public void destroy() {
//        App.context.stopService(new Intent(App.context, CatchTimeService.class));
        App.context.unregisterReceiver(screenReceiver);
    }

    /**
     * 熄屏广播
     */
    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    // 熄屏时结算当前应用使用时间，并将当前数据置空
                    if (currentAppTimeUsingInfo != null && currentPackageName != null) {
                        countingUsingTime();
                        currentAppTimeUsingInfo = null;
                        currentPackageName = null;
                    }
                }
            }
        }
    }
}
