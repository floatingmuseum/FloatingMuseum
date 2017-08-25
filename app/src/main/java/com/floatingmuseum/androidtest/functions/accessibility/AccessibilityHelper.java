package com.floatingmuseum.androidtest.functions.accessibility;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/8/25.
 */

public class AccessibilityHelper {

    public static boolean isEnabled(Context context, String serviceName) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + serviceName;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Logger.v("辅助助手...isEnabled()...accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Logger.e("辅助助手...isEnabled()...Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Logger.v("辅助助手...isEnabled()...***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Logger.v("辅助助手...isEnabled()...-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Logger.v("辅助助手...isEnabled()...We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Logger.v("辅助助手...isEnabled()...***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    @Nullable
    public static AccessibilityNodeInfo findTarget(AccessibilityEvent event, String targetText) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            return recursiveFind(nodeInfo, targetText);
        }
        return null;
    }

    @Nullable
    private static AccessibilityNodeInfo recursiveFind(AccessibilityNodeInfo nodeInfo, String targetText) {
        int count = nodeInfo.getChildCount();
        if (count == 0) {
            return matchingNodeText(nodeInfo, targetText);
        } else {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (childNodeInfo != null) {
                    return recursiveFind(childNodeInfo, targetText);
                }
            }
        }
        return nodeInfo;
    }

    @Nullable
    private static AccessibilityNodeInfo matchingNodeText(AccessibilityNodeInfo nodeInfo, String targetText) {
        CharSequence nodeText = nodeInfo.getText();
        if (!TextUtils.isEmpty(nodeText)) {
            Logger.d("辅助助手...matchingNodeText()..." + nodeText.toString() + "...类名:" + nodeInfo.getClassName() + "...包名:" + nodeInfo.getPackageName());
            if (targetText.equals(nodeText.toString())) {
                Logger.d("辅助助手...matchingNodeText()...找到目标..." + nodeText.toString());
                return nodeInfo;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    List<AccessibilityNodeInfo.AccessibilityAction> actions = nodeInfo.getActionList();
//                    Logger.d("辅助助手...matchingNodeText()...目标ActionSize..." + actions.size());
//                    for (AccessibilityNodeInfo.AccessibilityAction action : actions) {
//                        Logger.d("辅助助手...matchingNodeText()...目标Action..." + action.toString());
//                        if (AccessibilityNodeInfo.ACTION_CLICK == action.getId()) {
//                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            return;
//                        }
//                    }
//
//                    AccessibilityNodeInfo parentNodeInfo = nodeInfo.getParent();
//                    List<AccessibilityNodeInfo.AccessibilityAction> parentActions = parentNodeInfo.getActionList();
//                    Logger.d("辅助助手...matchingNodeText()...目标ParentActionSize..." + parentActions.size());
//                    for (AccessibilityNodeInfo.AccessibilityAction action : parentActions) {
//                        Logger.d("辅助助手...matchingNodeText()...目标ParentActionSize..." + action.toString());
//                        if (AccessibilityNodeInfo.ACTION_CLICK == action.getId()) {
//                            parentNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            return;
//                        }
//                    }
//                }
            }
        }
        return null;
    }
}
