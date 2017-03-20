package com.floatingmuseum.androidtest.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Floatingmuseum on 2017/3/20.
 */

public class RxUtil {

    private static ObservableTransformer schedulerTransFormer = new ObservableTransformer() {
        @Override
        public Observable apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * 线程切换
     */
    public static <T> ObservableTransformer<T, T> threadSwitch() {
        return (ObservableTransformer<T, T>) schedulerTransFormer;
    }
}
