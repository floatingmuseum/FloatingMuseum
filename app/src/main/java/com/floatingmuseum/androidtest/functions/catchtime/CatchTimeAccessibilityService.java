package com.floatingmuseum.androidtest.functions.catchtime;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.accessibility.AccessibilityEvent;

import com.orhanobut.logger.Logger;


/**
 * Created by Floatingmuseum on 2017/3/17.
 */

public class CatchTimeAccessibilityService extends AccessibilityService {

    private String currentPackageName;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        startService(new Intent(this, CatchTimeService.class));
        Logger.d("CatchTimeAccessibilityService...onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence csPackageName = event.getPackageName();
        Logger.d("CatchTimeAccessibilityService...当前包名:" + csPackageName);
        String newPackageName;
        if (csPackageName != null) {
            newPackageName = csPackageName.toString();
            if (!newPackageName.equals(currentPackageName)) {
                currentPackageName = newPackageName;
            }
            Logger.d("CatchTimeAccessibilityService...当前包名:" + currentPackageName + "...类名:" + event.getClassName());
        }
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
