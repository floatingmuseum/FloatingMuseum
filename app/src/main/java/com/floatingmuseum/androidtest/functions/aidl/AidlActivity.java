package com.floatingmuseum.androidtest.functions.aidl;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.RemoteUser;
import com.floatingmuseum.androidtest.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/6/11.
 */

public class AidlActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_send_message)
    Button btSendMessage;
    @BindView(R.id.ll_log_container)
    LinearLayout llLogContainer;

    RemoteUser remoteUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        ButterKnife.bind(this);

        btSendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send_message:
                try {
                    String message = "ServiceTime is:" + System.currentTimeMillis();
                    TextView newSendMessage = new TextView(this);
                    newSendMessage.setText(message);
                    llLogContainer.addView(newSendMessage);
                    remoteUser.sendMessage(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteUser = RemoteUser.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteUser = null;
        }
    };
}
