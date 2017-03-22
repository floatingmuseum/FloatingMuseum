package com.floatingmuseum.androidtest.functions.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/20.
 */

public class BluetoothActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bt_start_discover)
    Button btStartDiscover;
    @BindView(R.id.bt_cancel_discover)
    Button btCancelDiscover;
    @BindView(R.id.ll_bonded_device_container)
    LinearLayout llBondedDeviceContainer;
    @BindView(R.id.ll_discover_device_container)
    LinearLayout llDiscoverDeviceContainer;
    @BindView(R.id.tv_discover_state)
    TextView tvDiscoverState;
    @BindView(R.id.sc_open_for_discover)
    SwitchCompat scOpenForDiscover;
    @BindView(R.id.bt_open_bluetooth)
    Button btOpenBluetooth;
    @BindView(R.id.bt_close_bluetooth)
    Button btCloseBluetooth;

    private static final int REQUEST_ENABLE_BT = 1024;
    private static final int REQUEST_BT_DISCOVERABLE = 1023;
    private BluetoothReceiver receiver;
    private BluetoothManager blManager;
    private BluetoothAdapter blAdapter;
    private AdapterScanCallback leScanCallback;
    private ScannerCallback scannerCallback;

    Map<String, BluetoothDevice> discoverDevice = new HashMap<>();
    private UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket clientSocket;
    private OutputStream clientOutputStream;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket serverConnectSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);
        blManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        blAdapter = blManager.getAdapter();

//        myUUID = UUID.randomUUID();

        ServerThread serverThread = new ServerThread();
        serverThread.start();

        initView();

        initBondedDevices();

        initReceiver();
    }

    private void initView() {
        btOpenBluetooth.setOnClickListener(this);
        btCloseBluetooth.setOnClickListener(this);
        btStartDiscover.setOnClickListener(this);
        btCancelDiscover.setOnClickListener(this);
        scOpenForDiscover.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    setDiscoverable(120);
                } else {

                }
            }
        });
    }

    private void initBondedDevices() {
        //获取已配对过的设备
        Set<BluetoothDevice> bondedDevices = blAdapter.getBondedDevices();
        Logger.d("BluetoothActivity...已配对设备:" + bondedDevices.size());
        llBondedDeviceContainer.removeAllViews();
        for (final BluetoothDevice device : bondedDevices) {
            CardView deviceItem = (CardView) LayoutInflater.from(this).inflate(R.layout.bluetooth_device_item, llBondedDeviceContainer, false);
            TextView tvDeviceName = (TextView) deviceItem.findViewById(R.id.tv_device_name);
            TextView tvDeviceAddress = (TextView) deviceItem.findViewById(R.id.tv_device_address);
            tvDeviceName.setText("设备名称:" + device.getName());
            tvDeviceAddress.setText("设备地址:" + device.getAddress());
            deviceItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectToRemote(device);
                }
            });
            llBondedDeviceContainer.addView(deviceItem);
            Logger.d("BluetoothActivity...已配对设备:" + device.getName() + "..." + device.getAddress() + "..." + device.getBondState());
        }
    }

    private void initDiscoverDevice() {
        llDiscoverDeviceContainer.removeAllViews();
        for (String address : discoverDevice.keySet()) {
            final BluetoothDevice device = discoverDevice.get(address);
            CardView deviceItem = (CardView) LayoutInflater.from(this).inflate(R.layout.bluetooth_device_item, llDiscoverDeviceContainer, false);
            TextView tvDeviceName = (TextView) deviceItem.findViewById(R.id.tv_device_name);
            TextView tvDeviceAddress = (TextView) deviceItem.findViewById(R.id.tv_device_address);
            tvDeviceName.setText("设备名称:" + device.getName());
            tvDeviceAddress.setText("设备地址:" + device.getAddress());
            deviceItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectToRemote(device);
                }
            });
            llDiscoverDeviceContainer.addView(deviceItem);
        }
    }

    private void connectToRemote(BluetoothDevice device) {
        BluetoothDevice remoteDevice = blAdapter.getRemoteDevice(device.getAddress());
        try {
//            clientSocket = remoteDevice.createRfcommSocketToServiceRecord(myUUID);
            clientSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
            clientSocket.connect();
            clientOutputStream = clientSocket.getOutputStream();
            clientOutputStream.write("bluetooth message from floatingmuseum".getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可被其他设备检测到
     * 秒数
     */
    private void setDiscoverable(int time) {
        //设置设备在多少时间内是可见的
        //如果没有打开蓝牙，执行这个操作会自动打开蓝牙
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        startActivityForResult(intent, REQUEST_BT_DISCOVERABLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_open_bluetooth:
                enableBluetooth();
                break;
            case R.id.bt_close_bluetooth:
                disableBluetooth();
                break;
            case R.id.bt_start_discover:
                startDiscover();
                break;
            case R.id.bt_cancel_discover:
                stopDiscover();
                break;
        }
    }

    private void enableBluetooth() {
        if (!blAdapter.isEnabled()) {
            blAdapter.enable();
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {// YES 用户允许
                Logger.d("BluetoothActivity...onActivityResult:允许开启蓝牙");
            } else if (resultCode == RESULT_CANCELED) {// NO 用户取消
                Logger.d("BluetoothActivity...onActivityResult:不允许开启蓝牙");
            }
        } else if (requestCode == REQUEST_BT_DISCOVERABLE) {
            if (resultCode == RESULT_OK) {// YES 用户允许
                Logger.d("BluetoothActivity...onActivityResult:y允许被搜寻到");
            } else if (resultCode == RESULT_CANCELED) {// NO 用户取消
                Logger.d("BluetoothActivity...onActivityResult:不允许被搜寻到");
            }
        }
    }

    private void disableBluetooth() {
        if (blAdapter.isEnabled()) {
            blAdapter.disable();
        }
    }

    private void stopDiscover() {
        blAdapter.cancelDiscovery();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (scannerCallback != null) {
                blAdapter.getBluetoothLeScanner().stopScan(scannerCallback);
            }
        } else {
            if (leScanCallback != null) {
                blAdapter.startLeScan(leScanCallback);
            }
        }
    }

    private void startDiscover() {
        if (!blAdapter.isEnabled()) {
            ToastUtil.show("Please open bluetooth.");
            return;
        }
        if (blAdapter.isDiscovering()) {
            ToastUtil.show("Discovering,waiting with patience.");
            return;
        }

        //only this working
        boolean isSuccess = blAdapter.startDiscovery();

        if (isSuccess) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasIt = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            Logger.d("BluetoothActivity...权限ACCESS_COARSE_LOCATION:" + (hasIt == PackageManager.PERMISSION_GRANTED));
            Logger.d("BluetoothActivity...权限FEATURE_BLUETOOTH_LE:" + getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startDiscoverAbove21();
        } else {
            Logger.d("BluetoothActivity...startDiscoverBeneath21");
            leScanCallback = new AdapterScanCallback();
            blAdapter.startLeScan(leScanCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startDiscoverAbove21() {
        BluetoothLeScanner scanner = blAdapter.getBluetoothLeScanner();
        Logger.d("BluetoothActivity...startDiscoverAbove21");
        scannerCallback = new ScannerCallback();
        scanner.startScan(scannerCallback);
    }

    private void initReceiver() {
        receiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    private void startServer() {
//        UUID uuid = UUID.randomUUID();
//        try {
//            BluetoothServerSocket serverSocket = blAdapter.listenUsingRfcommWithServiceRecord("Floatingmuseum", uuid);
//            BluetoothSocket socket = serverSocket.accept();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void startClient() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Logger.d("BluetoothActivity...蓝牙开始扫描");
                    tvDiscoverState.setText("扫描状态:扫描中.");
                    discoverDevice.clear();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Logger.d("BluetoothActivity...蓝牙扫描结束");
                    tvDiscoverState.setText("扫描状态:扫描结束.");
                    initDiscoverDevice();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    //always contains
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothClass extraClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                    //Can contain the extra fields EXTRA_NAME and/or EXTRA_RSSI if they are available.
                    String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) -1);
                    Logger.d("BluetoothActivity...蓝牙发现设备...device:" + device + "...address:" + device.getAddress() + "...name:" + name + "...rssi:" + rssi);
                    discoverDevice.put(device.getAddress(), device);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    int oldState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
                    Logger.d("BluetoothActivity...蓝牙状态改变...newState:" + getBluetoothState(newState) + "...oldState:" + getBluetoothState(oldState));
                    break;
            }
        }
    }

    private String getBluetoothState(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                return "State on.";
            case BluetoothAdapter.STATE_OFF:
                return "State off.";
            case BluetoothAdapter.STATE_TURNING_ON:
                return "State turning on.";
            case BluetoothAdapter.STATE_TURNING_OFF:
                return "State turning off.";
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ScannerCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothDevice device = result.getDevice();
                Logger.d("BluetoothActivity...onScanResult...Name:" + device.getName() + "...Address:" + device.getAddress() + "...CallbackType:" + callbackType);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Logger.d("BluetoothActivity...onBatchScanResults...Results:" + results.size());
            for (ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                Logger.d("BluetoothActivity...onBatchScanResults...Name:" + device.getName() + "...Address:" + device.getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Logger.d("BluetoothActivity...onScanFailed:...ErrorCode:" + errorCode);
        }
    }

    private class AdapterScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Logger.d("BluetoothActivity...onLeScan...Name:" + device.getName() + "...Address:" + device.getAddress() + "...Rssi:" + rssi + "..." + scanRecord);
        }
    }

    private class ServerThread extends Thread {

        public ServerThread() {
            try {
                serverSocket = blAdapter.listenUsingRfcommWithServiceRecord("Floatingmuseum", myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                serverConnectSocket = serverSocket.accept();
                InputStream inputStream = serverConnectSocket.getInputStream();
                while (true) {
                    byte[] buffer = new byte[1024];
                    int count = inputStream.read(buffer);
                    Message msg = new Message();
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Logger.d("BluetoothActivity...handler...message:" + String.valueOf(msg.obj));
            ToastUtil.show(String.valueOf(msg.obj));
            super.handleMessage(msg);
        }
    };
}
