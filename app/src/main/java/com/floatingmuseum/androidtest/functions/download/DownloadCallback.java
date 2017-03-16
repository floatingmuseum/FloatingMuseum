package com.floatingmuseum.androidtest.functions.download;

/**
 * Created by Floatingmuseum on 2017/3/16.
 */

public interface DownloadCallback {

    void onProgress();

    void onPause();

    void onFinish();

    void onError(Throwable e);
}
