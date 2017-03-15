package com.floatingmuseum.androidtest.functions.download;

/**
 * Created by Floatingmuseum on 2017/3/15.
 */

public interface InitCallback {
    void onGetContentLength(long contentLength);
    void onError();
}
