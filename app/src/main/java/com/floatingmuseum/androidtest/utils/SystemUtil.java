package com.floatingmuseum.androidtest.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;

import com.floatingmuseum.androidtest.App;
import com.floatingmuseum.androidtest.functions.catchtime.CatchTimeAccessibilityService;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Floatingmuseum on 2017/3/13.
 */

public class SystemUtil {

    /**
     * 获取Mac地址
     */
    public static String getMacAddress() {

        Logger.d("系统版本:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                return getMacAddressOnAndroid6Above();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        WifiManager wifiManager = (WifiManager) App.context.getSystemService(Context.WIFI_SERVICE);
        // 获取本机mac物理地址
        String macInfo = wifiManager.getConnectionInfo().getMacAddress();
        System.out.println("本机的mac物理地址为：" + macInfo);
        return macInfo;
    }

    public static String getMacAddressOnAndroid6Above() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iF = interfaces.nextElement();
            byte[] addr = iF.getHardwareAddress();
            if (addr == null || addr.length == 0) {
                continue;
            }
            StringBuilder buf = new StringBuilder();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            String mac = buf.toString();
            Logger.d("系统版本6.0mac" + "...interfaceName=" + iF.getName() + ", mac=" + mac);
            if (iF.getName().equals("wlan0")) {
                return mac;
            }
        }
        return "";
    }

    /**
     * 获取设备上所有应用
     */
    public static List<PackageInfo> queryAllApplication(PackageManager pm) {
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfoList) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                Logger.d("PackageInfo:第三方应用Label:" + applicationInfo.loadLabel(pm).toString() + "...PackageName:" + applicationInfo.packageName + "...flag:" + applicationInfo.flags);
            } else {
                Logger.d("PackageInfo:系统应用Label:" + applicationInfo.loadLabel(pm).toString() + "...PackageName:" + applicationInfo.packageName + "...flag:" + applicationInfo.flags + "...");
            }
        }
        return packageInfoList;
    }

    /**
     * 获取设备上所有系统应用
     */
    public static List<PackageInfo> queryAllSystemApplication(PackageManager pm) {
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<PackageInfo> systemAppList = new ArrayList<>();
        for (PackageInfo info : packageInfoList) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            if (!isSystemApp(applicationInfo)) {
                systemAppList.add(info);
            }
        }
        return systemAppList;
    }

    /**
     * 获取设备上所有第三方应用
     */
    public static List<PackageInfo> queryAllThirdApplication(PackageManager pm) {
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<PackageInfo> thirdAppList = new ArrayList<>();
        for (PackageInfo info : packageInfoList) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            if (!isSystemApp(applicationInfo)) {
                thirdAppList.add(info);
            }
        }
        return thirdAppList;
    }

    /**
     * 获取设备上所有含有Launcher的应用
     */
    public static List<ResolveInfo> queryAllLauncherApplication(PackageManager pm) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allLauncherAppList = pm.queryIntentActivities(mainIntent, 0);
        return allLauncherAppList;
    }

    /**
     * 获取设备上所有含有Launcher的系统应用
     */
    public static List<ResolveInfo> queryAllLauncherSystemApplication(PackageManager pm) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allLauncherAppList = pm.queryIntentActivities(mainIntent, 0);
        List<ResolveInfo> allLauncherSystemAppList = new ArrayList<>();
        for (ResolveInfo info : allLauncherAppList) {
            if (isSystemApp(info.activityInfo.applicationInfo)) {
                allLauncherSystemAppList.add(info);
            }
        }
        return allLauncherSystemAppList;
    }

    /**
     * 获取设备上所有含有Launcher的第三方应用
     */
    public static List<ResolveInfo> queryAllLauncherThirdApplication(PackageManager pm) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allLauncherAppList = pm.queryIntentActivities(mainIntent, 0);
        List<ResolveInfo> allLauncherThirdAppList = new ArrayList<>();
        for (ResolveInfo info : allLauncherAppList) {
            if (!isSystemApp(info.activityInfo.applicationInfo)) {
                allLauncherThirdAppList.add(info);
            }
        }
        return allLauncherThirdAppList;
    }

    /**
     * 是否系统应用
     */
    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void getCurrentLauncher(PackageManager pm) {
//        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
//        filter.addCategory(Intent.CATEGORY_HOME);
//
//        List<IntentFilter> filters = new ArrayList();
//        filters.add(filter);
//
//        List<ComponentName> activities = new ArrayList();
//
//        pm.getPreferredActivities(filters, activities, null);
//
//        for (ComponentName activity : activities) {
//            Logger.d("启动器:" +"..."+activity.getPackageName());
//        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        List<ResolveInfo> infoList1 = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Logger.d("**************************************************启动器**************************************************");
        for (ResolveInfo info : infoList1) {
            Logger.d("启动器:" + info.loadLabel(pm).toString() + "..." + info.activityInfo.packageName);
        }
        Logger.d("**************************************************启动器**************************************************");
        //可以获取到当前的Launcher,如果主屏幕设置没有选中任何一个,则会返回Android
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Logger.d("启动器:" + resolveInfo.loadLabel(pm).toString() + "..." + resolveInfo.activityInfo.packageName);
        Logger.d("**************************************************启动器**************************************************");
    }

    /**
     * 获取栈顶包名
     * @param context
     * @param activityManager
     * @return
     */
    public static String getTopPackageName(Context context, ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            //5.0以后需要用这方法
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            //查询最近10秒内的使用信息
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            long timeStamp = 0;
            String newestPackageName = "";
            String newestClassName = "";
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    //包名...事件类型...类名...时间
                    Logger.d("UsageStats:" + event.getPackageName() + "..." + event.getEventType() + "..." + event.getClassName() + "..." + event.getTimeStamp());
                    result = event.getPackageName();

                    if (timeStamp == 0) {
                        newestPackageName = event.getPackageName();
                        newestClassName = event.getClassName();
                        timeStamp = event.getTimeStamp();
                    } else {
                        if (event.getTimeStamp() > timeStamp) {
                            newestPackageName = event.getPackageName();
                            newestClassName = event.getClassName();
                            timeStamp = event.getTimeStamp();
                        }
                    }
                }
            }
            Logger.d("UsageStats:newestPackageName:" + newestPackageName + "...newestClassName:" + newestClassName);
            Logger.d("UsageStats:**************************************************************************************************************");
            if (!TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    /**
     * 获取栈顶类名
     * @param context
     * @param activityManager
     * @return
     */
    public static String getTopClassName(Context context, ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getClassName();
            }
        } else {
            //5.0以后需要用这方法
            UsageStatsManager usageStatsManager;
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
            } else {
                usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            }
            long endTime = System.currentTimeMillis();
            //查询最近10秒内的使用信息
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
            long timeStamp = 0;
            String newestPackageName = "";
            String newestClassName = "";
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    //包名...事件类型...类名...时间
                    Logger.d("UsageStats:" + event.getPackageName() + "..." + event.getEventType() + "..." + event.getClassName() + "..." + event.getTimeStamp());
                    result = event.getPackageName();

                    if (timeStamp == 0) {
                        newestPackageName = event.getPackageName();
                        newestClassName = event.getClassName();
                        timeStamp = event.getTimeStamp();
                    } else {
                        if (event.getTimeStamp() > timeStamp) {
                            newestPackageName = event.getPackageName();
                            newestClassName = event.getClassName();
                            timeStamp = event.getTimeStamp();
                        }
                    }
                }
            }
            Logger.d("UsageStats:newestPackageName:" + newestPackageName + "...newestClassName:" + newestClassName);
            Logger.d("UsageStats:**************************************************************************************************************");
            if (!TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    /**
     * 服务是否运行
     */
    public static boolean isServiceRunning(Context context, String serviceClassName) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0) return false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(serviceClassName)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 辅助服务是否开启
     */
    public static boolean isAccessibilityServiceEnabled(Context context, String id) {
        AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> results = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : results) {
            if (id.equals(info.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 禁用状态栏下拉
     * 并不完美(只是在下拉状态时执行收合命令)
     */
    private void disableStatusBar(Context context) {
        Object service = context.getSystemService("statusbar");
        Class<?> statusbarManager;
        try {
            statusbarManager = Class.forName("android.app.StatusBarManager");
            if (Build.VERSION.SDK_INT <= 16) {
                try {

                    // Method[] methods=statusbarManager.getMethods();
                    Method test = statusbarManager.getMethod("collapse");
                    test.invoke(service);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {

                    // Method[] methods=statusbarManager.getMethods();
                    Method test = statusbarManager.getMethod("collapsePanels");
                    test.invoke(service);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否横屏
     */
    public static boolean isLandscape() {
        return App.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽px
     */
    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) App.context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return 屏幕高px
     */
    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) App.context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.heightPixels;
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue) {
        final float scale = App.context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp( float pxValue) {
        final float scale = App.context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
