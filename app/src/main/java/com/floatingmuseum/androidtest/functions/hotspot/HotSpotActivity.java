package com.floatingmuseum.androidtest.functions.hotspot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ListUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/20.
 * <p>
 * 创建热点，进行点对点传输
 */

public class HotSpotActivity extends BaseActivity {

    @BindView(R.id.bt_discover)
    Button btDiscover;
    @BindView(R.id.rv_device)
    RecyclerView rvDevice;
    private BroadcastReceiver receiver;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private boolean isWifiP2pEnabled = false;
    private List<WifiP2pDevice> devices = new ArrayList<>();
    private MyActionListener actionListener;
    private PeersListener peersListener;
    private ConnectionListener connectionListener;
    private HotSpotAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);
        ButterKnife.bind(this);
        manager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        initView();
        initListener();
        initBroadcast();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvDevice.setLayoutManager(linearLayoutManager);
        rvDevice.setHasFixedSize(true);
        adapter = new HotSpotAdapter(devices);
        rvDevice.setAdapter(adapter);
        btDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverPeers();
            }
        });
        rvDevice.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                WifiP2pDevice device = devices.get(position);
                Logger.d("HotSpotActivity...onSimpleItemClick:" + device.deviceName);
                connect(device);
            }
        });
    }

    private void initListener() {
        actionListener = new MyActionListener();
        peersListener = new PeersListener();
        connectionListener = new ConnectionListener();
    }

    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        receiver = new P2pReceiver();
        registerReceiver(receiver, filter);
    }

    private void discoverPeers() {
        manager.discoverPeers(channel, actionListener);
    }

    public void connect(WifiP2pDevice device) {
        // Picking the first device found on the network.
//        WifiP2pDevice device = devices.get(0);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, actionListener);
    }

//    private void startRegistration() {
//        //  Create a string map containing information about your service.
//        Map record = new HashMap();
//        record.put("listenport", String.valueOf(888));
//        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
//        record.put("available", "visible");
//
//        // Service information.  Pass it an instance name, service type
//        // _protocol._transportlayer , and the map containing
//        // information other devices will want once they connect to this one.
//        WifiP2pDnsSdServiceInfo serviceInfo =
//                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);
//
//        // Add the local service, sending the service info, network channel,
//        // and listener that will be used to indicate success or failure of
//        // the request.
//        manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                // Command successful! Code isn't necessarily needed here,
//                // Unless you want to update the UI or add logging statements.
//            }
//
//            @Override
//            public void onFailure(int arg0) {
//                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
//            }
//        });
//    }
//
//    HashMap<String, String> buddies = new HashMap<String, String>();
//    private void discoverService() {
//        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
//            @Override
//        /* Callback includes:
//         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
//         * record: TXT record dta as a map of key/value pairs.
//         * device: The device running the advertised service.
//         */
//
//            public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {
//                Logger.d("HotSpotActivity...onDnsSdTxtRecordAvailable" + record.toString());
//                buddies.put(device.deviceAddress, (String) record.get("buddyname"));
//            }
//        };
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class P2pReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    // Determine if Wifi P2P mode is enabled or not, alert the Activity.
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    Logger.d("HotSpotActivity...Receiver...WIFI_P2P_STATE_CHANGED_ACTION:" + state);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        isWifiP2pEnabled = true;
                        Logger.d("HotSpotActivity...Receiver...WIFI_P2P_STATE_CHANGED_ACTION:" + "已启用");
//                        setIsWifiP2pEnabled(true);
                    } else {
                        Logger.d("HotSpotActivity...Receiver...WIFI_P2P_STATE_CHANGED_ACTION:" + "没有启用");
                        isWifiP2pEnabled = false;
//                        setIsWifiP2pEnabled(false);
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    WifiP2pDeviceList wifiP2pDeviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                    // The peer list has changed!  We should probably do something about that.
                    Logger.d("HotSpotActivity...Receiver...WIFI_P2P_PEERS_CHANGED_ACTION:" + wifiP2pDeviceList.getDeviceList().size());
                    if (wifiP2pDeviceList.getDeviceList().size() > 0) {
                        for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                            Logger.d("HotSpotActivity...Receiver...WIFI_P2P_PEERS_CHANGED_ACTION:" + device.deviceName + "..." + device.deviceAddress);
                        }
                        List<WifiP2pDevice> deviceList = new ArrayList<>();
                        deviceList.addAll(wifiP2pDeviceList.getDeviceList());
                        if (ListUtil.isEmpty(deviceList)) {
                            Logger.d("HotSpotActivity...PeersListener...onPeersAvailable...无数据");
                        } else {
                            devices.clear();
                            devices.addAll(deviceList);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    // Connection state changed!  We should probably do something about that.
                    Logger.d("HotSpotActivity...Receiver...WIFI_P2P_CONNECTION_CHANGED_ACTION:");
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                    if (networkInfo.isConnected()) {
                        // We are connected with the other device, request connection
                        // info to find group owner IP
                        manager.requestConnectionInfo(channel, connectionListener);
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    Logger.d("HotSpotActivity...Receiver...WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:" + device.deviceName + "..." + device.deviceAddress);
                    // Request available peers from the wifi p2p manager. This is an
                    // asynchronous call and the calling activity is notified with a
                    // callback on PeerListListener.onPeersAvailable()
                    if (manager != null) {
                        manager.requestPeers(channel, peersListener);
                    }
                    break;
            }
        }
    }

    private class MyActionListener implements WifiP2pManager.ActionListener {

        @Override
        public void onSuccess() {
            // Code for when the discovery initiation is successful goes here.
            // No services have actually been discovered yet, so this method
            // can often be left blank.  Code for peer discovery goes in the
            // onReceive method, detailed below.
            Logger.d("HotSpotActivity...DiscoverListener...onSuccess:");
        }

        @Override
        public void onFailure(int reason) {
            // Code for when the discovery initiation fails goes here.
            // Alert the user that something went wrong.
            Logger.d("HotSpotActivity...DiscoverListener...onFailure:" + reason);
        }
    }

    private class PeersListener implements WifiP2pManager.PeerListListener {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Logger.d("HotSpotActivity...PeersListener...onPeersAvailable:" + peers.getDeviceList().size());
            List<WifiP2pDevice> deviceList = new ArrayList<>();
            deviceList.addAll(peers.getDeviceList());
            if (ListUtil.isEmpty(deviceList)) {
                Logger.d("HotSpotActivity...PeersListener...onPeersAvailable...无数据");
            } else {
                devices.clear();
                devices.addAll(deviceList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class ConnectionListener implements WifiP2pManager.ConnectionInfoListener {

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            Logger.d("HotSpotActivity...ConnectionListener...onConnectionInfoAvailable:" + info.toString());

            // InetAddress from WifiP2pInfo struct.
            String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
            Logger.d("HotSpotActivity...ConnectionListener...onConnectionInfoAvailable:" + groupOwnerAddress);
            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
            } else if (info.groupFormed) {
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.
            }
        }
    }
}
