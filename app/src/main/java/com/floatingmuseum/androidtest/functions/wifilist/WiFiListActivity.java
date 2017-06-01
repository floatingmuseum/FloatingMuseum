package com.floatingmuseum.androidtest.functions.wifilist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private String[] capabilities = {"[WEP", "[WPA2", "[WPA"};
    private String[] capabilitiesInfo = {"通过WEP进行保护", "通过WPA2进行保护", "通过WPA进行保护", "未加密"};
    private LinearLayoutManager linearLayoutManager;
    private Disposable disposable;
    private NetworkInfo.DetailedState lastDetailedState;
    private boolean isWiFiOpened = false;
    private WiFiDialog dialog;


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
                Logger.d("屏幕宽高:...宽:" + SystemUtil.getScreenWidth() + "...高度:" + SystemUtil.getScreenHeight());
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
            refreshResults(results);
        }
    }

    private void showWiFiDialog(final WiFiItemInfo wiFiItemInfo, boolean alreadySaved, final WifiConfiguration wcg) {
        dialog = new WiFiDialog(this, alreadySaved, wiFiItemInfo.getName());
        final EditText editText = (EditText) dialog.getEditText();

        if (alreadySaved) {
            dialog.hideInputEditText();
            dialog.showWiFiInfo(wiFiItemInfo, wifiAdmin);
        }

        if (wifiAdmin.isConnectedTo(wiFiItemInfo.getBssid())) {
            dialog.hideConnectButton();
        }

        // 方法在CustomDialog中实现
        dialog.setOnConnectClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wcg == null) {
                    connectWifi(wiFiItemInfo, editText.getText().toString());
                } else {
                    wifiAdmin.addNetwork(wcg);
                }
                dialog.dismiss();
            }
        });
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnCancelSaveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiAdmin.removeConfiguration(wcg);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
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
        registerReceiver(wifiRelatedReceiver, wifiRelatedFilter);
    }

    private void disWiFiScan() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        unregisterReceiver(wifiRelatedReceiver);
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
                dismissDialog();
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

        refreshResults(results);
        Logger.d("WiFi扫描结果...resultsUpdated:" + resultsUpdated);
    }

    private void refreshResults(List<ScanResult> newScanResults) {
        //先排重
        List<ScanResult> filteredList = filterList(newScanResults);
        //再排序
        sortList(filteredList);

        List<WiFiItemInfo> wifiItemInfoList = transferWiFiItemInfoList(filteredList);
        wifiList.clear();
        wifiList.addAll(wifiItemInfoList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 过滤同名WiFi
     * 过滤WiFi结果 已连接>信号强>信号低
     */
    private List<ScanResult> filterList(final List<ScanResult> results) {
        final Map<String, ScanResult> filterMap = new HashMap<>();
        List<ScanResult> filteredList = new ArrayList<>();

        for (ScanResult result : results) {
            //判断过滤Map中是否存在同名热点
            if (filterMap.containsKey(result.SSID)) {
                ScanResult existResult = filterMap.get(result.SSID);
                //判断Map中热点是否是已连接热点,如果不是,继续判断Map中热点信号强度是否低于同名热点,条件都满足则同名热点覆盖Map中热点
                if (!wifiAdmin.isConnectedTo(existResult.BSSID) && existResult.level < result.level) {
                    filterMap.put(result.SSID, result);
                }
            } else {
                filterMap.put(result.SSID, result);
            }
        }

        for (String ssid : filterMap.keySet()) {
            filteredList.add(filterMap.get(ssid));
        }
        return filteredList;
    }

    /**
     * 排序WiFi结果 已连接>信号强>信号低
     */
    private void sortList(List<ScanResult> results) {
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult result0, ScanResult result1) {
                //将已连接的热点前移
                if (wifiAdmin.isConnectedTo(result0.BSSID)) {
                    return -1;
                } else if (wifiAdmin.isConnectedTo(result1.BSSID)) {
                    return 1;
                }

                //将信号强度大的热点前移
                if (result0.level < result1.level) {
                    return 1;
                } else if (result0.level > result1.level) {
                    return -1;
                }
                return 0;
            }
        });
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
            if (info.getName().equals("CHAT")) {
                Logger.d("刷新WiFiItemInfo...新集合:" + info.getDesc());
            }
            infoList.add(info);
        }
        return infoList;
    }

    private String getWiFiDesc(ScanResult result) {
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        Logger.d("WiFi已连接节点信息:" + (wifiInfo == null ? wifiInfo : wifiInfo.toString()));
        if (result.SSID.equals("CHAT")) {
            Logger.d("刷新WiFiItemInfo...新集合:" + result.SSID + "..." + result.BSSID + "..." + wifiInfo);
        }
        if (wifiInfo != null && result.BSSID.equals(wifiInfo.getBSSID()) && wifiInfo.getSupplicantState().equals(SupplicantState.COMPLETED)) {
            if (result.SSID.equals("CHAT")) {
                Logger.d("刷新WiFiItemInfo...新集合:" + result.BSSID + "..." + wifiInfo.getBSSID());
            }
            return "已连接";
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
        }

        if (wifiInfo != null) {
            wifiInfoString = wifiInfo.toString();
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
            } else if (lastDetailedState != null && NetworkInfo.DetailedState.DISCONNECTED.equals(networkInfo.getDetailedState()) && NetworkInfo.DetailedState.AUTHENTICATING.equals(lastDetailedState)) {
                Logger.d("刷新WiFiItemInfo...连接失败:" + lastDetailedState);
                ToastUtil.show("连接失败." + lastDetailedState);
                lastDetailedState = null;
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
                Logger.d("刷新WiFiItemInfo...刷新单个Item:" + wifiItemInfo.getDesc());
                updateUI(i, wifiItemInfo);
                return;
            }
        }
    }

    private void updateUI(int position, WiFiItemInfo wifiItemInfo) {
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
}
