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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Floatingmuseum on 2017/5/25.
 */

public class WiFiListActivity extends BaseActivity {

    @BindView(R.id.switch_wifi_state)
    Switch switchWifiState;
    @BindView(R.id.rv_wifi_list)
    RecyclerView rvWifiList;
    @BindView(R.id.tv_wifi_state)
    TextView tvWifiState;

    private WiFiRelatedReceiver wifiRelatedReceiver;
    private WifiAdmin wifiAdmin;
    private WiFiListAdapter adapter;
    private List<WiFiItemInfo> wifiList = new ArrayList<>();
    private ConnectReceiver connectReceiver;
    private ConnectivityManager connectivityManager;
    private String[] capabilities = {"[WEP", "[WPA2", "[WPA"};
    private String[] capabilitiesInfo = {"通过WEP进行保护", "通过WPA2进行保护", "通过WPA进行保护", "未加密"};
    private LinearLayoutManager linearLayoutManager;
    private Disposable disposable;
    private NetworkInfo.DetailedState lastDetailedState;
    private boolean isWiFiOpened = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        ButterKnife.bind(this);
        wifiAdmin = new WifiAdmin(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchWifiState.setShowText(false);
        }

        initView();
        initWiFiStateReceiver();
    }

    private void startIntervalCheck() {
        disposable = Flowable.interval(1000, 5000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        wifiAdmin.WifiStartScan();
                    }
                });
    }

    private void initView() {
        switchWifiState.setChecked(wifiAdmin.isOpened());
        switchWifiState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchWifiState.isEnabled()) {
                    switchWifiState.setEnabled(false);
                    if (switchWifiState.isChecked()) {
                        wifiAdmin.openNetCard();
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
                    Logger.d("WifiDialog检查:" + info.getName() + "..." + ssid + "..." + configuration.SSID + "...Configuration:" + configuration + "...配置信息:" + configuration.toString());
                    if (info.getName().equals(ssid)) {
                        alreadySaved = true;
                        wcg = configuration;
                    }
                }
                showWiFiDialog(info, alreadySaved, wcg);
            }
        });
        if (wifiAdmin.isOpened()) {
            isWiFiOpened = true;
            List<ScanResult> results = wifiAdmin.getScanResult();
            sortAndRefreshResults(results);
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
//            mDialog.hideDisconnect();
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

    private void disWiFiScan() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
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
//        int previousState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, -1);
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                Logger.d("WiFi状态:开启中..." + wifiAdmin.isOpened());
                rvWifiList.setVisibility(View.GONE);
                tvWifiState.setVisibility(View.VISIBLE);
                tvWifiState.setText("WiFi模块开启中...");
                switchWifiState.setChecked(true);
                switchWifiState.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Logger.d("WiFi状态:已开启..." + wifiAdmin.isOpened());
                isWiFiOpened = true;
                rvWifiList.setVisibility(View.GONE);
                tvWifiState.setVisibility(View.VISIBLE);
                tvWifiState.setText("WiFi热点搜寻中....");
                switchWifiState.setEnabled(true);
                startIntervalCheck();
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                Logger.d("WiFi状态:关闭中..." + wifiAdmin.isOpened());
                isWiFiOpened = false;
                rvWifiList.setVisibility(View.GONE);
                tvWifiState.setVisibility(View.VISIBLE);
                tvWifiState.setText("WiFi模块关闭中....");
                switchWifiState.setChecked(false);
                switchWifiState.setEnabled(false);
                disWiFiScan();
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                Logger.d("WiFi状态:已关闭..." + wifiAdmin.isOpened());
                rvWifiList.setVisibility(View.GONE);
                tvWifiState.setVisibility(View.VISIBLE);
                tvWifiState.setText("WiFi模块已关闭.");
                switchWifiState.setEnabled(true);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Logger.d("WiFi状态:未知..." + wifiAdmin.isOpened());
//                        wifiSearching.setVisibility(View.GONE);
                break;
        }
    }

    private void handleScanResultsAvailable(Intent intent) {
        if (!isWiFiOpened) {
            return;
        }
        boolean resultsUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
        List<ScanResult> results = wifiAdmin.getScanResult();

        if (results.size() == 0) {
            tvWifiState.setText("未发现WiFi热点,继续搜寻中...");
            rvWifiList.setVisibility(View.GONE);
            tvWifiState.setVisibility(View.VISIBLE);
            return;
        } else {
            rvWifiList.setVisibility(View.VISIBLE);
            tvWifiState.setVisibility(View.GONE);
        }

        sortAndRefreshResults(results);
        Logger.d("WiFi扫描结果...resultsUpdated:" + resultsUpdated);
    }

    private void sortAndRefreshResults(List<ScanResult> newScanResults) {
        // TODO: 2017/5/26 1.已连接上的热点忽略强度大小直接排在第一 2.同名WiFi只保留一个,如果已连接则保留当前连接的热点,否则保留信号强度最高

        //先排重

        //再排序
        Collections.sort(newScanResults, new Comparator<ScanResult>() {
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

        List<WiFiItemInfo> wifiItemInfoList = transferWiFiItemInfoList(newScanResults);
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
            info.setDesc(desc);
            infoList.add(info);
        }
        return infoList;
    }

    private String getWiFiDesc(ScanResult result) {
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        Logger.d("WiFi已连接节点信息:" + (wifiInfo == null ? wifiInfo : wifiInfo.toString()));
        if (wifiInfo != null && result.BSSID.equals(wifiInfo.getBSSID())) {
            return "已连接";
//            return getWiFiStateDesc(WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()));
        } else {
            return "";
        }
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
        int supplicantError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -999);

        Logger.d("WiFi节点状态...supplicantState:" + supplicantState + "...supplicantError:" + supplicantError + "..." + intent.hasExtra(WifiManager.EXTRA_SUPPLICANT_ERROR));
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

        if (bssid == null && networkInfo != null && networkInfo.getExtraInfo() != null) {
            if (NetworkInfo.DetailedState.AUTHENTICATING.equals(networkInfo.getDetailedState())) {
                lastDetailedState = NetworkInfo.DetailedState.AUTHENTICATING;
            } else if (NetworkInfo.DetailedState.DISCONNECTED.equals(networkInfo.getDetailedState()) && NetworkInfo.DetailedState.AUTHENTICATING.equals(lastDetailedState)) {
                ToastUtil.show("连接失败.");
            }
        } else {
            lastDetailedState = null;
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
