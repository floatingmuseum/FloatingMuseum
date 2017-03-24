package com.floatingmuseum.androidtest.functions.nsd;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Floatingmuseum on 2017/3/24.
 */

public class NsdServerActivity extends BaseActivity implements View.OnClickListener, Runnable {

    public static final String SERVICE_NAME = "我是服务端";
    public static final String SERVICE_TYPE = "_http._tcp.";
    private Button mBtnRegister;
    private TextView mTvContent;
    private NsdServiceInfo mNsdServiceInfo;
    private ServerSocket mServerSocket;
    private int mPort;
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTvContent.setText(mTvContent.getText() + (String) msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd_server);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        createServerSocket();
        createNsdServiceInfo();
        createRegistration();
        register();
    }

    private void register() {
        mNsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        mNsdManager.registerService(mNsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    private void createRegistration() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                ToastUtil.show("onRegistrationFailed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                ToastUtil.show("onUnregistrationFailed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                ToastUtil.show("onServiceRegistered");
                new Thread(NsdServerActivity.this).start();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                ToastUtil.show("onServiceUnregistered");
            }
        };
    }

    /**
     * 创建一个server来获取端口
     */
    private void createServerSocket() {
        if (mServerSocket != null) return;
        try {
            mServerSocket = new ServerSocket(0);//设为0,会自动获取没有占用的端口
            mPort = mServerSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建NsdServiceInfo
     */
    private void createNsdServiceInfo() {
        if (mNsdServiceInfo != null) return;
        mNsdServiceInfo = new NsdServiceInfo();
        mNsdServiceInfo.setServiceName(SERVICE_NAME);
        mNsdServiceInfo.setServiceType(SERVICE_TYPE);
        mNsdServiceInfo.setPort(mPort);
    }

    @Override
    public void run() {
        try {
            Socket socket = mServerSocket.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String content = null;
            while ((content = bufferedReader.readLine()) != null) {
                Message msg = mHandler.obtainMessage();
                msg.obj = content;
                msg.what = 0;
                mHandler.sendMessageDelayed(msg, 3000);
            }
            bufferedReader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mServerSocket.close();
            mServerSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //别忘了取消注册
        mNsdManager.unregisterService(mRegistrationListener);
    }
}
