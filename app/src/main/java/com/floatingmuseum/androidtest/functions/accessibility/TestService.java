package com.floatingmuseum.androidtest.functions.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/8/25.
 */

public class TestService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        if (Build.VERSION.SDK_INT < 16) {
//            nodeInfo = event.getSource();
//        } else {
//            nodeInfo = getRootInActiveWindow();
//        }


        AccessibilityNodeInfo target = AccessibilityHelper.findTarget(event, "设备管理器");
        if (target != null) {
            Logger.d("辅助助手...onAccessibilityEvent...:" + target.toString());
        } else {
            Logger.d("辅助助手...onAccessibilityEvent...:" + target);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
