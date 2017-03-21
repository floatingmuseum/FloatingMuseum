package com.floatingmuseum.androidtest.functions.catchtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ListUtil;
import com.floatingmuseum.androidtest.utils.RealmManager;
import com.floatingmuseum.androidtest.utils.TimeUtil;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Floatingmuseum on 2017/3/7.
 * <p>
 * 计算应用使用时间，设备开机时长
 */

public class CatchTimeActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_time);
        Logger.d("CatchTimeActivity...今日起始时间:" + TimeUtil.getTodayStartTime().toString() + "...毫秒:" + TimeUtil.getTodayStartTime().getTime());
        Logger.d("CatchTimeActivity...今日结束时间:" + TimeUtil.getTodayEndTime().toString() + "...毫秒:" + TimeUtil.getTodayEndTime().getTime());
//        initCatchTimeService();
        queryAppUsingInfo();
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
                Logger.d("CatchTimeActivity...应用统计...应用名:" + info.getAppName() + "...包名:" + info.getPackageName() + "...起始时间:" + info.getStartTime() + "...结束时间:" + info.getEndTime() + "...使用时间:" + info.getUsingTime() + "...当天起始时间:" + info.getDayStartTime());
            }
        }
    }

    private void initCatchTimeService() {
        Intent intent = new Intent(this, CatchTimeService.class);
        startService(intent);
    }
}
