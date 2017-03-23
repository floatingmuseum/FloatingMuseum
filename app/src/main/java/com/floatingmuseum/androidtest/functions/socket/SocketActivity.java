package com.floatingmuseum.androidtest.functions.socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/23.
 */

public class SocketActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_device_address)
    TextView tvDeviceAddress;
    @BindView(R.id.tv_device_port)
    TextView tvDevicePort;
    @BindView(R.id.bt_start_server)
    Button btStartServer;
    @BindView(R.id.bt_stop_server)
    Button btStopServer;
    @BindView(R.id.et_server_address)
    EditText etServerAddress;
    @BindView(R.id.et_server_port)
    EditText etServerPort;
    @BindView(R.id.bt_connect_to)
    Button btConnectTo;
    @BindView(R.id.bt_disconnect_from)
    Button btDisconnectFrom;
    @BindView(R.id.ll_message_container)
    LinearLayout llMessageContainer;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.bt_send_message)
    Button btSendMessage;
    @BindView(R.id.ll_send)
    LinearLayout llSend;
    private String deviceAddress;
    private int devicePort;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
    private ServerThread serverThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        deviceAddress = getIpAddress();
        tvDeviceAddress.setText("DeviceAddress:" + deviceAddress);
        devicePort = 8080;
        tvDevicePort.setText(String.valueOf(devicePort));
        btStartServer.setOnClickListener(this);
        btStopServer.setOnClickListener(this);
        btConnectTo.setOnClickListener(this);
        btDisconnectFrom.setOnClickListener(this);
        btSendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_server:
                startServer();
                break;
            case R.id.bt_stop_server:
                stopServer();
                break;
            case R.id.bt_connect_to:
                connectTo();
                break;
            case R.id.bt_disconnect_from:
                break;
            case R.id.bt_send_message:
                break;
        }
    }

    private void startServer() {
        serverThread = new ServerThread(deviceAddress, devicePort,handler);
    }

    private void stopServer() {
        if (serverThread!=null) {
            serverThread.stopThread();
        }
    }

    private void connectTo() {
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
