package com.floatingmuseum.androidtest.functions.otherprocess;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.App;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import floatingmuseum.userhunter.RemoteMuseum;

/**
 * Created by Floatingmuseum on 2017/6/13.
 */

public class OtherProcessActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_bind)
    Button btBind;
    @BindView(R.id.bt_unbind)
    Button btUnbind;
    @BindView(R.id.bt_send)
    Button btSend;
    @BindView(R.id.bt_make_error)
    Button btMakeError;
    @BindView(R.id.bt_make_remote_error)
    Button btMakeRemoteError;

    private RemoteMuseum museum;
    private boolean isConnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_process);
        ButterKnife.bind(this);

        btBind.setOnClickListener(this);
        btUnbind.setOnClickListener(this);
        btSend.setOnClickListener(this);
        btMakeError.setOnClickListener(this);
        btMakeRemoteError.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_bind:
                connectToService();
                break;
            case R.id.bt_unbind:
                disconnectToService();
                break;
            case R.id.bt_send:
                sendMessage("发送消息:...时间" + DateUtils.formatDateTime(App.context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
                break;
            case R.id.bt_make_error:
                int x = 1 / 0;
                break;
            case R.id.bt_make_remote_error:
                sendMessage("suicide");
                break;
        }
    }

    private void connectToService() {
        startService(new Intent(this, OtherProcessService.class));
        bindService(new Intent(this, OtherProcessService.class), connection, BIND_AUTO_CREATE);
    }

    private void disconnectToService() {
        //需要两个都调用
        stopService(new Intent(this, OtherProcessService.class));
        unbindService(connection);
    }

    private void sendMessage(String message) {
        if (museum != null && isConnected) {
            try {
                museum.sendMessage(message);
            } catch (RemoteException e) {
                Logger.d("RemoteMuseum:发送信息异常");
                e.printStackTrace();
            }
        } else {
            Logger.d("RemoteMuseum:重新尝试建立连接");
            connectToService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("RemoteMuseum:Activity...onDestroy()");
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConnected = true;
            Logger.d("RemoteMuseum:连接成功..." + name);
            museum = RemoteMuseum.Stub.asInterface(service);
            try {
                museum.asBinder().linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                Logger.d("RemoteMuseum:连接成功...设置destroy监听" + name);
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
            Logger.d("RemoteMuseum:连接断开..." + name);
        }
    };

    IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Logger.d("RemoteMuseum:...binder Gone away");
            if (museum != null) {
                museum.asBinder().unlinkToDeath(deathRecipient, 0);
                museum = null;
            }
            //重连
            connectToService();
        }
    };
}
