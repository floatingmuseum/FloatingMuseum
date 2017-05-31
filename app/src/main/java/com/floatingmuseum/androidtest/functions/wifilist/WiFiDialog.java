package com.floatingmuseum.androidtest.functions.wifilist;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/5/25.
 */

public class WiFiDialog extends Dialog {

    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_wifi_state)
    TextView tvWifiState;
    @BindView(R.id.tv_wifi_strength)
    TextView tvWifiStrength;
    @BindView(R.id.tv_wifi_speed)
    TextView tvWifiSpeed;
    @BindView(R.id.ll_wifi_info)
    LinearLayout llWifiInfo;
    @BindView(R.id.tv_cancel_save)
    TextView tvCancelSave;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_connect)
    TextView tvConnect;

    private boolean alreadySaved;
    private Context context;
    private String name;

    public WiFiDialog(Context context, boolean alreadySaved, String name) {
        super(context);
        this.context = context;
        this.alreadySaved = alreadySaved;
        this.name = name;


        setCustomDialog();
    }

    private void setCustomDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.wifi_dialog, null);
        setContentView(view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        //对话框高度占屏幕宽度四分之三
        lp.width = SystemUtil.getScreenWidth() / 4 * 3;
        Logger.d("屏幕宽高:...数值:" + lp.width + "...dp2px:" + SystemUtil.dp2px(lp.width) + "...px2dp:" + SystemUtil.px2dp(lp.width));
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        ButterKnife.bind(this, view);
        tvWifiName.setText(name);
        tvCancelSave.setVisibility(alreadySaved ? View.VISIBLE : View.GONE);
    }

    public View getEditText() {
        return etPassword;
    }

    public void hideInputEditText() {
        etPassword.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
    }

    public void hideConnectButton() {
        tvConnect.setVisibility(View.GONE);
    }

    /**
     * 确定键监听器 * @param listener
     */
    public void setOnConnectClickListener(View.OnClickListener listener) {
        tvConnect.setOnClickListener(listener);
    }

    /**
     * 取消键监听器 * @param listener
     */
    public void setOnCancelClickListener(View.OnClickListener listener) {
        tvCancel.setOnClickListener(listener);
    }

    public void setOnCancelSaveClickListener(View.OnClickListener listener) {
        tvCancelSave.setOnClickListener(listener);
    }

    public void showWiFiInfo(WiFiItemInfo itemInfo, WifiAdmin wifiAdmin) {
        llWifiInfo.setVisibility(View.VISIBLE);
        if (wifiAdmin.isConnectedTo(itemInfo.getBssid())) {
            tvWifiState.setText("已连接");
        } else {
            tvWifiState.setText("未连接");
        }

        tvWifiStrength.setText(checkStrength(itemInfo.getLevel()));
        tvWifiSpeed.setText(wifiAdmin.getWifiInfo().getLinkSpeed() + "Mbps");
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