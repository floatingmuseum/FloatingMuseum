package com.floatingmuseum.androidtest.functions.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
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
//        String targetText = "航智云管控";
//        String targetText = "有权查看使用情况的应用";
//        performGlobalAction(GLOBAL_ACTION_BACK);

//        AccessibilityHelper.listAllNode(event.getSource());
//        AccessibilityHelper.listAllNode(getRootInActiveWindow());
//        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//        CharSequence csPackageName = event.getPackageName();
//        CharSequence csClassName = event.getClassName();
//
//        String packageName = null;
//        String className = null;
//        if (!TextUtils.isEmpty(csPackageName)){
//            packageName = csPackageName.toString();
//        }
//        if (!TextUtils.isEmpty(csClassName)){
//            className = csClassName.toString();
//        }
//
//        Logger.d("辅助助手...onAccessibilityEvent...**********开始**********包名:"+packageName+"...类名:"+className+"..."+event.toString());
//        if (nodeInfo != null) {
//            int eventType = event.getEventType();
//            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//                    || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//                    || eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//                if (!handledArray.get(event.getWindowId())) {
//                    Logger.d("辅助助手...onAccessibilityEvent...接收事件:" + event.getWindowId());
//                    AccessibilityNodeInfo target = AccessibilityHelper.searchTarget(getRootInActiveWindow(), targetText);
//                    if (target != null && targetText.equals(target.getText())) {
//                        Logger.d("辅助助手...onAccessibilityEvent...事件查询结果:" + target.getWindowId() + target.getText() + "...详情:" + target.toString());
//                        handledArray.put(event.getWindowId(), true);
//                        AccessibilityHelper.doAction(target, AccessibilityNodeInfo.ACTION_CLICK);
//                    } else {
//                        Logger.d("辅助助手...onAccessibilityEvent...事件查询结果:" + event.getWindowId() + "...null");
//                    }
//                }
//            }
//        }
//        Logger.d("辅助助手...onAccessibilityEvent...**********结束**********");
        SettingInitManager.getInstance().initUsageStats(this,getRootInActiveWindow());
    }

    @Override
    public void onInterrupt() {

    }
}
