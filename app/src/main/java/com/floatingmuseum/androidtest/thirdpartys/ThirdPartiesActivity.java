package com.floatingmuseum.androidtest.thirdpartys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.thirdpartys.virtualapk.VirtualApkActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class ThirdPartiesActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_virtual_apk)
    Button btVirtualApk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdparties);
        ButterKnife.bind(this);

        btVirtualApk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_virtual_apk:
                startActivity(VirtualApkActivity.class);
                break;
        }
    }
}
