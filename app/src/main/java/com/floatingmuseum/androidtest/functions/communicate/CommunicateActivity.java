package com.floatingmuseum.androidtest.functions.communicate;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.floatingmuseum.androidtest.R;
import com.floatingmuseum.androidtest.base.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.Connections.EndpointDiscoveryListener;
import com.orhanobut.logger.Logger;

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

public class CommunicateActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Connections.MessageListener, View.OnClickListener {

    @BindView(R.id.bt_advertising)
    Button btAdvertising;
    @BindView(R.id.bt_discovering)
    Button btDiscovering;
    @BindView(R.id.et_connect_to_id)
    EditText etConnectToId;
    @BindView(R.id.bt_connect_to)
    Button btConnectTo;
    @BindView(R.id.bt_disconnect_from)
    Button btDisconnectFrom;
    @BindView(R.id.bt_disconnect_all)
    Button btDisconnectAll;
    @BindView(R.id.et_send_message)
    EditText etSendMessage;
    @BindView(R.id.bt_send_message)
    Button btSendMessage;

    private GoogleApiClient googleApiClient;
    private boolean isHost;
    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);
        ButterKnife.bind(this);
        initView();
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
        btAdvertising.setOnClickListener(this);
        btDiscovering.setOnClickListener(this);
        btConnectTo.setOnClickListener(this);
        btDisconnectFrom.setOnClickListener(this);
        btDisconnectAll.setOnClickListener(this);
        btSendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_advertising:
                startAdvertising();
                break;
            case R.id.bt_discovering:
                startDiscovery();
                break;
            case R.id.bt_connect_to:
                String remoteEndpoint = etConnectToId.getText().toString();
                Logger.d("CommunicateActivity...bt_connect_to:" + remoteEndpoint);
                connectTo(remoteEndpoint);
                break;
            case R.id.bt_disconnect_from:
//                Nearby.Connections.disconnectFromEndpoint(googleApiClient, remoteEndpointId);
                break;
            case R.id.bt_disconnect_all:
                Nearby.Connections.stopAllEndpoints(googleApiClient);
                break;
            case R.id.bt_send_message:
                String remoteEndpointId = "";
                Nearby.Connections.sendReliableMessage(googleApiClient, remoteEndpointId, etSendMessage.getText().toString().getBytes());
                break;
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
            Logger.d("CommunicateActivity...startAdvertising:...not connect to network");
        }

        // Identify that this device is the host
        isHost = true;

        // Advertising with an AppIdentifer lets other devices on the
        // network discover this application and prompt the user to
        // install the application.

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // The advertising timeout is set to run indefinitely
        // Positive values represent timeout in milliseconds
        long NO_TIMEOUT = 0L;

        String name = null;
        Logger.d("CommunicateActivity...startAdvertising:...Nearby.Connections.startAdvertising");
        Nearby.Connections.startAdvertising(googleApiClient, name, appMetadata, NO_TIMEOUT, new Connections.ConnectionRequestListener() {
            @Override
            public void onConnectionRequest(String remoteEndpointId, final String remoteEndpointName, byte[] handshakeData) {
                Logger.d("CommunicateActivity...onConnectionRequest...RemoteEndPointId:" + remoteEndpointId + "...RemoteEndPointName" + remoteEndpointName + "...HandShakeData" + handshakeData);
                if (isHost) {
                    byte[] myPayload = null;
                    // Automatically accept all requests
                    Nearby.Connections.acceptConnectionRequest(googleApiClient, remoteEndpointId,
                            myPayload, CommunicateActivity.this).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Logger.d("CommunicateActivity...onConnectionRequest...Connected to " + remoteEndpointName);
                            } else {
                                Logger.d("CommunicateActivity...onConnectionRequest...Failed to connect to:" + remoteEndpointName);
                            }
                        }
                    });
                } else {
                    // Clients should not be advertising and will reject all connection requests.
                    Nearby.Connections.rejectConnectionRequest(googleApiClient, remoteEndpointId);
                }
            }
        }).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    // Device is advertising
                    Logger.d("CommunicateActivity...startAdvertising:..Device is advertising:" + result.getLocalEndpointName());
                } else {
                    int statusCode = result.getStatus().getStatusCode();
                    Logger.d("CommunicateActivity...startAdvertising:..Advertising failed:" + statusCode);
                    // Advertising failed - see statusCode for more details
                }
            }
        });
    }

    private void startDiscovery() {
        if (!isConnectedToNetwork()) {//连接失败
            // Implement logic when device is not connected to a network
            Logger.d("CommunicateActivity...startDiscovery:...not connect to network");
        }
        String serviceId = getString(R.string.floating_museum_service_id);

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = 1000L;

        Logger.d("CommunicateActivity...startDiscovery:...Nearby.Connections.startDiscovery");
        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections.startDiscovery(googleApiClient, serviceId, DISCOVER_TIMEOUT, new EndpointDiscoveryListener() {
            @Override
            public void onEndpointFound(String endpointId, String serviceId, String name) {
                // This device is discovering endpoints and has located an advertiser.
                // Write your logic to initiate a connection with the device at
                // the endpoint ID
                Logger.d("CommunicateActivity...onEndpointFound...EndPointId:" + endpointId + "...ServiceId:" + serviceId + "...Name:" + name);
            }

            @Override
            public void onEndpointLost(String endpointId) {
                Logger.d("CommunicateActivity...onEndpointLost...EndPointId:" + endpointId);
            }
        }).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    // Device is discovering
                    Logger.d("CommunicateActivity...startDiscovery:..Device is discovering:");
                } else {
                    int statusCode = status.getStatusCode();
                    Logger.d("CommunicateActivity...startDiscovery:..Discovering failed:" + statusCode);
                    // Advertising failed - see statusCode for more details
                }
            }
        });
    }

    private void connectTo(String remoteEndpoint) {
        // Send a connection request to a remote endpoint. By passing 'null' for
        // the name, the Nearby Connections API will construct a default name
        // based on device model such as 'LGE Nexus 5'.
        String myName = "Floatingmuseum";
        byte[] myPayload = myName.getBytes();
        Nearby.Connections.sendConnectionRequest(googleApiClient, myName, remoteEndpoint, myPayload, new Connections.ConnectionResponseCallback() {
            @Override
            public void onConnectionResponse(String remoteEndpointId, Status status,
                                             byte[] bytes) {
                if (status.isSuccess()) {
                    Logger.d("CommunicateActivity...connectTo:..onConnectionResponse Success:" + remoteEndpointId + "..." + status.toString() + "..." + bytes);
                    // Successful connection
                } else {
                    Logger.d("CommunicateActivity...connectTo:..onConnectionResponse Failed:" + remoteEndpointId + "..." + status.toString() + "..." + bytes);
                    // Failed connection
                }
            }
        }, this);
    }

    @Override
    public void onMessageReceived(String remoteEndpointId, byte[] payload, boolean isReliable) {
        Logger.d("CommunicateActivity...onMessageReceived...RemoteEndpointId:" + remoteEndpointId + "...Payload:" + payload + "...isReliable:" + isReliable);
    }

    @Override
    public void onDisconnected(String remoteEndpointId) {
        Logger.d("CommunicateActivity...onDisconnected...RemoteEndpointId:" + remoteEndpointId);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Logger.d("CommunicateActivity...onDestroy:...isConnected:" + googleApiClient.isConnected());
        }
    }
}
