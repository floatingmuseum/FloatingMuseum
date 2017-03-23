package com.floatingmuseum.androidtest.functions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.functions.analysesystem.AnalyseSystemActivity;
import com.floatingmuseum.androidtest.functions.autoinstall.AutoInstallActivity;
import com.floatingmuseum.androidtest.functions.bluetooth.BluetoothActivity;
import com.floatingmuseum.androidtest.functions.camera.CameraActivity;
import com.floatingmuseum.androidtest.functions.catchtime.CatchTimeActivity;
import com.floatingmuseum.androidtest.functions.communicate.CommunicateActivity;
import com.floatingmuseum.androidtest.functions.download.DownloadActivity;
import com.floatingmuseum.androidtest.functions.exception.ExceptionActivity;
import com.floatingmuseum.androidtest.functions.hotspot.ClientActivity;
import com.floatingmuseum.androidtest.functions.hotspot.HotSpotActivity;
import com.floatingmuseum.androidtest.functions.hotspot.ServerActivity;
import com.floatingmuseum.androidtest.functions.nsd.NsdChatActivity;
import com.floatingmuseum.androidtest.functions.shell.ShellActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class FunctionsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_auto_install)
    Button btAutoInstall;
    @BindView(R.id.bt_shell)
    Button btShell;
    @BindView(R.id.bt_camera)
    Button btCamera;
    @BindView(R.id.bt_exception)
    Button btException;
    @BindView(R.id.bt_download)
    Button btDownload;
    @BindView(R.id.bt_catch_time)
    Button btCatchTime;
    @BindView(R.id.bt_analyse_system)
    Button btAnalyseSystem;
    @BindView(R.id.bt_communicate)
    Button btCommunicate;
    @BindView(R.id.bt_bluetooth)
    Button btBluetooth;
    @BindView(R.id.bt_hot_spot)
    Button btHotSpot;
    @BindView(R.id.bt_server)
    Button btServer;
    @BindView(R.id.bt_client)
    Button btClient;
    @BindView(R.id.bt_nsd)
    Button btNsd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        ButterKnife.bind(this);

        btAutoInstall.setOnClickListener(this);
        btShell.setOnClickListener(this);
        btCamera.setOnClickListener(this);
        btException.setOnClickListener(this);
        btDownload.setOnClickListener(this);
        btCatchTime.setOnClickListener(this);
        btAnalyseSystem.setOnClickListener(this);
        btCommunicate.setOnClickListener(this);
        btBluetooth.setOnClickListener(this);
        btHotSpot.setOnClickListener(this);

        btServer.setOnClickListener(this);
        btClient.setOnClickListener(this);

        btNsd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_auto_install:
                startActivity(AutoInstallActivity.class);
                break;

            case R.id.bt_shell:
                startActivity(ShellActivity.class);
                break;
            case R.id.bt_camera:
                startActivity(CameraActivity.class);
                break;
            case R.id.bt_exception:
                startActivity(ExceptionActivity.class);
                break;
            case R.id.bt_download:
                startActivity(DownloadActivity.class);
                break;
            case R.id.bt_catch_time:
                startActivity(CatchTimeActivity.class);
                break;
            case R.id.bt_analyse_system:
                startActivity(AnalyseSystemActivity.class);
                break;
            case R.id.bt_communicate:
                startActivity(CommunicateActivity.class);
                break;
            case R.id.bt_bluetooth:
                startActivity(BluetoothActivity.class);
                break;
            case R.id.bt_hot_spot:
                startActivity(HotSpotActivity.class);
                break;
            case R.id.bt_server:
                startActivity(ServerActivity.class);
                break;
            case R.id.bt_client:
                startActivity(ClientActivity.class);
                break;
            case R.id.bt_nsd:
                startActivity(NsdChatActivity.class);
                break;
        }
    }
}
