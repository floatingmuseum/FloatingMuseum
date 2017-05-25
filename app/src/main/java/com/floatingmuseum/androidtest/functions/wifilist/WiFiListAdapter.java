package com.floatingmuseum.androidtest.functions.wifilist;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/5/25.
 */

public class WiFiListAdapter extends BaseQuickAdapter<WiFiItemInfo, BaseViewHolder> {
    private WifiAdmin wifiAdmin;

    public WiFiListAdapter(@Nullable List<WiFiItemInfo> data, WifiAdmin wifiAdmin) {
        super(R.layout.item_wifi, data);
        this.wifiAdmin = wifiAdmin;
    }

    @Override
    protected void convert(BaseViewHolder helper, WiFiItemInfo info) {
        helper.setText(R.id.wifi_name, info.getName())
                .setImageResource(R.id.wifi_state, wifiAdmin.checkLevel(info.getLevel(), info.isLock()));
        if (TextUtils.isEmpty(info.getDesc())) {
            helper.setVisible(R.id.wifi_password_state, false);
        } else {
            helper.setVisible(R.id.wifi_password_state, true)
                    .setText(R.id.wifi_password_state, info.getDesc());
        }
    }
}
