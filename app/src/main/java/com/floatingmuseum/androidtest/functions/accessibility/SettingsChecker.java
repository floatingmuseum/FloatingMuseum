package com.floatingmuseum.androidtest.functions.accessibility;

import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/8/29.
 */

public class SettingsChecker {

    public static boolean isDefaultLauncher(Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        Logger.d("权限检测...isDefaultLauncher()..." + str);
        return "com.example.edcationcloud".equals(str);
    }

    public static boolean isDeviceAdminEnabled(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        List<ComponentName> activeAdmins = manager.getActiveAdmins();
        if (activeAdmins != null) {
            for (ComponentName admin : activeAdmins) {
                Logger.d("权限检测...isDeviceManagerEnabled()..." + admin.toString());
                if ("com.example.edcationcloud".equals(admin.getPackageName())) {
                    Logger.d("权限检测...isDeviceManagerEnabled()..." + admin.toString());
                    return true;
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager Manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = Manager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * 安装未知来源的应用开关是否打开
     */
    public static boolean isiInstallNonMarketsAppsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            try {
                int installNonMarketAppsEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS);
                return installNonMarketAppsEnabled == 1;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean isInstallFromNonMarketsEnabled(Context context) {
//        final String service = context.getPackageName() + "/" + serviceName;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                int lockPatternEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.LOCK_PATTERN_ENABLED);
                Logger.v("辅助助手...isEnabled()...lockPatternEnabled = " + lockPatternEnabled);
            }
            int installNonMarketAppsEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS);
            Logger.v("辅助助手...isEnabled()...installFromNonMarkets = " + installNonMarketAppsEnabled);
            String defaultInputMethod = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
            Logger.v("辅助助手...isEnabled()...defaultInputMethod = " + defaultInputMethod);
            String enabledInputMethods = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS);
            Logger.v("辅助助手...isEnabled()...enabledInputMethods = " + enabledInputMethods);
//            int inputMethodSelectorVisibility = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.INPUT_METHOD_SELECTOR_VISIBILITY);
//            Logger.v("辅助助手...isEnabled()...inputMethodSelectorVisibility = " + inputMethodSelectorVisibility);
//            int selectedInputMethodSubtype = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE);
//            Logger.v("辅助助手...isEnabled()...selectedInputMethodSubtype = " + selectedInputMethodSubtype);

//            Settings.Global;
//            Settings.System;
        } catch (Settings.SettingNotFoundException e) {
            Logger.e("辅助助手...isEnabled()...Error finding setting, default installFromNonMarkets to not found: "
                    + e.getMessage());
        }
//        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
//
//        if (enabled == 1) {
//            Logger.v("辅助助手...isEnabled()...***Install From Non-Markets IS ENABLED*** -----------------");
//            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
//                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//            if (settingValue != null) {
//                mStringColonSplitter.setString(settingValue);
//                while (mStringColonSplitter.hasNext()) {
//                    String accessibilityService = mStringColonSplitter.next();
//
//                    Logger.v("辅助助手...isEnabled()...-------------- > accessibilityService :: " + accessibilityService + " " + service);
//                    if (accessibilityService.equalsIgnoreCase(service)) {
//                        Logger.v("辅助助手...isEnabled()...We've found the correct setting - accessibility is switched on!");
//                        return true;
//                    }
//                }
//            }
//        } else {
//            Logger.v("辅助助手...isEnabled()...***ACCESSIBILITY IS DISABLED***");
//        }
        return false;
    }
}
