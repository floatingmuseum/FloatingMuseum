package com.floatingmuseum.androidtest.functions.wifilist;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.widget.Toast;

import com.floatingmuseum.androidtest.R;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * wifi操作工具类
 *
 * @author seconds
 */
public class WifiAdmin {
    private final static String TAG = "WifiUtils";
    private Context context;
    // 扫描结果列表
    private List<ScanResult> listResult;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiLock mWifiLock;

    /**
     * 构造方法
     *
     * @param context
     */
    public WifiAdmin(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 打开Wifi网卡
     */
    public void openNetCard() {
        if (!mWifiManager.isWifiEnabled()) {
            Logger.d("openNetCard");
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi网卡
     */
    public void closeNetCard() {
        if (mWifiManager.isWifiEnabled()) {
            Logger.d("closeNetCard");
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查当前Wifi网卡状态
     *
     * @return
     */
    public int checkNetCardState() {
        // if (mWifiManager.getWifiState() == 0) {
        // StringUtils.toast(context, "网卡正在关闭");
        // // Log.i(TAG, "网卡正在关闭");
        // } else if (mWifiManager.getWifiState() == 1) {
        // StringUtils.toast(context, "网卡已经关闭");
        // // Log.i(TAG, "网卡已经关闭");
        // } else if (mWifiManager.getWifiState() == 2) {
        // StringUtils.toast(context, "网卡正在打开");
        // // Log.i(TAG, "网卡正在打开");
        // } else if (mWifiManager.getWifiState() == 3) {
        // StringUtils.toast(context, "网卡已经打开");
        // // Log.i(TAG, "网卡已经打开");
        // } else {
        // StringUtils.toast(context, "没有获取到状态");
        // // Log.i(TAG, "没有获取到状态");
        // }
        return mWifiManager.getWifiState();
    }

    /**
     * 扫描wifi
     */
    public void WifiStartScan() {
        mWifiManager.startScan();
    }

    /**
     * 扫描周边网络
     *
     * @return
     */
    public List<ScanResult> getScanResult() {
        mWifiManager.getWifiState();
        listResult = mWifiManager.getScanResults();
        return listResult;
    }

    /**
     * 断开当前连接的网络
     */
    public void disconnectWifi() {
        int netId = getNetworkId();
        WifiInfo info = getWifiInfo();

        mWifiManager.disableNetwork(netId);
        boolean isDisconnect = mWifiManager.disconnect();
        Logger.d("WifiDialog:断开:" + isDisconnect + "..." + info.toString());
//        FileUtil.saveWifiLog(MyApplication.mContext, "WifiDialog:断开:" + isDisconnect);
    }

    /**
     * 检查当前网络状态
     */
    public void checkNetWorkState() {
        if (mWifiInfo != null) {
            Toast.makeText(context, "网络正常工作", Toast.LENGTH_SHORT).show();
            // Log.i(TAG, "网络正常工作");
        } else {
            Toast.makeText(context, "网络已断开", Toast.LENGTH_SHORT).show();
            // Log.i(TAG, "网络已断开");
        }
    }

    /**
     * 得到连接的ID
     *
     * @return
     */
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 得到IP地址
     *
     * @return
     */
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 得到MAC地址
     *
     * @return
     */
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    /**
     * 得到SSID
     *
     * @return
     */
    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    /**
     * 得到接入点的BSSID
     *
     * @return
     */
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULLNULL" : mWifiInfo.getBSSID();
    }

    //未测试过此方法
    public int getLevel(int level) {
        return WifiManager.calculateSignalLevel(level, 5);
    }

    /**
     * 得到WifiInfo的所有信息包
     *
     * @return
     */
    public WifiInfo getWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? null : mWifiInfo;
    }

    /**
     * 得到配置好的网络
     *
     * @return
     */
    public List<WifiConfiguration> getConfiguration() {
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        return mWifiConfiguration;
    }

    /**
     * 指定配置好的网络进行连接
     *
     * @param index
     */
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index >= mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        boolean b = mWifiManager.enableNetwork(
                mWifiConfiguration.get(index).networkId, true);
    }

    /**
     * 添加一个网络并连接
     *
     * @param wcg
     */
    public void addNetwork(WifiConfiguration wcg) {

        for (WifiConfiguration config : mWifiManager.getConfiguredNetworks()) {
            String newSSID = config.SSID;

            if (wcg.SSID.equals(newSSID)) {
                Logger.d("WifiDialog:重启旧连接");
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(config.networkId, true);
                mWifiManager.reconnect();
                return;
            }
        }

        Logger.d("WifiDialog:新建连接");
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean connected = mWifiManager.enableNetwork(wcgID, true);
        Logger.d("WifiDialog:连接:" + connected + "...networkId:" + wcgID + wcg.networkId);

//        int wcgID = mWifiManager.addNetwork(wcg);
//        boolean b = mWifiManager.enableNetwork(wcgID, true);
//        mWifiManager.saveConfiguration();
    }

    public void saveConfiguration() {
        mWifiManager.saveConfiguration();
    }

    public void removeConfiguration(WifiConfiguration wcg) {
        boolean isRemoveNetwork = mWifiManager.removeNetwork(wcg.networkId);
        boolean isSaveConfiguration = mWifiManager.saveConfiguration();
//        Logger.d("WifiDialog:取消保存:" + wcg.SSID + "..." + wcg.BSSID + "...netId:" + wcg.networkId + "...");
//        Logger.d("WifiDialog:取消保存:" + isRemoveNetwork + "...isSaveConfiguration:" + isSaveConfiguration + "...netId:" + wcg.networkId);
//        FileUtil.saveWifiLog(MyApplication.mContext, "WifiDialog:取消保存:" + isRemoveNetwork + "...isSaveConfiguration:" + isSaveConfiguration);
    }

//	public List<> getConfiguration(){
//		return mWifiConfiguration
//	}

    /**
     * 创建wifi信息 分为三种情况：1没有密码2用wep加密3用wpa加密
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    public WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                            int Type) {
        Log.i(TAG, "SSID:" + SSID + ",password:" + Password);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExists(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        } else {
            Log.i(TAG, "isExists is null.");
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            Log.i(TAG, "Type =1.");
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            Log.i(TAG, "Type =2.");
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {

            Log.i(TAG, "Type =3.");
            config.preSharedKey = "\"" + Password + "\"";

            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 是否已经开启wifi
     */
    public boolean isOpened() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 是否已连接到某个热点
     */
    public boolean IsWifiConnected() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isConnectedTo(String BSSID) {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
//			Log.d("yan","BSSID:"+BSSID+ "...SSID:"+wifiInfo.getSSID()+"...当前连接"+wifiInfo.getBSSID());
            if (wifiInfo.getBSSID() != null) {
                return wifiInfo.getBSSID().equals(BSSID);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 查看以前是否已经配置过该SSID
     *
     * @param SSID
     * @return
     */
    private WifiConfiguration isExists(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        if (existingConfigs != null && existingConfigs.size() > 0) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
//		Logger.d("existingConfigs:"+existingConfigs);

        return null;
    }

    /**
     * 判断wifi信号强度
     *
     * @param level
     * @return
     */
//    public int changelevel(int level) {
//        if (level < 0 && level >= -60) {
//            return R.drawable.ic_wifi_open_signal_3;
//        } else if (level < -60 && level >= -70) {
//            return R.drawable.ic_wifi_open_signal_2;
//
//        }
//        return R.drawable.ic_wifi_open_signal_1;
//    }
    public int checkLevel(int level, boolean lock) {
        if (level >= -55) {
            return lock == true ? R.drawable.ic_signal_wifi_4_bar_lock_36pt
                    : R.drawable.ic_signal_wifi_4_bar_36pt;
        } else if (level < -55 && level >= -70) {
            return lock == true ? R.drawable.ic_signal_wifi_3_bar_lock_36pt
                    : R.drawable.ic_signal_wifi_3_bar_36pt;
        } else if (level < -70 && level >= -85) {
            return lock == true ? R.drawable.ic_signal_wifi_2_bar_lock_36pt
                    : R.drawable.ic_signal_wifi_2_bar_36pt;
        } else if (level < -85 && level >= -100) {
            return lock == true ? R.drawable.ic_signal_wifi_1_bar_lock_36pt
                    : R.drawable.ic_signal_wifi_1_bar_36pt;
        } else {
            return R.drawable.ic_signal_wifi_0_bar_36pt;
        }
    }

    public int checkLevel(int level) {
        if (level >= -55) {
            return R.drawable.ic_signal_wifi_4_bar_white_36dp;
        } else if (level < -55 && level >= -70) {
            return R.drawable.ic_signal_wifi_3_bar_white_36dp;
        } else if (level < -70 && level >= -85) {
            return R.drawable.ic_signal_wifi_2_bar_white_36dp;
        } else if (level < -85 && level >= -100) {
            return R.drawable.ic_signal_wifi_1_bar_white_36dp;
        } else {
            return R.drawable.ic_signal_wifi_0_bar_white_36dp;
        }
    }

    /**
     * 路由器状态 1没有密码2用wep加密3用wpa加密
     *
     * @param
     * @return
     */
    public static int Get_type(String capabilities) {
        // String cap = wifi.getCapabilities();
        if (capabilities.contains("WEP")) {
            return 2;
        } else if (capabilities.contains("WPA")) {
            return 3;
        }
        return 1;
    }
}
