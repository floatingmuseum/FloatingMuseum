package com.floatingmuseum.androidtest.functions.download;

/**
 * Created by Floatingmuseum on 2017/3/7.
 */

public interface DownloadProgressListener {
    /**
     * 进度回调
     * @param read
     * @param count
     * @param done
     */
    void update(long read, long count, boolean done);
}
