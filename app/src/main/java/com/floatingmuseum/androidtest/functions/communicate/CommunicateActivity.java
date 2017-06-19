package com.floatingmuseum.androidtest.functions.communicate;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Floatingmuseum on 2017/3/17.
 * <p>
 * Using Google connections api
 * https://developers.google.cn/nearby/connections/overview
 * failed on most devices
 */

public class CommunicateActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    @BindView(R.id.et_send_message)
    EditText etSendMessage;
    @BindView(R.id.bt_send_message)
    Button btSendMessage;
    @BindView(R.id.bt_start_advertising)
    Button btStartAdvertising;
    @BindView(R.id.bt_start_discovering)
    Button btStartDiscovering;
    @BindView(R.id.bt_stop_advertising)
    Button btStopAdvertising;
    @BindView(R.id.bt_stop_discovering)
    Button btStopDiscovering;
    @BindView(R.id.rv_found_list)
    RecyclerView rvFoundList;
    @BindView(R.id.et_nickname)
    EditText etNickname;

    private GoogleApiClient googleApiClient;
    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};
    private String serviceId;
    private List<String> foundList = new ArrayList<>();
    private FoundListAdapter adapter;
    private String myName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);
        ButterKnife.bind(this);
        initView();
        serviceId = getString(R.string.floating_museum_service_id);
        Logger.d("CommunicateActivity...isGooglePlayServicesAvailable:" + checkPlayServices());
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
        googleApiClient.connect();
    }

    private int PLAY_SERVICES_RESOLUTION_REQUEST = 233;

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }

        return true;
    }

    private void initView() {
        btStartAdvertising.setOnClickListener(this);
        btStartDiscovering.setOnClickListener(this);
        btStopAdvertising.setOnClickListener(this);
        btStopDiscovering.setOnClickListener(this);
        btSendMessage.setOnClickListener(this);
        rvFoundList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoundListAdapter(foundList);
        rvFoundList.setAdapter(adapter);
        rvFoundList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                String endpointID = foundList.get(position);
                startConnectTo(endpointID);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start_advertising:
                startAdvertising();
                break;
            case R.id.bt_start_discovering:
                startDiscovery();
                break;
            case R.id.bt_stop_advertising:
                Nearby.Connections.stopAdvertising(googleApiClient);
                break;
            case R.id.bt_stop_discovering:
                Nearby.Connections.stopDiscovery(googleApiClient);
                break;
            case R.id.bt_send_message:
                CharSequence cs = etSendMessage.getText();
                if (TextUtils.isEmpty(cs)) {
                    ToastUtil.show("先生还是写点什么吧.");
                    return;
                } else {
                    sendMessage(cs.toString());
                }
                break;
        }
    }

    private void sendMessage(String message) {
        Logger.d("CommunicateActivity...sendMessage():...Message:" + message);
        try {
            Nearby.Connections.sendPayload(googleApiClient, message, Payload.fromBytes(message.getBytes("UTF-8")))
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Logger.d("CommunicateActivity...sendMessage():...result:" + status.toString());
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            Logger.d("CommunicateActivity...sendMessage():...UnsupportedEncodingException");
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        // TODO: 2017/3/17 在某些设备(比如1+1)即使回调此方法，之后使用其他功能，仍然告知 java.lang.IllegalStateException: GoogleApiClient is not connected yet.
        Logger.d("CommunicateActivity...onConnected:" + connectionHint + "...isConnected:" + googleApiClient.isConnected());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Logger.d("CommunicateActivity...onConnectionSuspended:" + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d("CommunicateActivity...onConnected:" + connectionResult.toString());
    }

    private void startAdvertising() {
        if (!isConnectedToNetwork()) {//连接失败
            // Implement logic when device is not connected to a network
            Logger.d("CommunicateActivity...广播...startAdvertising:...not connect to network");
        }

        Editable editable = etNickname.getText();
        if (TextUtils.isEmpty(editable)) {
            ToastUtil.show("Where is your nickname.");
            return;
        }
        myName = editable.toString();
        // Identify that this device is the host
        Logger.d("CommunicateActivity...广播...startAdvertising:...Nearby.Connections.startAdvertising");
        Nearby.Connections
                .startAdvertising(googleApiClient, myName, serviceId, myConnectionLifecycleCallback, new AdvertisingOptions(Strategy.P2P_CLUSTER))
                .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                    @Override
                    public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                        Logger.d("CommunicateActivity...广播...onResult():" + result.getLocalEndpointName());
                    }
                });
    }

    private void startDiscovery() {
        if (!isConnectedToNetwork()) {//连接失败
            // Implement logic when device is not connected to a network
            Logger.d("CommunicateActivity...搜寻...startDiscovery:...not connect to network");
        }

        Logger.d("CommunicateActivity...搜寻...startDiscovery:...Nearby.Connections.startDiscovery");
        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections
                .startDiscovery(googleApiClient, serviceId, endpointDiscoveryCallback, new DiscoveryOptions(Strategy.P2P_CLUSTER))
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            // We're discovering!
                            Logger.d("CommunicateActivity...搜寻...onResult():开始搜寻");
                        } else {
                            // We were unable to start discovering.
                            Logger.d("CommunicateActivity...搜寻...onResult():无法开始搜寻");
                        }
                    }
                });
    }

    private void startConnectTo(String endpointID) {
        Nearby.Connections
                .requestConnection(googleApiClient, myName, endpointID, myConnectionLifecycleCallback)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Logger.d("CommunicateActivity...连接其他...请求成功...等待认证");
                            // We successfully requested a connection. Now both sides
                            // must accept before the connection is established.
                        } else {
                            Logger.d("CommunicateActivity...连接其他...请求失败" + status.toString());
                            // Nearby Connections failed to request the connection.
                        }
                    }
                });
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    private void handleConnectRequest(final String endpointId, final ConnectionInfo connectionInfo) {
        new AlertDialog.Builder(this)
                .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                .setMessage("Confirm if the code " + connectionInfo.getAuthenticationToken() + " is also displayed on the other device")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user confirmed, so we can accept the connection.
                        Logger.d("CommunicateActivity...广播...允许连接:...endpointId:" + endpointId + "...endpointName:" + connectionInfo.getEndpointName());
                        Nearby.Connections.acceptConnection(googleApiClient, endpointId, payloadCallback);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user canceled, so we should reject the connection.
                        Logger.d("CommunicateActivity...广播...拒绝连接:...endpointId:" + endpointId + "...endpointName:" + connectionInfo.getEndpointName());
//                        Nearby.Connections.rejectConnection(googleApiClient, payloadCallback);
                        Nearby.Connections.rejectConnection(googleApiClient, "拒绝访问");
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Logger.d("CommunicateActivity...onDestroy:...isConnected:" + googleApiClient.isConnected());
        }
    }

    private ConnectionLifecycleCallback myConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            Logger.d("CommunicateActivity...广播...onConnectionInitiated():...endpointId:" + endpointId + "...name:" + connectionInfo.getEndpointName() + "...token:" + connectionInfo.getAuthenticationToken() + "...isIncoming:" + connectionInfo.isIncomingConnection());
            handleConnectRequest(endpointId, connectionInfo);
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution connectionResolution) {
            Logger.d("CommunicateActivity...广播...onConnectionResult():...endpointId:" + endpointId + "...Status:" + connectionResolution.getStatus().toString());
            switch (connectionResolution.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    Logger.d("CommunicateActivity...广播...onConnectionResult():...endpointId:" + endpointId + "...StatusOK:连接成功");
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    Logger.d("CommunicateActivity...广播...onConnectionResult():...endpointId:" + endpointId + "...StatusRejected:连接被拒绝");
                    break;
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            Logger.d("CommunicateActivity...广播...onDisconnected():...endpointId:" + endpointId);
        }
    };

    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(String endpointID, DiscoveredEndpointInfo discoveredEndpointInfo) {
            Logger.d("CommunicateActivity...搜寻...onEndpointFound()::" + endpointID + "..." + discoveredEndpointInfo.getEndpointName());
            foundList.add(endpointID);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onEndpointLost(String endpointID) {
            Logger.d("CommunicateActivity...搜寻...onEndpointLost():丢失:" + endpointID);
            foundList.remove(endpointID);
            adapter.notifyDataSetChanged();
        }
    };

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointID, Payload payload) {
            Logger.d("CommunicateActivity...信息传送...onPayloadReceived():endpointID:" + endpointID + "...Type:" + payload.getType());
            if (Payload.Type.BYTES == payload.getType()) {
                Logger.d("CommunicateActivity...信息传送...onPayloadReceived():endpointID:" + endpointID + "...Message:" + payload.asBytes().toString());
            }
        }

        @Override
        public void onPayloadTransferUpdate(String endpointID, PayloadTransferUpdate payloadTransferUpdate) {
            Logger.d("CommunicateActivity...信息传送...onPayloadTransferUpdate():endpointID:" + endpointID + "...current:" + payloadTransferUpdate.getBytesTransferred() + "...total:" + payloadTransferUpdate.getTotalBytes());
        }
    };
}
