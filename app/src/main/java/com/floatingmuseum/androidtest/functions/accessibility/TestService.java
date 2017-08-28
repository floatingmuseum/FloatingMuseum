package com.floatingmuseum.androidtest.functions.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/8/25.
 */

public class TestService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    SparseBooleanArray handledArray = new SparseBooleanArray();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        if (Build.VERSION.SDK_INT < 16) {
//            nodeInfo = event.getSource();
//        } else {
//            nodeInfo = getRootInActiveWindow();
//        }
        String targetText = "设备管理器";
        Logger.d("辅助助手...onAccessibilityEvent...接收事件:" + event.getWindowId() + "...已处理:" + handledArray.get(event.getWindowId()));
        if (!handledArray.get(event.getWindowId())) {
            Logger.d("辅助助手...onAccessibilityEvent...接收事件:" + event.getWindowId());
            AccessibilityNodeInfo target = AccessibilityHelper.serachTarget(event, targetText);
            if (target != null && targetText.equals(target.getText())) {
                Logger.d("辅助助手...onAccessibilityEvent...事件查询结果:" + target.getWindowId() + target.getText() + "...详情:" + target.toString());
                handledArray.put(event.getWindowId(), true);
            } else {
                Logger.d("辅助助手...onAccessibilityEvent...事件查询结果:" + event.getWindowId() + "...null");
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
