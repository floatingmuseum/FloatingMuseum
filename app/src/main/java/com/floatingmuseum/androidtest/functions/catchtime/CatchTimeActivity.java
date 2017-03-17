package com.floatingmuseum.androidtest.functions.catchtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.TimeUtil;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Date;

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
    }

    private void initCatchTimeService() {
        Intent intent = new Intent(this, CatchTimeService.class);
        startService(intent);
    }
}
