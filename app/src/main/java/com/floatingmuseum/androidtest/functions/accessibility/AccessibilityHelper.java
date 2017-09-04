package com.floatingmuseum.androidtest.functions.accessibility;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/8/25.
 */

public class AccessibilityHelper {

    public static SparseArray<AccessibilityNodeInfo> targetArray = new SparseArray<>();

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

    public static void listAllNode(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            listAllNodeInfo(nodeInfo);
        }
    }

    private static void listAllNodeInfo(AccessibilityNodeInfo nodeInfo) {
        CharSequence cs = nodeInfo.getText();
        if (!TextUtils.isEmpty(cs)) {
            Logger.d("辅助助手...列出全部节点...内容:" + cs.toString() + "..." + nodeInfo.getClassName());
        } else {
            Logger.d("辅助助手...列出全部节点...内容:" + "..." + nodeInfo.getClassName());
        }
        int count = nodeInfo.getChildCount();
        if (count != 0) {
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
                if (childNode != null) {
                    listAllNodeInfo(childNode);
                }
            }
        }
    }

    @Nullable
    public static AccessibilityNodeInfo searchTarget(AccessibilityNodeInfo nodeInfo, String targetText) {
//        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            return recursiveSearch(nodeInfo, targetText);
        }
        return null;
    }

    private static SparseArray targets = new SparseArray();

    @Nullable
    public static AccessibilityNodeInfo searchTarget(AccessibilityNodeInfo nodeInfo, String targetText, SearchParams params) {
//        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            return recursiveSearch(nodeInfo, targetText, params);
        }
        return null;
    }

    @Nullable
    private static AccessibilityNodeInfo recursiveSearch(AccessibilityNodeInfo nodeInfo, String targetText, SearchParams params) {
//        doScroll(nodeInfo);
        // TODO: 2017/8/28 如果需要查询的目标处于需要滑动才可见的底部，则保存一个可滑动的node,以便执行滑动.
        int count = nodeInfo.getChildCount();

        if (count == 0) {
            return matchingTargetNodeText(nodeInfo, targetText, params);
        } else {
            for (int i = 0; i < count; i++) {
                //如果递归查询已经找到目标,则不再继续挖掘,逐层向上返回.
                AccessibilityNodeInfo result = targetArray.get(nodeInfo.getWindowId());
                if (result != null) {
                    return result;
                }

                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                AccessibilityNodeInfo target;
                if (childNodeInfo != null) {
                    target = recursiveSearch(childNodeInfo, targetText);
                    if (target != null) {
                        //如果遍历返回的结果不为Null,则找到目标node,存储node
                        targetArray.clear();
                        Logger.d("辅助助手...matchingTargetNodeText()...存储目标..." + target.getText().toString() + "..." + target.getClassName() + "...ParentNode:" + target.getParent());
                        targetArray.put(nodeInfo.getWindowId(), target);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static AccessibilityNodeInfo recursiveSearch(AccessibilityNodeInfo nodeInfo, String targetText) {
//        doScroll(nodeInfo);
        // TODO: 2017/8/28 如果需要查询的目标处于需要滑动才可见的底部，则保存一个可滑动的node,以便执行滑动.
        int count = nodeInfo.getChildCount();

        if (count == 0) {
            return matchingTargetNodeText(nodeInfo, targetText);
        } else {
            for (int i = 0; i < count; i++) {
                //如果递归查询已经找到目标,则不再继续挖掘,逐层向上返回.
                AccessibilityNodeInfo result = targetArray.get(nodeInfo.getWindowId());
                if (result != null) {
                    return result;
                }

                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                AccessibilityNodeInfo target;
                if (childNodeInfo != null) {
                    target = recursiveSearch(childNodeInfo, targetText);
                    if (target != null) {
                        //如果遍历返回的结果不为Null,则找到目标node,存储node
                        targetArray.clear();
                        Logger.d("辅助助手...matchingTargetNodeText()...存储目标..." + target.getText().toString() + "..." + target.getClassName() + "...ParentNode:" + target.getParent());
                        targetArray.put(nodeInfo.getWindowId(), target);
                    }
                }
            }
        }
        return null;
    }

    private static void doScroll(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            Logger.d("辅助助手...node类名:" + nodeInfo.getClassName());
            if ("android.support.v7.widget.RecyclerView".equals(nodeInfo.getClassName()) || "android.widget.ScrollView".equals(nodeInfo.getClassName())) {
                Logger.d("辅助助手...node类名...执行滑动:");
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (AccessibilityNodeInfo.AccessibilityAction action : nodeInfo.getActionList()) {
                        Logger.d("辅助助手...node类名...所含action:" + action.toString());
                    }
                }
            }
        }
    }

    @Nullable
    private static AccessibilityNodeInfo matchingTargetNodeText(AccessibilityNodeInfo nodeInfo, String targetText, SearchParams params) {
        CharSequence nodeText = nodeInfo.getText();
        Logger.d("辅助助手...matchingTargetNodeText()...节点类名:" + nodeInfo.getClassName() + "...包名:" + nodeInfo.getPackageName() + "...ParentNode:" + nodeInfo.getParent());
        if (!TextUtils.isEmpty(nodeText)) {
            Logger.d("辅助助手...matchingTargetNodeText()..." + nodeText.toString() + "...类名:" + nodeInfo.getClassName() + "...包名:" + nodeInfo.getPackageName() + "...ParentNode:" + nodeInfo.getParent());
            if (matchingTargetParams(nodeInfo, params)) {
                if (targetText.equals(nodeText.toString())) {
                    Logger.d("辅助助手...matchingTargetNodeText()...找到目标..." + nodeText.toString());
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 如果前提条件都满足,再检查最终目标.
     */
    private static boolean matchingTargetParams(AccessibilityNodeInfo nodeInfo, SearchParams params) {
        if (!params.isPreconditionsSatisfied()) {
            params.confirmPrecondition(nodeInfo.getText().toString());
            return false;
        } else if (!params.isPreconditionsWithSameParentSatisfied()) {
            params.confirmPreconditionsWithSameParent(nodeInfo.getText().toString(), nodeInfo);
            return params.isPreconditionsWithSameParentSatisfied();
        }
        return true;
    }

    @Nullable
    private static AccessibilityNodeInfo matchingTargetNodeText(AccessibilityNodeInfo nodeInfo, String targetText) {
        CharSequence nodeText = nodeInfo.getText();
        Logger.d("辅助助手...matchingTargetNodeText()...节点类名:" + nodeInfo.getClassName() + "...包名:" + nodeInfo.getPackageName() + "...ParentNode:" + nodeInfo.getParent());
        if (!TextUtils.isEmpty(nodeText)) {
            Logger.d("辅助助手...matchingTargetNodeText()..." + nodeText.toString() + "...类名:" + nodeInfo.getClassName() + "...包名:" + nodeInfo.getPackageName() + "...ParentNode:" + nodeInfo.getParent());
            if (targetText.equals(nodeText.toString())) {
                Logger.d("辅助助手...matchingTargetNodeText()...找到目标..." + nodeText.toString());
                return nodeInfo;
            }
        }
        return null;
    }

    public static void doAction(AccessibilityNodeInfo nodeInfo, int action) {
        try {
            recursiveDoAction(nodeInfo, action);
        } catch (FinishActionException e) {
            Logger.d("辅助助手...doAction...执行Action:抛异常确认");
            e.printStackTrace();
        }
    }

    private static void recursiveDoAction(AccessibilityNodeInfo nodeInfo, int action) throws FinishActionException {
        /**
         * 如果想执行点击操作,但是当前节点不存在此操作,则向上查找父控件是否存在点击操作.
         */
        if (nodeInfo == null) {
            Logger.d("辅助助手...doAction...nodeInfo:" + nodeInfo);
            return;
        } else {
            Logger.d("辅助助手...doAction...nodeInfo:" + nodeInfo.toString() + "...ParentNode:" + nodeInfo.getParent());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<AccessibilityNodeInfo.AccessibilityAction> actionList = nodeInfo.getActionList();
            for (AccessibilityNodeInfo.AccessibilityAction accessibilityAction : actionList) {
                Logger.d("辅助助手...doAction...可执行Action:" + accessibilityAction.toString() + "..." + action);
                if (accessibilityAction.getId() == action) {
                    Logger.d("辅助助手...doAction...执行Action:" + accessibilityAction.toString());
                    nodeInfo.performAction(action);
                    throw new FinishActionException();
                }
            }
            recursiveDoAction(nodeInfo.getParent(), action);
        } else {
            // TODO: 2017/8/28 未测试5.0以下
            int actions = nodeInfo.getActions();
            Logger.d("辅助助手...doAction...可执行Actions:" + actions + "..." + action);
            if (actions == action) {
                Logger.d("辅助助手...doAction...执行Actions:" + actions);
                nodeInfo.performAction(action);
                throw new FinishActionException();
            }
            recursiveDoAction(nodeInfo.getParent(), action);
        }
    }
}
