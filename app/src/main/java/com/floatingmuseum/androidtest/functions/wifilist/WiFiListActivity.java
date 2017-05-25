package com.floatingmuseum.androidtest.functions.wifilist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/5/25.
 */

public class WiFiListActivity extends BaseActivity {

    @BindView(R.id.switch_wifi_state)
    Switch switchWifiState;
    @BindView(R.id.rv_wifi_list)
    RecyclerView rvWifiList;

    private WiFiRelatedReceiver wifiRelatedReceiver;
    private WifiAdmin wifiAdmin;
    private WiFiListAdapter adapter;
    private List<WiFiItemInfo> wifiList = new ArrayList<>();
    private ConnectReceiver connectReceiver;
    private ConnectivityManager connectivityManager;
    private String[] capabilities = {"[WEP", "[WPA2", "[WPA"};
    private String[] capabilitiesInfo = {"通过WEP进行保护", "通过WPA2进行保护", "通过WPA进行保护", "未加密"};
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        ButterKnife.bind(this);
        wifiAdmin = new WifiAdmin(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchWifiState.setShowText(false);
        }
        initSwitch();
        initRv();
        initWiFiStateReceiver();
    }

    private void initSwitch() {
        switchWifiState.setChecked(wifiAdmin.isOpened());
        switchWifiState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchWifiState.isEnabled()) {
                    switchWifiState.setEnabled(false);
                    if (switchWifiState.isChecked()) {
                        wifiAdmin.openNetCard();
//                        wifiClosed.setVisibility(View.GONE);
//                        wifiSearching.setVisibility(View.VISIBLE);
//                        wifi_fragment.refreshWifi();
                    } else {
                        wifiAdmin.closeNetCard();
                    }
                }
            }
        });

        switchWifiState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.d("WiFiSwitch按钮:" + isChecked);
            }
        });
    }

    private void initRv() {
        linearLayoutManager = new LinearLayoutManager(this);
        rvWifiList.setLayoutManager(linearLayoutManager);
        adapter = new WiFiListAdapter(wifiList, wifiAdmin);
        rvWifiList.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                boolean alreadySaved = false;
                WifiConfiguration wcg = null;
                WiFiItemInfo info = wifiList.get(position);
                List<WifiConfiguration> configurations = wifiAdmin.getConfiguration();
                //查看是否保存有当前item的Configuration
                for (WifiConfiguration configuration : configurations) {
                    String ssid = configuration.SSID.substring(1, configuration.SSID.length() - 1);
                    Logger.d("WifiDialog检查:" + ssid + "..." + info.getName() + "...BSSID:" + configuration.BSSID + "..." + info.getBssid());
                    if (info.getBssid().equals(configuration.BSSID)) {
                        alreadySaved = true;
                        wcg = configuration;
                    }
                }
                showWiFiDialog(info, alreadySaved, wcg);
            }
        });
        if (wifiAdmin.isOpened()) {
            sortAndRefreshResults();
        }
    }

    private void showWiFiDialog(final WiFiItemInfo wiFiItemInfo, boolean alreadySaved, final WifiConfiguration wcg) {
        final WiFiDialog mDialog = new WiFiDialog(this, alreadySaved, wiFiItemInfo.getName());
        final EditText editText = (EditText) mDialog.getEditText();

        if (alreadySaved) {
            mDialog.hideInputEditText();
            mDialog.showWiFiInfo(wiFiItemInfo, wifiAdmin);
        }

        if (wifiAdmin.isConnectedTo(wiFiItemInfo.getBssid())) {
            mDialog.hideConnectButton();

        } else {
            mDialog.hideDisconnect();
        }

        // 方法在CustomDialog中实现
        mDialog.setOnConnectClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wcg == null) {
                    connectWifi(wiFiItemInfo, editText.getText().toString());
                } else {
                    wifiAdmin.addNetwork(wcg);
                }
                mDialog.dismiss();
            }
        });
        mDialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setOnDisconnectClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                wifiAdmin.disconnectWifi();
                mDialog.dismiss();
            }
        });
        mDialog.setOnCancelSaveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiAdmin.removeConfiguration(wcg);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void connectWifi(WiFiItemInfo wiFiItemInfo, String password) {
        Logger.d("WiFi建立新连接...密码:" + password);
        int type = WifiAdmin.Get_type(wiFiItemInfo.getCapabilities());
        WifiConfiguration configuration = wifiAdmin.CreateWifiInfo(wiFiItemInfo.getName(), password, type);
        wifiAdmin.addNetwork(configuration);
    }

    private void initWiFiStateReceiver() {
        wifiRelatedReceiver = new WiFiRelatedReceiver();
        IntentFilter wifiRelatedFilter = new IntentFilter();
        wifiRelatedFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiRelatedFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        wifiRelatedFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiRelatedFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiRelatedFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(wifiRelatedReceiver, wifiRelatedFilter);

        connectReceiver = new ConnectReceiver();
        IntentFilter connectFilter = new IntentFilter();
        connectFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectReceiver, connectFilter);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        connectivityManager.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
//            @Override
//            public void onNetworkActive() {
//
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiRelatedReceiver);
        unregisterReceiver(connectReceiver);
    }

    private class WiFiRelatedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                handleWiFiStateChanged(intent);
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                handleScanResultsAvailable(intent);
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                handleSupplicantStateChanged(intent);
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                handleNetworkStateChanged(intent);
            } else if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
                handleSupplicantConnectionChanged(intent);
            }
        }
    }

    private void handleWiFiStateChanged(Intent intent) {
        int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
        int previousState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, -1);
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                Logger.d("WiFi状态:已关闭..." + wifiAdmin.isOpened());
                switchWifiState.setEnabled(true);
//                        wifiList.setVisibility(View.GONE);
//                        switchWifiState.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Logger.d("WiFi状态:开启中..." + wifiAdmin.isOpened());
//                        wifiClosed.setVisibility(View.GONE);
//                        wifiList.setVisibility(View.GONE);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Logger.d("WiFi状态:已开启..." + wifiAdmin.isOpened());
                switchWifiState.setEnabled(true);
//                        wifiClosed.setVisibility(View.GONE);
//                        switchWifiState.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                Logger.d("WiFi状态:关闭中..." + wifiAdmin.isOpened());
//                        wifiSearching.setVisibility(View.GONE);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Logger.d("WiFi状态:未知..." + wifiAdmin.isOpened());
//                        wifiSearching.setVisibility(View.GONE);
                break;
        }

        switch (previousState) {
            case WifiManager.WIFI_STATE_DISABLED:
                Logger.d("WiFiPrevious状态:已关闭..." + wifiAdmin.isOpened());
//                        wifiList.setVisibility(View.GONE);
//                        switchWifiState.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Logger.d("WiFiPrevious状态:开启中..." + wifiAdmin.isOpened());
//                        wifiClosed.setVisibility(View.GONE);
//                        wifiList.setVisibility(View.GONE);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Logger.d("WiFiPrevious状态:已开启..." + wifiAdmin.isOpened());
//                        wifiClosed.setVisibility(View.GONE);
//                        switchWifiState.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                Logger.d("WiFiPrevious状态:关闭中..." + wifiAdmin.isOpened());
//                        wifiSearching.setVisibility(View.GONE);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Logger.d("WiFiPrevious状态:未知..." + wifiAdmin.isOpened());
//                        wifiSearching.setVisibility(View.GONE);
                break;
        }
    }

    private void handleScanResultsAvailable(Intent intent) {
        boolean resultsUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
        sortAndRefreshResults();
        Logger.d("WiFi扫描结果...resultsUpdated:" + resultsUpdated);
    }

    private void sortAndRefreshResults() {
        List<ScanResult> results = wifiAdmin.getScanResult();
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult result0, ScanResult result1) {
                if (result0.level < result1.level) {
                    return 1;
                } else if (result0.level > result1.level) {
                    return -1;
                }
                return 0;
            }
        });

        List<WiFiItemInfo> wifiItemInfoList = transferWiFiItemInfoList(results);
        wifiList.clear();
        wifiList.addAll(wifiItemInfoList);
        adapter.notifyDataSetChanged();
    }

    private List<WiFiItemInfo> transferWiFiItemInfoList(List<ScanResult> results) {
        List<WiFiItemInfo> infoList = new ArrayList<>();
        for (ScanResult result : results) {
            int encryptionType = checkWifiCapabilities(result.capabilities);
            WiFiItemInfo info = new WiFiItemInfo();
            info.setName(result.SSID);
            info.setBssid(result.BSSID);
            info.setLevel(result.level);
            info.setCapabilities(result.capabilities);
            info.setLock(isWiFiLocked(encryptionType));
            String desc = getWiFiDesc(result);
            if (!TextUtils.isEmpty(desc)) {
                Logger.d("WiFi结果刷新后Name:" + info.getName() + "...Desc:" + desc+"..."+wifiAdmin.getWifiInfo().getSupplicantState());
            }
            info.setDesc(desc);
            infoList.add(info);
        }
        return infoList;
    }

    private String getWiFiDesc(ScanResult result) {
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        Logger.d("WiFi已连接节点信息:" + (wifiInfo == null ? wifiInfo : wifiInfo.toString()));
        if (wifiInfo != null) {
            if (wifiInfo.getBSSID().equals(result.BSSID)) {
                return getWiFiStateDesc(WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()));
            }
        }
        return "";
    }

    private boolean isWiFiLocked(int encryptionType) {
        return encryptionType == 3 ? false : true;
    }

    public int checkWifiCapabilities(String capability) {
        for (int i = 0; i < capabilities.length; i++) {
            if (capability.startsWith(capabilities[i])) {
                return i;
            }
        }
        return 3;
    }

    private void handleSupplicantStateChanged(Intent intent) {
        SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
        int supplicantError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);

        Logger.d("WiFi节点状态...supplicantState:" + supplicantState + "...supplicantError:" + supplicantError);
    }

    private void handleNetworkStateChanged(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
        WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
        String networkInfoString = null;
        String wifiInfoString = null;
        if (networkInfo != null) {
            networkInfoString = networkInfo.toString();
//            Logger.d("WiFi网络状态...NetworkInfo:" + networkInfo.toString());
        }
        if (wifiInfo != null) {
            wifiInfoString = wifiInfo.toString();
//            Logger.d("WiFi网络状态...WiFiInfo:" + wifiInfo.toString());
        }
        if (!TextUtils.isEmpty(bssid)) {
//            Logger.d("WiFi网络状态...BSSID:" + bssid);
        }
        Logger.d("WiFi网络状态...BSSID:" + bssid + "...NetworkInfo:" + networkInfoString + "...WiFiInfo:" + wifiInfoString);
        if (!TextUtils.isEmpty(bssid) && networkInfo != null) {
            if (networkInfo.isConnected()) {
                wifiAdmin.saveConfiguration();
            }
            refreshSingleWiFiItem(bssid, networkInfo);
        }
    }

    private void refreshSingleWiFiItem(String bssid, NetworkInfo networkInfo) {
        if (wifiList.isEmpty()) {
            return;
        }

        for (int i = 0; i < wifiList.size(); i++) {
            WiFiItemInfo wifiItemInfo = wifiList.get(i);
            if (bssid.equals(wifiItemInfo.getBssid())) {
                wifiItemInfo.setDesc(getWiFiStateDesc(networkInfo.getDetailedState()));
                updateUI(i, bssid, wifiItemInfo);
                return;
            }
        }
    }

    private void updateUI(int position, String bssid, WiFiItemInfo wifiItemInfo) {
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        if (position >= firstVisibleItemPosition && position <= lastVisibleItemPosition) {
            BaseViewHolder holder = (BaseViewHolder) rvWifiList.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                if (TextUtils.isEmpty(wifiItemInfo.getDesc())) {
                    holder.setVisible(R.id.wifi_password_state, false);
                } else {
                    holder.setVisible(R.id.wifi_password_state, true)
                            .setText(R.id.wifi_password_state, wifiItemInfo.getDesc());
                }
            }
        }
    }

    private String getWiFiStateDesc(NetworkInfo.DetailedState state) {
        String desc = "";
        switch (state) {
            case AUTHENTICATING:
                desc = "正在进行身份验证...";
                break;
            case OBTAINING_IPADDR:
                desc = "正在获取Ip地址";
                break;
            case CONNECTED:
                desc = "已连接";
                break;
        }
        return desc;
    }

    private void handleSupplicantConnectionChanged(Intent intent) {
        boolean supplicantConnectionChanged = intent.getBooleanExtra(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION, false);
        Logger.d("WiFi节点连接...supplicantConnectionChanged:" + supplicantConnectionChanged);
    }

    private class ConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d("WiFi网络ConnectReceiver...intent:" + intent);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                Logger.d("WiFi网络ConnectReceiver...NetworkInfo:" + networkInfo.toString());
            }
        }
    }
}
