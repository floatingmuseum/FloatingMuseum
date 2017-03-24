package com.floatingmuseum.androidtest.functions.nsd;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Floatingmuseum on 2017/3/24.
 */

public class NsdClientActivity extends BaseActivity implements View.OnClickListener, Runnable {


        public static final String SERVICE_TYPE = "_http._tcp.";
        private Button mBtnScan;
        private Toolbar mToolbar;
        private Adapter adapter;
        private NsdManager.DiscoveryListener mDiscoveryListener;
        private Socket mSocket;
        private NsdManager nsdManager;
        private NsdManager.ResolveListener mResolverListener;
        private NsdServiceInfo mNsdServiceInfo;
        private BufferedWriter bufferedWriter;
        private RecyclerView mReycleView;
        private ArrayList<NsdServiceInfo> datas = new ArrayList<>();
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                adapter.notifyItemInserted(datas.size());
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nsd_client);
            init();
            createDiscoverListener();
            createResolverListener();
        }

        private void init() {
            nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
//            setSupportActionBar(mToolbar);
            mBtnScan = (Button) findViewById(R.id.btn_scan);
            mBtnScan.setOnClickListener(this);
            mReycleView = (RecyclerView) findViewById(R.id.recycleView);
            mReycleView.setLayoutManager(new LinearLayoutManager(NsdClientActivity.this, LinearLayoutManager.VERTICAL, false));
            adapter = new Adapter(datas);
            mReycleView.setAdapter(adapter);
            adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
                @Override
                public void onClick(NsdServiceInfo nsdServiceInfo) {
                    //获得NsdServiceInfo的详细信息
                    nsdManager.resolveService(nsdServiceInfo, mResolverListener);
                }
            });
        }


        @Override
        public void onClick(View view) {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        }

        private void createResolverListener() {
            mResolverListener = new NsdManager.ResolveListener() {
                @Override
                public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                    ToastUtil.show("onResolveFailed");
                }

                @Override
                public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                    mNsdServiceInfo = nsdServiceInfo;
                    new Thread(NsdClientActivity.this).start();
                }
            };
        }

        private void createDiscoverListener() {
            mDiscoveryListener = new NsdManager.DiscoveryListener() {
                @Override
                public void onStartDiscoveryFailed(String s, int i) {
                    ToastUtil.show("onStartDiscoveryFailed");
                }

                @Override
                public void onStopDiscoveryFailed(String s, int i) {
                    ToastUtil.show("onStopDiscoveryFailed");
                }

                @Override
                public void onDiscoveryStarted(String s) {
                    ToastUtil.show("onDiscoveryStarted");
                }

                @Override
                public void onDiscoveryStopped(String s) {
                    ToastUtil.show("onDiscoveryStopped");
                }

                @Override
                public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                    ToastUtil.show("onServiceFound");
                    //这里的nsdServiceInfo只能获取到名字,ip和端口都不能获取到,要想获取到需要调用NsdManager.resolveService方法
                    datas.add(nsdServiceInfo);
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                    ToastUtil.show("onServiceLost");
                }
            };
        }

        @Override
        public void run() {
            try {
//            mNsdServiceInfo.getHost()通过这个方法获得的ip前线带有"/",所有还要调用getHostAddress,我在这里调了好久才发现
                mSocket = new Socket(mNsdServiceInfo.getHost().getHostAddress(), mNsdServiceInfo.getPort());
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                bufferedWriter.write("我连上你了!" + "\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            nsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
}
