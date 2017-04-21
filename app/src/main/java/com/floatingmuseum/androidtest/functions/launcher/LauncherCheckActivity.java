package com.floatingmuseum.androidtest.functions.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/4/21.
 */

public class LauncherCheckActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_start_check)
    Button btStartCheck;
    @BindView(R.id.bt_stop_check)
    Button btStopCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_check);
        ButterKnife.bind(this);

        btStartCheck.setOnClickListener(this);
        btStopCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_check:
                startService(new Intent(this, LauncherCheckService.class));
                break;
            case R.id.bt_stop_check:
                stopService(new Intent(this, LauncherCheckService.class));
                break;
        }
    }
}
