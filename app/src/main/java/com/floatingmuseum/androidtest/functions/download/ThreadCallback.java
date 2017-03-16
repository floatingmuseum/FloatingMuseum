package com.floatingmuseum.androidtest.functions.download;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public interface ThreadCallback {

    void onProgress(ThreadInfo threadInfo);

    void onFinished(int threadId);
}
