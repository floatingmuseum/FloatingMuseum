package com.floatingmuseum.androidtest.functions.analysesystem;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/13.
 */

public class AnalyseSystemActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_mac)
    TextView tvMac;
    @BindView(R.id.bt_all_app)
    Button btAllApp;
    @BindView(R.id.bt_all_system_app)
    Button btAllSystemApp;
    @BindView(R.id.bt_all_third_app)
    Button btAllThirdApp;
    @BindView(R.id.bt_all_launcher_app)
    Button btAllLauncherApp;
    @BindView(R.id.bt_all_launcher_system_app)
    Button btAllLauncherSystemApp;
    @BindView(R.id.bt_all_launcher_third_app)
    Button btAllLauncherThirdApp;
    @BindView(R.id.tv_release)
    TextView tvRelease;
    @BindView(R.id.tv_brand)
    TextView tvBrand;
    @BindView(R.id.tv_manufacturer)
    TextView tvManufacturer;
    @BindView(R.id.tv_product)
    TextView tvProduct;
    @BindView(R.id.tv_board)
    TextView tvBoard;
    @BindView(R.id.tv_bootloader)
    TextView tvBootloader;
    @BindView(R.id.tv_cpu_abi)
    TextView tvCpuAbi;
    @BindView(R.id.tv_cpu_abi2)
    TextView tvCpuAbi2;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @BindView(R.id.tv_display)
    TextView tvDisplay;
    @BindView(R.id.tv_fingerprint)
    TextView tvFingerprint;
    @BindView(R.id.tv_hardware)
    TextView tvHardware;
    @BindView(R.id.tv_host)
    TextView tvHost;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_model)
    TextView tvModel;
    @BindView(R.id.tv_radio)
    TextView tvRadio;
    @BindView(R.id.tv_serial)
    TextView tvSerial;
    @BindView(R.id.tv_tags)
    TextView tvTags;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_unknown)
    TextView tvUnknown;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_codename)
    TextView tvCodename;
    @BindView(R.id.tv_incremental)
    TextView tvIncremental;
    @BindView(R.id.tv_sdk_int)
    TextView tvSdkInt;
    private PackageManager pm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_system);
        ButterKnife.bind(this);
        pm = getPackageManager();
        initView();
        initData();
    }

    private void initView() {
        btAllApp.setOnClickListener(this);
        btAllSystemApp.setOnClickListener(this);
        btAllThirdApp.setOnClickListener(this);
        btAllLauncherApp.setOnClickListener(this);
        btAllLauncherSystemApp.setOnClickListener(this);
        btAllLauncherThirdApp.setOnClickListener(this);
    }

    private void initData() {
        String macAddress = SystemUtil.getMacAddress();
        tvMac.setText("Mac地址:" + macAddress);
        tvBrand.setText("Brand:" + Build.BRAND);
        tvManufacturer.setText("Manufacturer:" + Build.MANUFACTURER);
        tvProduct.setText("Product:" + Build.PRODUCT);
        tvBoard.setText("Product:" + Build.PRODUCT);
        tvBootloader.setText("Bootloader:" + Build.BOOTLOADER);
        tvCpuAbi.setText("Cpu_Abi:" + Build.CPU_ABI);
        tvCpuAbi2.setText("Cpu_Abi2:" + Build.CPU_ABI2);
        tvDevice.setText("Device:" + Build.DEVICE);
        tvDisplay.setText("Display:" + Build.DISPLAY);
        tvFingerprint.setText("Fingerprint:" + Build.FINGERPRINT);
        tvHardware.setText("Hardware:" + Build.HARDWARE);
        tvHost.setText("Host:" + Build.HOST);
        tvId.setText("ID:" + Build.ID);
        tvModel.setText("Model:" + Build.MODEL);
        tvRadio.setText("Radio:" + Build.getRadioVersion() + "..." + Build.RADIO);
        tvSerial.setText("Serial:" + Build.SERIAL);
        tvTags.setText("Tags:" + Build.TAGS);
        tvTime.setText("Time:" + Build.TIME);
        tvType.setText("Type:" + Build.TYPE);
        tvUnknown.setText("Unknown:" + Build.UNKNOWN);
        tvUser.setText("User:" + Build.USER);
        tvCodename.setText("Codename:" + Build.VERSION.CODENAME);
        tvIncremental.setText("Incremental:" + Build.VERSION.INCREMENTAL);
        tvRelease.setText("Release:" + Build.VERSION.RELEASE);
        tvSdkInt.setText("SDK_INT:" + Build.VERSION.SDK_INT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_all_app:
                SystemUtil.queryAllApplication(pm);
                break;
            case R.id.bt_all_system_app:
                List<PackageInfo> systemAppList = SystemUtil.queryAllSystemApplication(pm);
                for (PackageInfo info : systemAppList) {
                    ApplicationInfo applicationInfo = info.applicationInfo;
                    Logger.d("PackageInfo:系统应用Label:" + applicationInfo.loadLabel(pm).toString() + "...PackageName:" + applicationInfo.packageName + "...flag:" + applicationInfo.flags);
                }
                break;
            case R.id.bt_all_third_app:
                List<PackageInfo> thirdAppList = SystemUtil.queryAllThirdApplication(pm);
                for (PackageInfo info : thirdAppList) {
                    ApplicationInfo applicationInfo = info.applicationInfo;
                    Logger.d("PackageInfo:第三方应用Label:" + applicationInfo.loadLabel(pm).toString() + "...PackageName:" + applicationInfo.packageName + "...flag:" + applicationInfo.flags);
                }
                break;
            case R.id.bt_all_launcher_app:
                List<ResolveInfo> allLauncherAppList = SystemUtil.queryAllLauncherApplication(pm);
                for (ResolveInfo info : allLauncherAppList) {
                    Logger.d("ResolveInfo:Label:" + info.loadLabel(pm).toString() + "...PackageName:" + info.activityInfo.packageName + "...flag:" + info.activityInfo.flags);
                }
                break;
            case R.id.bt_all_launcher_system_app:
                List<ResolveInfo> allLauncherSystemAppList = SystemUtil.queryAllLauncherSystemApplication(pm);
                for (ResolveInfo info : allLauncherSystemAppList) {
                    Logger.d("ResolveInfo:系统应用Label:" + info.loadLabel(pm).toString() + "...PackageName:" + info.activityInfo.packageName + "...flag:" + info.activityInfo.flags);
                }
                break;
            case R.id.bt_all_launcher_third_app:
                List<ResolveInfo> allLauncherThirdAppList = SystemUtil.queryAllLauncherThirdApplication(pm);
                for (ResolveInfo info : allLauncherThirdAppList) {
                    Logger.d("ResolveInfo:第三方应用Label:" + info.loadLabel(pm).toString() + "...PackageName:" + info.activityInfo.packageName + "...flag:" + info.activityInfo.flags);
                }
                break;
        }
    }
}
