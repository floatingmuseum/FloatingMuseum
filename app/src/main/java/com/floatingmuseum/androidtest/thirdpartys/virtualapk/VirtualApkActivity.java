package com.floatingmuseum.androidtest.thirdpartys.virtualapk;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/7/3.
 */

public class VirtualApkActivity extends BaseActivity {

    @BindView(R.id.bt_load_plugin)
    Button btLoadPlugin;
    @BindView(R.id.bt_open_plugin)
    Button btOpenPlugin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_apk);
        ButterKnife.bind(this);

        btLoadPlugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlugin();
            }
        });

        btOpenPlugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPluginActivity();
            }
        });
    }

    private void loadPlugin() {
        String pluginPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().concat("/Test.apk");
        Logger.d("VirtualApkActivity...文件地址:" + pluginPath);
        File plugin = new File(pluginPath);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPluginActivity() {
//        Intent intent = new Intent();
//        intent.setClassName("floatingmuseum.plugintest", "floatingmuseum.plugintest.PluginActivity");
//        try {
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
