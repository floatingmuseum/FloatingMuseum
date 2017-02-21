package com.floatingmuseum.androidtest.functions.autoinstall;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/16.
 */

public class AutoInstallActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_accessibility_install)
    Button btAccessibilityInstall;
    @BindView(R.id.bt_silent_install)
    Button btSilentInstall;

    public static boolean isActivityDestroyed = true;

    private static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
//                ToastUtil.show(R.string.install_finished);
            }
        }

        ;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_install);
        ButterKnife.bind(this);

        btAccessibilityInstall.setOnClickListener(this);
        btSilentInstall.setOnClickListener(this);
        //设置Activity状态未被销毁，可以执行辅助自动安装
        isActivityDestroyed = false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_accessibility_install:

                break;
            case R.id.bt_silent_install:
                silentInstall();
                break;
        }
    }

    /**
     * 执行具体的静默安装逻辑，需要设备ROOT。
     */
    private void silentInstall() {
        final String apkPath = null;
        if (!isRootSystem()) {
//            ToastUtil.show(R.string.unroot_system);
            return;
        }
        if (TextUtils.isEmpty(apkPath)) {
//            ToastUtil.show(R.string.blank_path);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                DataOutputStream dataOutputStream = null;
                BufferedReader errorStream = null;
                try {
                    // 申请su权限
                    Process process = Runtime.getRuntime().exec("su");
                    dataOutputStream = new DataOutputStream(
                            process.getOutputStream());
                    // 执行pm install命令
                    String command = "pm install -r " + apkPath + "\n";
                    dataOutputStream.write(command.getBytes(Charset
                            .forName("utf-8")));
                    dataOutputStream.flush();
                    dataOutputStream.writeBytes("exit\n");
                    dataOutputStream.flush();
                    process.waitFor();
                    errorStream = new BufferedReader(new InputStreamReader(
                            process.getErrorStream()));
                    String msg = "";
                    String line;
                    // 读取命令的执行结果
                    while ((line = errorStream.readLine()) != null) {
                        msg += line;
                    }
                    // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
                    if (!msg.contains("Failure")) {
                        handler.sendEmptyMessage(0);
                        result = true;
                    }
                } catch (Exception e) {
                } finally {
                    try {
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        if (errorStream != null) {
                            errorStream.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }).start();
    }

    /**
     * 系统是否root
     *
     * @return
     */
    public boolean isRootSystem() {
        boolean bool = false;
        try {
            bool = (!new File("/system/bin/su").exists())
                    || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityDestroyed = true;
    }
}
