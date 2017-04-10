package com.floatingmuseum.androidtest.functions.catchtime;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

import com.floatingmuseum.androidtest.utils.RealmManager;
import com.floatingmuseum.androidtest.utils.TimeUtil;
import com.orhanobut.logger.Logger;


/**
 * Created by Floatingmuseum on 2017/3/17.
 */

public class CatchTimeAccessibilityService extends AccessibilityService {

    private String currentPackageName;
    private AppTimeUsingInfo currentAppTimeUsingInfo;
    private ScreenReceiver screenReceiver;

    @Override
    public void onCreate() {
        Logger.d("CatchTimeAccessibilityService...onCreate");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // TODO: 2017/4/10 重启时这里联网出错，还是放到activity里吧 
        startService(new Intent(this, CatchTimeService.class));
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);//亮屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);//熄屏
        filter.addAction(Intent.ACTION_USER_PRESENT);//解锁
        screenReceiver = new ScreenReceiver();
        registerReceiver(screenReceiver, filter);
        Logger.d("CatchTimeAccessibilityService...onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO: 2017/3/30 当屏幕熄灭时，应该结算当前应用的使用时间，屏幕点亮时开始下一个应用计时 
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
            // TODO: 2017/4/11 重启时这里为null 
            Logger.d("CatchTimeAccessibilityService...ActivityInfo:" + info);
            for (ActivityInfo activityInfo : info) {
//                Logger.d("CatchTimeAccessibilityService...包名:" + newPackageName + "...所含类名:" + activityInfo.name + "..." + activityInfo.toString());
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
            if (currentPackageName != null) {//first time
                countingUsingTime();
            }
            prepareNewAppTimeUsingInfo(appName, newPackageName);
            Logger.d("CatchTimeAccessibilityService...当前应用名:" + appName + "..." + TimeUtil.getTodayStartTime().getTime() + "..." + TimeUtil.getTodayStartTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentPackageName = newPackageName;
    }

    private void countingUsingTime() {
        if (!isTooShort()) {
            Logger.d("CatchTimeAccessibilityService...时间充足...进行计算应用名:" + currentAppTimeUsingInfo.getAppName() + "..." + currentAppTimeUsingInfo.getPackageName());
            //结算数据
            long endTime = System.currentTimeMillis();
            long usingTime = endTime - currentAppTimeUsingInfo.getStartTime();
            currentAppTimeUsingInfo.setEndTime(endTime);
            currentAppTimeUsingInfo.setUsingTime(usingTime);
            //插入到数据库
            RealmManager.insertOrUpdate(currentAppTimeUsingInfo);
        } else {
            Logger.d("CatchTimeAccessibilityService...间隔过短...忽略计算应用名:" + currentAppTimeUsingInfo.getAppName() + "..." + currentAppTimeUsingInfo.getPackageName());
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

    @Override
    public void onInterrupt() {
        Logger.d("CatchTimeAccessibilityService...onInterrupt");
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, CatchTimeService.class));
        unregisterReceiver(screenReceiver);
        Logger.d("CatchTimeAccessibilityService...onDestroy");
    }

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Intent.ACTION_SCREEN_ON:
                        Logger.d("CatchTimeAccessibilityService:亮屏广播");
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        // 熄屏时结算当前应用使用时间，并将当前数据置空
                        countingUsingTime();
                        currentAppTimeUsingInfo = null;
                        currentPackageName = null;
                        Logger.d("CatchTimeAccessibilityService:熄屏广播");
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        Logger.d("CatchTimeAccessibilityService:解锁广播");
                        break;
                }
            }
        }
    }
}
