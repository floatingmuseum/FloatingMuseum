package com.floatingmuseum.androidtest.functions.wifilist;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;

/**
 * Created by Floatingmuseum on 2017/5/25.
 */

public class WiFiDialog extends Dialog {

    private EditText inputEditText;
    private Button connect, cancel;
    private TextView title;
//    private Button disconnect;
    private Button cancelSave;
    private LinearLayout wifi_info;
    private TextView wifi_state;
    private TextView wifi_strength;
    private TextView wifi_speed;
    private boolean alreadySaved;
    private Context context;

    public WiFiDialog(Context context, boolean alreadySaved, String name) {
        super(context);
        this.context = context;
        this.alreadySaved = alreadySaved;
        setTitle(name);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(context).inflate(R.layout.wifi_dialog, null);
        title = (TextView) mView.findViewById(R.id.title);
        inputEditText = (EditText) mView.findViewById(R.id.password);
        connect = (Button) mView.findViewById(R.id.connect);
        cancel = (Button) mView.findViewById(R.id.cancel);
//        disconnect = (Button) mView.findViewById(R.id.disconnect);
        cancelSave = (Button) mView.findViewById(R.id.cancel_save);
        cancelSave.setVisibility(alreadySaved ? View.VISIBLE : View.GONE);

        wifi_info = (LinearLayout) mView.findViewById(R.id.wifi_info);
        wifi_state = (TextView) mView.findViewById(R.id.wifi_state);
        wifi_strength = (TextView) mView.findViewById(R.id.wifi_strength);
        wifi_speed = (TextView) mView.findViewById(R.id.wifi_speed);

        super.setContentView(mView);
    }

    public View getEditText() {
        return inputEditText;
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    public void hideInputEditText() {
        inputEditText.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
    }

//    public void hideDisconnect() {
//        disconnect.setVisibility(View.GONE);
//    }

    public void hideConnectButton() {
        connect.setVisibility(View.GONE);
    }

    /**
     * 确定键监听器 * @param listener
     */
    public void setOnConnectClickListener(View.OnClickListener listener) {
        connect.setOnClickListener(listener);
    }

    /**
     * 取消键监听器 * @param listener
     */
    public void setOnCancelClickListener(View.OnClickListener listener) {
        cancel.setOnClickListener(listener);
    }

//    public void setOnDisconnectClickListener(View.OnClickListener listener) {
//        disconnect.setOnClickListener(listener);
//    }

    public void setOnCancelSaveClickListener(View.OnClickListener listener) {
        cancelSave.setOnClickListener(listener);
    }

    public void showWiFiInfo(WiFiItemInfo itemInfo, WifiAdmin wifiAdmin) {
        wifi_info.setVisibility(View.VISIBLE);
        if (wifiAdmin.isConnectedTo(itemInfo.getBssid())) {
            wifi_state.setText("已连接");
        } else {
            wifi_state.setText("未连接");
        }

        wifi_strength.setText(checkStrength(itemInfo.getLevel()));
        wifi_speed.setText(wifiAdmin.getWifiInfo().getLinkSpeed() + "Mbps");
    }

    private String checkStrength(int level) {
        if (level >= -55) {
            return "强";
        } else if (level < -55 && level >= 85) {
            return "中";
        } else {
            return "弱";
        }
    }
}