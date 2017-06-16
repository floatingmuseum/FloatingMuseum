package com.floatingmuseum.androidtest.functions.otherprocess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.floatingmuseum.androidtest.utils.RxUtil;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import floatingmuseum.userhunter.RemoteMuseum;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Floatingmuseum on 2017/6/13.
 */

public class OtherProcessService extends Service {

    private Disposable disposable;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("RemoteMuseum:Service...onCreate()");
        startCheck();
    }

    private void startCheck() {
        if (disposable == null || disposable.isDisposed()) {
            disposable = Flowable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            Logger.d("RemoteMuseum:Service...服务还活着" + aLong);
                        }
                    });
        }
    }

    private void stopCheck() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("RemoteMuseum:Service...onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCheck();
        Logger.d("RemoteMuseum:Service...onDestroy()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return remoteMuseum;
    }

    private RemoteMuseum.Stub remoteMuseum = new RemoteMuseum.Stub() {
        @Override
        public void sendMessage(String message) throws RemoteException {
            Logger.d("RemoteMuseum:sendMessage" + message);
        }
    };
}
