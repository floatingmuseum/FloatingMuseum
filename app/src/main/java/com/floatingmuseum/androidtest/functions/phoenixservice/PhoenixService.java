package com.floatingmuseum.androidtest.functions.phoenixservice;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.floatingmuseum.androidtest.App;
import com.floatingmuseum.androidtest.utils.RxUtil;
import com.floatingmuseum.androidtest.utils.SystemUtil;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Floatingmuseum on 2017/5/7.
 */

public class PhoenixService extends Service {

    private Disposable disposable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("PhoenixService...onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopMission();
        startMission();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startMission() {
        disposable = Flowable.interval(0,1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Logger.d("PhoenixService...accept:"+aLong+"..."+ SystemUtil.getTopPackageName(App.context, (ActivityManager) App.context.getSystemService(ACTIVITY_SERVICE)));
                    }
                });
    }

    private void stopMission(){
        if (disposable!=null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0) return false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(PhoenixService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMission();
        Logger.d("PhoenixService...onDestroy");
    }
}
