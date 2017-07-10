package com.floatingmuseum.androidtest.thirdpartys.replugin;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.component.service.PluginServiceClient;
import com.qihoo360.replugin.model.PluginInfo;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/7/4.
 */

public class RePluginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_install_plugin)
    Button btInstallPlugin;
    @BindView(R.id.tv_plugin_state)
    TextView tvPluginState;
    @BindView(R.id.bt_open_plugin_activity)
    Button btOpenPluginActivity;
    @BindView(R.id.bt_open_plugin_service)
    Button btOpenPluginService;

    private boolean isLoadPluginSuccess = false;
    private boolean isPluginApkExist = false;
    private String pluginPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_plugin);
        ButterKnife.bind(this);

        btInstallPlugin.setOnClickListener(this);
        btOpenPluginActivity.setOnClickListener(this);
        btOpenPluginService.setOnClickListener(this);
        checkPluginState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_install_plugin:
                installPlugin();
                break;
            case R.id.bt_open_plugin_activity:
                startPluginActivity();
                break;
            case R.id.bt_open_plugin_service:
                startPluginService();
        }
    }

    private void checkPluginState() {
        pluginPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Plugins/PluginDemo.apk");
//        pluginPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().concat("/PluginDemo.apk");
        File pluginFile = new File(pluginPath);
        boolean isPluginInstalled = RePlugin.isPluginInstalled("PluginDemo");
        Logger.d("插件...插件状态...isPluginInstalled:"+isPluginInstalled+"...isPluginFileExists:"+pluginFile.exists());
        if (isPluginInstalled) {
            if (pluginFile.exists()) {//已安装插件有新版本更新
                isLoadPluginSuccess = true;
                isPluginApkExist = true;
                tvPluginState.setText("Plugin State:There is new version of PluginDemo");
            } else {//插件已安装
                isLoadPluginSuccess = true;
                tvPluginState.setText("Plugin State:Load success");
            }
        } else {
            if (pluginFile.exists()) {//插件未安装
                isPluginApkExist = true;
                tvPluginState.setText("Plugin State:None of plugin are loaded");
            } else {//未找到插件Apk
                tvPluginState.setText("Plugin State:Found nothing");
            }
        }
    }

    private void installPlugin() {
        if (isPluginApkExist) {
            PluginInfo info = RePlugin.install(pluginPath);
            Logger.d("插件...插件信息:" + info);
            if (info != null) {
                boolean isPreloadSuccess = RePlugin.preload(info);
                isLoadPluginSuccess = true;
                tvPluginState.setText("Plugin State:Load success");
                Logger.d("插件...插件信息:" + info.toString() + "...isPreloadSuccess:" + isPreloadSuccess);
            } else {
                tvPluginState.setText("Plugin State:Load failed");
            }
        } else {
            ToastUtil.show("No plugin apk found.");
        }
    }

    private void startPluginActivity() {
        if (isLoadPluginSuccess) {
            boolean isSuccess = RePlugin.startActivity(this, RePlugin.createIntent("PluginDemo", "floatingmuseum.replugindemo.PluginActivity"));
            Logger.d("插件...插件Activity打开:" + isSuccess);
        } else {
            ToastUtil.show("请先加载插件.");
        }
    }

    private void startPluginService() {
        if (isLoadPluginSuccess) {
            PluginServiceClient.startService(this, RePlugin.createIntent("PluginDemo", "PluginService"));
        } else {
            ToastUtil.show("请先加载插件.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PluginServiceClient.stopService(this, RePlugin.createIntent("PluginDemo", "PluginService"));
    }
}
