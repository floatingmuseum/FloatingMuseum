package com.floatingmuseum.androidtest;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.github.moduth.blockcanary.BlockCanary;
import com.orhanobut.logger.Logger;
import com.wanjian.cockroach.Cockroach;

import java.net.Proxy;

import floatingmuseum.sonic.Sonic;
import io.realm.Realm;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class App extends MultiDexApplication {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

//        OkGo.init(this);
        Realm.init(this);
        Sonic.getInstance().init(this);
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        initFileDownloader();
//        initCockroach();
    }

    private void initFileDownloader() {
//        FileDownloader.init(getApplicationContext(), new DownloadMgrInitialParams.InitCustomMaker()
//                .connectionCreator(new FileDownloadUrlConnection
//                        .Creator(new FileDownloadUrlConnection.Configuration()
//                        .connectTimeout(15_000) // set connection timeout.
//                        .readTimeout(15_000) // set read timeout.
//                        .proxy(Proxy.NO_PROXY) // set proxy
//                )));
//        int i = 15_000;
    }

    private void initCockroach() {
        Cockroach.install(new Cockroach.ExceptionHandler() {

            // handlerException内部建议手动try{  你的异常处理逻辑  }catch(Throwable e){ } ，以防handlerException内部再次抛出异常，导致循环调用handlerException

            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            throwable.printStackTrace();
                            Logger.e("AndroidRuntime--->CockroachException:" + thread + "<---" + throwable);
                            ToastUtil.show("Exception Happend\n" + thread + "\n" + throwable.toString());
//                        throw new RuntimeException("..."+(i++));
                        } catch (Throwable e) {

                        }
                    }
                });
            }
        });
    }
}
