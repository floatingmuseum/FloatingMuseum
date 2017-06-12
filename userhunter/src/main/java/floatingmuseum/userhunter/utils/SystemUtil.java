package floatingmuseum.userhunter.utils;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/6/12.
 */

public class SystemUtil {
    /**
     * 获取顶部包名和类名
     */
    public static String[] getTopPackageNameClassName(Context context, ActivityManager activityManager) {
        String[] packageNameAndClassName = new String[2];
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                ComponentName topActivity = appTasks.get(0).topActivity;
//                Logger.d("栈顶信息:包名:" + topActivity.getPackageName() + "..." + topActivity.getClassName() + "..." + topActivity.toString() + "..." + packageNameAndClassName.length);
                packageNameAndClassName[0] = topActivity.getPackageName();
                packageNameAndClassName[1] = topActivity.getClassName();
                return packageNameAndClassName;
            }
        } else {
            //5.0以后需要用这方法
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            long timeStamp = 0;
            boolean hasInfo = false;
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    if (timeStamp == 0) {
                        packageNameAndClassName[0] = event.getPackageName();
                        packageNameAndClassName[1] = event.getClassName();
                        timeStamp = event.getTimeStamp();
                    } else {
                        if (event.getTimeStamp() > timeStamp) {
                            packageNameAndClassName[0] = event.getPackageName();
                            packageNameAndClassName[1] = event.getClassName();
                            timeStamp = event.getTimeStamp();
                        }
                    }
                    hasInfo = true;
                }
            }
            if (hasInfo) {
                return packageNameAndClassName;
            }
        }
        return null;
    }
}
