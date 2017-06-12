package com.floatingmuseum.androidtest.functions.aidl;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.floatingmuseum.androidtest.App;
import com.orhanobut.logger.Logger;

import java.util.Random;

import floatingmuseum.userhunter.RemoteHunter;

/**
 * Created by Floatingmuseum on 2017/6/12.
 */

public class AidlService extends Service {

    public static final String ACTION_START_REMOTE_ACTIVITY = "startRemoteActivity";
    public static final String ACTION_BIND_REMOTE_SERVICE = "bindRemoteService";
    public static final String ACTION_SEND_REMOTE_MESSAGE = "sendRemoteMessage";
    public static final String ACTION_STOP_SERVICE = "stopService";

    private RemoteHunter remoteHunter;
    private boolean isDisconnected = false;
    private int lostTask = -1;

    public static void sendCommand(String command) {
        Logger.d("远程猎手...命令:" + command);
        Intent intent = new Intent(App.context, AidlService.class);
        intent.setAction(command);
        App.context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Logger.d("远程猎手...onStartCommand...action:" + action);
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case ACTION_START_REMOTE_ACTIVITY:
                    startRemoteActivity();
                    break;
                case ACTION_BIND_REMOTE_SERVICE:
                    bindRemoteService();
                    break;
                case ACTION_SEND_REMOTE_MESSAGE:
                    sendMessage();
                    break;
                case ACTION_STOP_SERVICE:
                    stopSelf();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startRemoteActivity() {
        Intent intent = getLauncherIntent("floatingmuseum.userhunter");
        startActivity(intent);
    }

    private void bindRemoteService() {
        Intent serviceIntent = new Intent()
                .setComponent(new ComponentName(
                        "floatingmuseum.userhunter",
                        "floatingmuseum.userhunter.RemoteHunterService"));
        startService(serviceIntent);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);
    }

    private void sendMessage() {
        if (remoteHunter == null) {
            return;
        }
        Logger.d("远程猎手...sendMessage:isDisconnected..." + isDisconnected);
        int x = new Random().nextInt(20);
        int y = new Random().nextInt(20);
        if (isDisconnected) {
            //连接已断开,保存发送的信息,重新绑定service,绑定成功后取出遗留信息发送
            lostTask = 1;
            lostX = x;
            lostY = y;
            Logger.d("远程猎手...sendMessage:保存遗留任务...x:" + lostX + "...y:" + lostY);
            bindRemoteService();
            return;
        }
        addTest(x, y);
    }

    private int lostX = 0;
    private int lostY = 0;

    private void addTest(int x, int y) {
        try {
            String message = "Ask RemoteHunter:" + x + "+" + y + "=?";
            Logger.d("远程猎手...addTest:" + message);
            int result = remoteHunter.add(x, y);
            Logger.d("远程猎手...addTest:" + result);
//            TextView newSendMessage = new TextView(this);
//            newSendMessage.setText(message);
//            llLogContainer.addView(newSendMessage);
//            TextView newResultMessage = new TextView(this);
//            newResultMessage.setText("RemoteHunter answer is " + result);
//            llLogContainer.addView(newResultMessage);
//            scrollview.fullScroll(ScrollView.FOCUS_DOWN);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static Intent getLauncherIntent(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageManager manager = App.context.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            Logger.d("是否含有Launcher:" + intent.toString());
            return intent;
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connection != null) {
            unbindService(connection);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d("远程猎手...onServiceConnected:" + name + "...Pid:" + Process.myPid());
            remoteHunter = RemoteHunter.Stub.asInterface(service);
            isDisconnected = false;
            Logger.d("远程猎手...onServiceConnected...是否存在遗留任务:" + lostTask);
            if (lostTask != -1) {
                //发送遗留信息
                Logger.d("远程猎手...onServiceConnected...遗留任务值:...lostX:" + lostX + "...lostY:" + lostY);
                addTest(lostX, lostY);
                lostX = 0;
                lostY = 0;
                lostTask = -1;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d("远程猎手...onServiceDisconnected:" + name);
            //远程连接断开时重启remoteActivity,因为如果是被用户手动在设置里强行停止,不重启remoteActivity无法重新绑定远程service
            //不在这里进行重新绑定,而是在下次发送信息时进行重新绑定,在此绑定无效.
            startRemoteActivity();
            isDisconnected = true;
        }
    };
}
