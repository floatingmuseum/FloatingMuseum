package com.floatingmuseum.androidtest.functions.hotspot;

import android.net.wifi.p2p.WifiP2pDevice;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;

import java.util.List;

/**
 * Created by Floatingmuseum on 2017/3/24.
 */

public class HotSpotAdapter extends BaseQuickAdapter<WifiP2pDevice, BaseViewHolder> {

    public HotSpotAdapter(List<WifiP2pDevice> data) {
        super(R.layout.item_wifi_p2p_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiP2pDevice item) {
        helper.setText(R.id.tv_device_name, item.deviceName)
                .setText(R.id.tv_device_address, item.deviceAddress);
    }
}
