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

    @Override
    public void onCreate() {
        Logger.d("CatchTimeAccessibilityService...onCreate");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Logger.d("CatchTimeAccessibilityService...onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AppTimeUsingManager.getInstance().countingAppUsingTime(event);
    }

    @Override
    public void onInterrupt() {
        Logger.d("CatchTimeAccessibilityService...onInterrupt");
    }

    @Override
    public void onDestroy() {
        AppTimeUsingManager.getInstance().destroy();
        Logger.d("CatchTimeAccessibilityService...onDestroy");
    }
}
