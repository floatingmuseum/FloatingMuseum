package com.floatingmuseum.androidtest.functions.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/20.
 */

public class BlueToothActivity extends BaseActivity implements View.OnClickListener {

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

    private BluetoothReceiver receiver;
    private BluetoothManager blManager;
    private BluetoothAdapter blAdapter;
    private AdapterScanCallback leScanCallback;
    private ScannerCallback scannerCallback;

    Map<String, BluetoothDevice> discoverDevice = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);

        initView();

        blManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        blAdapter = blManager.getAdapter();
        initBondedDevices();


        initReceiver();
    }

    private void initView() {
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
        Logger.d("BlueToothActivity...已配对设备:" + bondedDevices.size());
        llBondedDeviceContainer.removeAllViews();
        for (BluetoothDevice device : bondedDevices) {
            CardView deviceItem = (CardView) LayoutInflater.from(this).inflate(R.layout.bluetooth_device_item, llBondedDeviceContainer, false);
            TextView tvDeviceName = (TextView) deviceItem.findViewById(R.id.tv_device_name);
            TextView tvDeviceAddress = (TextView) deviceItem.findViewById(R.id.tv_device_address);
            tvDeviceName.setText("设备名称:" + device.getName());
            tvDeviceAddress.setText("设备地址:" + device.getAddress());
            deviceItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            llBondedDeviceContainer.addView(deviceItem);
            Logger.d("BlueToothActivity...已配对设备:" + device.getName() + "..." + device.getAddress() + "..." + device.getBondState());
        }
    }

    private void initDiscoverDevice() {
        llDiscoverDeviceContainer.removeAllViews();
        for (String address : discoverDevice.keySet()) {
            BluetoothDevice device = discoverDevice.get(address);
            CardView deviceItem = (CardView) LayoutInflater.from(this).inflate(R.layout.bluetooth_device_item, llDiscoverDeviceContainer, false);
            TextView tvDeviceName = (TextView) deviceItem.findViewById(R.id.tv_device_name);
            TextView tvDeviceAddress = (TextView) deviceItem.findViewById(R.id.tv_device_address);
            tvDeviceName.setText("设备名称:" + device.getName());
            tvDeviceAddress.setText("设备地址:" + device.getAddress());
            deviceItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            llDiscoverDeviceContainer.addView(deviceItem);
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
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_discover:
                startDiscover();
                break;
            case R.id.bt_cancel_discover:
                stopDiscover();
                break;
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
            Logger.d("BlueToothActivity...权限ACCESS_COARSE_LOCATION:" + (hasIt == PackageManager.PERMISSION_GRANTED));
            Logger.d("BlueToothActivity...权限FEATURE_BLUETOOTH_LE:" + getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startDiscoverAbove21();
        } else {
            Logger.d("BlueToothActivity...startDiscoverBeneath21");
            leScanCallback = new AdapterScanCallback();
            blAdapter.startLeScan(leScanCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startDiscoverAbove21() {
        BluetoothLeScanner scanner = blAdapter.getBluetoothLeScanner();
        Logger.d("BlueToothActivity...startDiscoverAbove21");
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
                    Logger.d("BlueToothActivity...蓝牙开始扫描");
                    tvDiscoverState.setText("扫描状态:扫描中.");
                    discoverDevice.clear();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Logger.d("BlueToothActivity...蓝牙扫描结束");
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
                    Logger.d("BlueToothActivity...蓝牙发现设备...device:" + device + "...address:" + device.getAddress() + "...name:" + name + "...rssi:" + rssi);
                    discoverDevice.put(device.getAddress(), device);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    int oldState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
                    Logger.d("BlueToothActivity...蓝牙状态改变...newState:" + newState + "...oldState:" + oldState);
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ScannerCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothDevice device = result.getDevice();
                Logger.d("BlueToothActivity...onScanResult...Name:" + device.getName() + "...Address:" + device.getAddress() + "...CallbackType:" + callbackType);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Logger.d("BlueToothActivity...onBatchScanResults...Results:" + results.size());
            for (ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                Logger.d("BlueToothActivity...onBatchScanResults...Name:" + device.getName() + "...Address:" + device.getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Logger.d("BlueToothActivity...onScanFailed:...ErrorCode:" + errorCode);
        }
    }

    private class AdapterScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Logger.d("BlueToothActivity...onLeScan...Name:" + device.getName() + "...Address:" + device.getAddress() + "...Rssi:" + rssi + "..." + scanRecord);
        }
    }
}
