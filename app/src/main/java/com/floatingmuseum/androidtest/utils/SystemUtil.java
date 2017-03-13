package com.floatingmuseum.androidtest.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.floatingmuseum.androidtest.App;
import com.orhanobut.logger.Logger;

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
}
