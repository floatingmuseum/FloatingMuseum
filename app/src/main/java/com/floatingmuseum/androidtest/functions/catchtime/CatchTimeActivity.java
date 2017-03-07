package com.floatingmuseum.androidtest.functions.catchtime;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

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
    }
}
