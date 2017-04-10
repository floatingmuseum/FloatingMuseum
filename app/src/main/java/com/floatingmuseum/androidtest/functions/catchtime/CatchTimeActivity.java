package com.floatingmuseum.androidtest.functions.catchtime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ListUtil;
import com.floatingmuseum.androidtest.utils.RealmManager;
import com.floatingmuseum.androidtest.utils.TimeUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * Created by Floatingmuseum on 2017/3/7.
 * <p>
 * 计算应用使用时间，设备开机时长
 */

public class CatchTimeActivity extends BaseActivity {

    @BindView(R.id.rv_app_using_time)
    RecyclerView rvAppUsingTime;

    private Map<String, Long> appTotalTime = new HashMap<>();
    private List<AppTimeInfo> showingList = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_time);
        ButterKnife.bind(this);
        Logger.d("CatchTimeActivity...今日起始时间:" + TimeUtil.getTodayStartTime().toString() + "...毫秒:" + TimeUtil.getTodayStartTime().getTime());
        Logger.d("CatchTimeActivity...今日结束时间:" + TimeUtil.getTodayEndTime().toString() + "...毫秒:" + TimeUtil.getTodayEndTime().getTime());
//        initCatchTimeService();
        queryAppUsingInfo();
        initView();
//        getHistoryApps();
    }

    private void queryAppUsingInfo() {
        RealmResults<AppTimeUsingInfo> infoList = (RealmResults<AppTimeUsingInfo>) RealmManager.query(AppTimeUsingInfo.class);

//        for (AppTimeUsingInfo info : infoList) {
//            Logger.d("CatchTimeActivity...应用名:" + info.getAppName() + "...包名:" + info.getPackageName() + "...起始时间:" + info.getStartTime() + "...结束时间:" + info.getEndTime() + "...使用时间:" + info.getUsingTime() + "...当天起始时间:" + info.getDayStartTime());
//        }
        Logger.d("CatchTimeActivity...应用统计*****************************总数据量:" + infoList.size() + "*****************************");
        List<List<AppTimeUsingInfo>> after = ListUtil.subList(infoList);
        for (List<AppTimeUsingInfo> list : after) {
            Logger.d("CatchTimeActivity...应用统计*****************************日起始时间:" + list.get(0).getDayStartTime() + "*****************************");
            for (AppTimeUsingInfo info : list) {
                if (appTotalTime.containsKey(info.getAppName())) {
                    long time = appTotalTime.get(info.getAppName());
                    time += info.getUsingTime();
                    appTotalTime.put(info.getAppName(), time);
                } else {
                    appTotalTime.put(info.getAppName(), info.getUsingTime());
                }
                Logger.d("CatchTimeActivity...应用统计...应用名:" + info.getAppName() + "...包名:" + info.getPackageName() + "...起始时间:" + info.getStartTime() + "...结束时间:" + info.getEndTime() + "...使用时间:" + info.getUsingTime() + "...当天起始时间:" + info.getDayStartTime());
                madeList(info);
            }
        }
        Logger.d("CatchTimeActivity...应用统计*****************************总使用时间*****************************");
        for (String appName : appTotalTime.keySet()) {
            Logger.d("CatchTimeActivity...应用统计...应用名:" + appName + "...总使用时间:" + appTotalTime.get(appName));
        }
    }

    private void madeList(AppTimeUsingInfo info) {
        if (showingList.size() == 0) {
            showingList.add(new AppTimeInfo(info.getAppName(), info.getPackageName(), info.getDayStartTime(), info.getUsingTime()));
        }
        boolean hasIt = false;
        for (AppTimeInfo appTimeInfo : showingList) {
            if (appTimeInfo.getDayStartTime() == info.getDayStartTime() && appTimeInfo.getPackageName().equals(info.getPackageName())) {
                long usingTime = appTimeInfo.getUsingTime();
                appTimeInfo.setUsingTime(usingTime + info.getUsingTime());
                hasIt = true;
                break;
            }
        }
        if (!hasIt) {
            showingList.add(new AppTimeInfo(info.getAppName(), info.getPackageName(), info.getDayStartTime(), info.getUsingTime()));
        }
    }

    private void initView() {
        Logger.d("CatchTimeActivity...initView()...:" + showingList.size());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvAppUsingTime.setLayoutManager(manager);
        CatchTimeAdapter adapter = new CatchTimeAdapter(showingList);
        rvAppUsingTime.setAdapter(adapter);
        rvAppUsingTime.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        adapter.notifyDataSetChanged();
    }

    /**
     * 没深入了解
     */
    private void getHistoryApps() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> list = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);
            Logger.d("应用使用情况:" + list);
            Logger.d("应用使用情况:" + list.size());
            if (!ListUtil.isEmpty(list)) {
                for (UsageStats stats : list) {
                    Logger.d("应用使用情况:" + stats.getPackageName()
                            + "...firstStamp:" + new Date(stats.getFirstTimeStamp()).toString()
                            + "...lastStamp:" + new Date(stats.getLastTimeStamp()).toString()
                            + "...lastUsed:" + new Date(stats.getLastTimeUsed()).toString()
                            + "...TotalForeground:" + TimeUnit.MILLISECONDS.toMinutes(stats.getTotalTimeInForeground()));
                }
            }
        }
    }

    private void initCatchTimeService() {
        Intent intent = new Intent(this, CatchTimeService.class);
        startService(intent);
    }
}
