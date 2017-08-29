package com.floatingmuseum.androidtest.functions.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.util.SparseBooleanArray;
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

    SparseBooleanArray handledArray = new SparseBooleanArray();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String targetText = "设备管理器";
//        String targetText = "有权查看使用情况的应用";

//        AccessibilityHelper.listAllNode(event.getSource());
//        AccessibilityHelper.listAllNode(getRootInActiveWindow());
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                    || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                    || eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                if (!handledArray.get(event.getWindowId())) {
                    Logger.d("辅助助手...onAccessibilityEvent...接收事件:" + event.getWindowId());
                    AccessibilityNodeInfo target = AccessibilityHelper.searchTarget(getRootInActiveWindow(), targetText);
                    if (target != null && targetText.equals(target.getText())) {
                        Logger.d("辅助助手...onAccessibilityEvent...事件查询结果:" + target.getWindowId() + target.getText() + "...详情:" + target.toString());
                        handledArray.put(event.getWindowId(), true);
                        AccessibilityHelper.doAction(target, AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        Logger.d("辅助助手...onAccessibilityEvent...事件查询结果:" + event.getWindowId() + "...null");
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
