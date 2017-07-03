package com.floatingmuseum.androidtest;

import android.app.ActivityManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.functions.FunctionsActivity;
import com.floatingmuseum.androidtest.thirdpartys.ThirdPartiesActivity;
import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.floatingmuseum.androidtest.views.ViewActivity;
import com.orhanobut.logger.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_views)
    Button btViews;
    @BindView(R.id.bt_functions)
    Button btFunctions;
    @BindView(R.id.bt_third_parties)
    Button btThirdParties;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btViews.setOnClickListener(this);
        btFunctions.setOnClickListener(this);
        btThirdParties.setOnClickListener(this);


//        List<ResolveInfo> systemAppList = SystemUtil.queryAllLauncherSystemApplication(getPackageManager());
//        Logger.d("******************************************* Launcher应用...系统应用 *******************************************");
//        for (ResolveInfo info : systemAppList) {
//            Logger.d("Launcher应用...应用名:" + info.activityInfo.loadLabel(getPackageManager()).toString() + "...包名:" + info.activityInfo.packageName);
//        }
//        List<ResolveInfo> thirdAppList = SystemUtil.queryAllLauncherThirdApplication(getPackageManager());
//        Logger.d("手机应用******************************************* Launcher应用...第三方应用 *******************************************");
//        for (ResolveInfo info : thirdAppList) {
//            Logger.d("Launcher应用...应用名:" + info.activityInfo.loadLabel(getPackageManager()).toString() + "...包名:" + info.activityInfo.packageName);
//        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_views:
                startActivity(ViewActivity.class);
                break;
            case R.id.bt_functions:
                startActivity(FunctionsActivity.class);
                break;
            case R.id.bt_third_parties:
                startActivity(ThirdPartiesActivity.class);
//                int x = 1 / 0;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
