package com.floatingmuseum.androidtest.thirdpartys.replugin;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.net.Repository;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/7/4.
 */

public class RePluginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_load_plugin)
    Button btLoadPlugin;
    @BindView(R.id.bt_open_plugin_app)
    Button btOpenPluginApp;

    private boolean isLoadPluginSuccess = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_plugin);
        ButterKnife.bind(this);

        btLoadPlugin.setOnClickListener(this);
        btOpenPluginApp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_load_plugin:
                loadPlugin();
                break;
            case R.id.bt_open_plugin_app:
                startPlugin();
                break;
        }
    }

    private void loadPlugin() {
        String pluginPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().concat("/PluginDemo.apk");
        Logger.d("插件...插件Apk路径:" + pluginPath);
        PluginInfo info = RePlugin.install(pluginPath);
        if (info != null) {
            isLoadPluginSuccess = true;
            Logger.d("插件...插件信息:" + info.toString());
        }
    }

    private void startPlugin() {
        if (isLoadPluginSuccess) {
            boolean isSuccess = RePlugin.startActivity(this, RePlugin.createIntent("RePluginDemo", "floatingmuseum.replugindemo.PluginActivity"));
            Logger.d("插件...插件Activity打开:" + isSuccess);
        } else {
            ToastUtil.show("插件加载失败.");
        }
    }
}
