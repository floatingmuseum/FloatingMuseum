package com.floatingmuseum.androidtest.functions.hotspot;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Floatingmuseum on 2017/3/22.
 */

public class ServerActivity extends BaseActivity {
    ServerSocket ss = null;
    String mClientMsg = "";
    Thread myCommsThread = null;
    protected static final int MSG_ID = 0x1337;
    public static final int SERVERPORT = 6000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        TextView tv = (TextView) findViewById(R.id.TextView01);
        tv.setText("Nothing from client yet");
        this.myCommsThread = new Thread(new CommsThread());
        this.myCommsThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            // 确保你退出时要关闭socket连接
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler myUpdateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID:
                    TextView tv = (TextView) findViewById(R.id.TextView01);
                    tv.setText(mClientMsg);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class CommsThread implements Runnable {
        public void run() {
            Socket s = null;
            try {
                ss = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                Message m = new Message();
                m.what = MSG_ID;
                try {
                    if (s == null)
                        s = ss.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String st = null;
                    st = input.readLine();
                    mClientMsg = st;
                    myUpdateHandler.sendMessage(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
