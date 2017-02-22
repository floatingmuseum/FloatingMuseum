package com.floatingmuseum.androidtest;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.floatingmuseum.androidtest.utils.ToastUtil;
import com.orhanobut.logger.Logger;
import com.wanjian.cockroach.Cockroach;

/**
 * Created by Floatingmuseum on 2017/2/15.
 */

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        initCockroach();
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
