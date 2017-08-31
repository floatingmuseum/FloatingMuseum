package com.floatingmuseum.androidtest.functions.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.util.SparseBooleanArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.floatingmuseum.androidtest.App;
import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/8/30.
 */

public class SettingInitManager {

    public static final int SETTINGS_STATE_INSTALL_NON_MARKET_APPS_ENABLED = 0;
    public static final int SETTINGS_STATE_DEVICE_ADMIN_ENABLED = 1;
    public static final int SETTINGS_STATE_DEFAULT_LAUNCHER_ENABLED = 2;
    public static final int SETTINGS_STATE_USAGE_STATS_ENABLED = 3;

    private static SettingInitManager manager;
    private boolean isiInstallNonMarketsAppsEnabled = false;
    private boolean hasUsageStatsPermission = false;
    private boolean isDeviceAdminEnabled = false;
    private boolean isDefaultLauncher = false;

    private SparseBooleanArray settingsState = new SparseBooleanArray();

    private SettingInitManager() {
    }

    public static SettingInitManager getInstance() {
        if (manager == null) {
            synchronized (SettingInitManager.class) {
                if (manager == null) {
                    manager = new SettingInitManager();
                }
            }
        }
        return manager;
    }

    public SparseBooleanArray refreshSettingState() {
        isiInstallNonMarketsAppsEnabled = SettingsChecker.isiInstallNonMarketsAppsEnabled(App.context);
        settingsState.put(SETTINGS_STATE_INSTALL_NON_MARKET_APPS_ENABLED, isiInstallNonMarketsAppsEnabled);
        isDeviceAdminEnabled = SettingsChecker.isDeviceAdminEnabled(App.context);
        settingsState.put(SETTINGS_STATE_DEVICE_ADMIN_ENABLED, isDeviceAdminEnabled);
        isDefaultLauncher = SettingsChecker.isDefaultLauncher(App.context);
        settingsState.put(SETTINGS_STATE_DEFAULT_LAUNCHER_ENABLED, isDefaultLauncher);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            hasUsageStatsPermission = SettingsChecker.hasUsageStatsPermission(App.context);
            settingsState.put(SETTINGS_STATE_USAGE_STATS_ENABLED, hasUsageStatsPermission);
            Logger.d("权限检测:...hasUsageStatsPermission:" + hasUsageStatsPermission);
        }
        Logger.d("权限检测:...isiInstallNonMarketsAppsEnabled:" + isiInstallNonMarketsAppsEnabled + "...isDeviceAdminEnabled:" + isDeviceAdminEnabled + "...isDefaultLauncher:" + isDefaultLauncher);
        return settingsState;
    }

    private SparseBooleanArray usageStatsStage = new SparseBooleanArray();

    public void initUsageStats(AccessibilityService service, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        Logger.d("辅助助手...应用使用情况初始化阶段:...1:" + usageStatsStage.get(0) + "...2:" + usageStatsStage.get(1));
        if (!usageStatsStage.get(0)) {
            AccessibilityNodeInfo target = AccessibilityHelper.searchTarget(nodeInfo, "航智云管控");
            if (target != null && "航智云管控".equals(target.getText())) {
                AccessibilityHelper.doAction(nodeInfo, AccessibilityNodeInfo.ACTION_CLICK);
                usageStatsStage.put(0, true);
            }
        } else if (!usageStatsStage.get(1)) {
            AccessibilityNodeInfo target = AccessibilityHelper.searchTarget(nodeInfo, "确定");
            if (target != null && "确定".equals(target.getText())) {
                AccessibilityHelper.doAction(nodeInfo, AccessibilityNodeInfo.ACTION_CLICK);
                usageStatsStage.put(1, true);
            }
        } else {
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }
}
