package com.floatingmuseum.androidtest.functions.aidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.floatingmuseum.androidtest.App;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import floatingmuseum.userhunter.RemoteHunter;


/**
 * Created by Floatingmuseum on 2017/6/11.
 */

public class AidlActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_send_message)
    Button btSendMessage;
    @BindView(R.id.ll_log_container)
    LinearLayout llLogContainer;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    @BindView(R.id.bt_bind_remote_hunter)
    Button btBindRemoteHunter;
    @BindView(R.id.bt_start_remote_hunter_activity)
    Button btStartRemoteHunterActivity;

    private RemoteHunter remoteHunter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        ButterKnife.bind(this);

        btBindRemoteHunter.setOnClickListener(this);
        btSendMessage.setOnClickListener(this);
        btStartRemoteHunterActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_remote_hunter_activity:
//                Intent intent = getLauncherIntent("floatingmuseum.userhunter");
//                startActivity(intent);
                AidlService.sendCommand(AidlService.ACTION_START_REMOTE_ACTIVITY);
            case R.id.bt_bind_remote_hunter:
//                Intent serviceIntent = new Intent()
//                        .setComponent(new ComponentName(
//                                "floatingmuseum.userhunter",
//                                "floatingmuseum.userhunter.RemoteHunterService"));
//                startService(serviceIntent);
//                bindService(serviceIntent, connection, BIND_AUTO_CREATE);
                AidlService.sendCommand(AidlService.ACTION_BIND_REMOTE_SERVICE);
                break;
            case R.id.bt_send_message:
//                sendMessage();
                AidlService.sendCommand(AidlService.ACTION_SEND_REMOTE_MESSAGE);
                break;
        }
    }

    private void sendMessage() {
        if (remoteHunter == null) {
            return;
        }
        try {
            int x = new Random().nextInt(20);
            int y = new Random().nextInt(20);
            int result = remoteHunter.add(x, y);
            String message = "Ask RemoteHunter:" + x + "+" + y + "=?";
            TextView newSendMessage = new TextView(this);
            newSendMessage.setText(message);
            llLogContainer.addView(newSendMessage);
            TextView newResultMessage = new TextView(this);
            newResultMessage.setText("RemoteHunter answer is " + result);
            llLogContainer.addView(newResultMessage);
            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static Intent getLauncherIntent(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageManager manager = App.context.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            Logger.d("是否含有Launcher:" + intent.toString());
            return intent;
        } else {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
        AidlService.sendCommand(AidlService.ACTION_STOP_SERVICE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d("RemoteHunter...AidlActivity...onServiceConnected:" + name + "...Pid:" + Process.myPid());
            remoteHunter = RemoteHunter.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d("RemoteHunter...AidlActivity...onServiceDisconnected:" + name);
        }
    };
}
