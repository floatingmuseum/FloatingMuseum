package com.floatingmuseum.androidtest.functions.nsd;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.rafakob.nsdhelper.NsdListener;
import com.rafakob.nsdhelper.NsdService;
import com.rafakob.nsdhelper.NsdType;

import java.net.ServerSocket;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Floatingmuseum on 2017/3/24.
 */

public class NsdActivity extends BaseActivity implements View.OnClickListener, NsdListener {

    @BindView(R.id.bt_register)
    Button btRegister;
    @BindView(R.id.bt_discover)
    Button btDiscover;
    private NsdManager nsdManager;
    private RegisterListener registerListener;
    private DiscoverListener discoverListener;
    private ResolveListener resolveListener;
    private com.rafakob.nsdhelper.NsdHelper nsdHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);
        ButterKnife.bind(this);
        initView();
        nsdHelper = new com.rafakob.nsdhelper.NsdHelper(this, this);
        nsdHelper.setLogEnabled(true);
        nsdHelper.setAutoResolveEnabled(true);
        nsdHelper.setDiscoveryTimeout(30);
//        nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
//        registerListener = new RegisterListener();
//        discoverListener = new DiscoverListener();
//        resolveListener = new ResolveListener();
    }

    private void initView() {
        btRegister.setOnClickListener(this);
        btDiscover.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                nsdHelper.registerService("Chat", NsdType.HTTP);
//                registerService();
                break;
            case R.id.bt_discover:
                nsdHelper.startDiscovery(NsdType.HTTP);
//                discoverService();
                break;
        }
    }

    public void registerService() {
        // 注意：注册网络服务时不要对端口进行硬编码，通过如下这种方式为你的网络服务获取
        // 一个可用的端口号.
//        int port = 0;
//        try {
//            ServerSocket sock = new ServerSocket(0);
//            port = sock.getLocalPort();
//            sock.close();
//        } catch (Exception e) {
//            ToastUtil.show("can not set port");
//        }

        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("NsdChat");
        serviceInfo.setServiceType("nsdchat._tcp");
        serviceInfo.setPort(8080);
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registerListener);
    }

    public void unregisterService() {
        nsdHelper.unregisterService();
        nsdHelper.stopDiscovery();
//        nsdManager.stopServiceDiscovery(discoverListener); // 关闭网络发现
//        nsdManager.unregisterService(registerListener);    // 注销网络服务
    }

    private void discoverService() {
        nsdManager.discoverServices("_http._tcp", NsdManager.PROTOCOL_DNS_SD, discoverListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterService();
    }

    private class RegisterListener implements NsdManager.RegistrationListener {

        @Override
        public void onServiceRegistered(NsdServiceInfo info) {
            // Save the service name.  Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
            String serviceName = info.getServiceName();
            Logger.d("NsdActivity...RegisterListener....onServiceRegistered:" + serviceName);
        }

        @Override
        public void onRegistrationFailed(NsdServiceInfo info, int errorCode) {
            // Registration failed!  Put debugging code here to determine why.
            Logger.d("NsdActivity...RegisterListener....onRegistrationFailed:" + getErrorCodeMeaning(errorCode));
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo info) {
            // Service has been unregistered.  This only happens when you call
            // NsdManager.unregisterService() and pass in this listener.
            Logger.d("NsdActivity...RegisterListener....onServiceUnregistered:" + info.getServiceName());
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Unregistration failed.  Put debugging code here to determine why.
            Logger.d("NsdActivity...RegisterListener....onUnregistrationFailed:" + getErrorCodeMeaning(errorCode));
        }
    }

    private class DiscoverListener implements NsdManager.DiscoveryListener {

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Logger.d("NsdActivity...DiscoverListener....onStartDiscoveryFailed:" + serviceType + "..." + getErrorCodeMeaning(errorCode));
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Logger.d("NsdActivity...DiscoverListener....onStopDiscoveryFailed:" + serviceType + "..." + getErrorCodeMeaning(errorCode));
        }

        @Override
        public void onDiscoveryStarted(String serviceType) {
            Logger.d("NsdActivity...DiscoverListener....onDiscoveryStarted:" + serviceType);
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Logger.d("NsdActivity...DiscoverListener....onDiscoveryStopped:" + serviceType);
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            Logger.d("NsdActivity...DiscoverListener....onServiceFound:" + serviceInfo.getServiceName());
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            Logger.d("NsdActivity...DiscoverListener....onServiceLost:" + serviceInfo.getServiceName());
        }
    }

    private class ResolveListener implements NsdManager.ResolveListener {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Logger.d("NsdActivity...ResolveListener....onResolveFailed:" + serviceInfo.getServiceName() + "..." + getErrorCodeMeaning(errorCode));
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Logger.d("NsdActivity...ResolveListener....onServiceResolved:" + serviceInfo.getServiceName());
        }
    }

    private String getErrorCodeMeaning(int errorCode) {
        switch (errorCode) {
            case NsdManager.FAILURE_ALREADY_ACTIVE:
                return "FAILURE_ALREADY_ACTIVE";
            case NsdManager.FAILURE_INTERNAL_ERROR:
                return "FAILURE_INTERNAL_ERROR";
            case NsdManager.FAILURE_MAX_LIMIT:
                return "FAILURE_MAX_LIMIT";
        }
        return "Nothing meaning found.";
    }


    @Override
    public void onNsdRegistered(NsdService nsdService) {
        Logger.d("NsdActivity...NsdHelper....onNsdRegistered:" + nsdService.getName());
    }

    @Override
    public void onNsdDiscoveryFinished() {
        Logger.d("NsdActivity...NsdHelper....onNsdDiscoveryFinished");
    }

    @Override
    public void onNsdServiceFound(NsdService nsdService) {
        Logger.d("NsdActivity...NsdHelper....onNsdServiceFound:" + nsdService.getName());
    }

    @Override
    public void onNsdServiceResolved(NsdService nsdService) {
        Logger.d("NsdActivity...NsdHelper....onNsdServiceResolved:" + nsdService.getName());
    }

    @Override
    public void onNsdServiceLost(NsdService nsdService) {
        Logger.d("NsdActivity...NsdHelper....onNsdServiceLost:" + nsdService.getName());
    }

    @Override
    public void onNsdError(String errorMessage, int errorCode, String errorSource) {
        Logger.d("NsdActivity...NsdHelper....onNsdError:" + errorMessage + "..." + errorCode + "..." + errorSource);
    }
}
