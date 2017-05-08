package com.floatingmuseum.androidtest.functions.phoenixservice;

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
 * Created by Floatingmuseum on 2017/5/7.
 */

public class PhoenixActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_phoenix_start)
    Button btPhoenixStart;
    @BindView(R.id.bt_destroy_app)
    Button btDestroyApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoenix);
        ButterKnife.bind(this);

        btPhoenixStart.setOnClickListener(this);
        btDestroyApp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_phoenix_start:
                startService(new Intent(PhoenixActivity.this,PhoenixService.class));
                break;
            case R.id.bt_destroy_app:
                stopApp();
//                stopService(new Intent(PhoenixActivity.this,PhoenixService.class));
//                System.exit(0);
//                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }
}
