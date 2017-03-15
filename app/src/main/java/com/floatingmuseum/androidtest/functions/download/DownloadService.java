package com.floatingmuseum.androidtest.functions.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadService extends Service implements ThreadCallback {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String EXTRA_URL = "EXTRA_URL";
    private DownloadTask downloadTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            String downloadUrl = intent.getStringExtra(EXTRA_URL);
            Logger.d("DownloadService...ACTION_START:" + downloadUrl);
            downloadTask = new DownloadTask(this, downloadUrl, this);
            downloadTask.start();
        } else if (ACTION_STOP.equals(action)) {
            String downloadUrl = intent.getStringExtra(EXTRA_URL);
            Logger.d("DownloadService...ACTION_STOP:" + downloadUrl);
            if (downloadTask != null) {
                downloadTask.stop();
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onProgress(ThreadInfo threadInfo) {
//        long start = threadInfo.getStartPosition();
//        long current = threadInfo.getCurrentPosition();
//        long end = threadInfo.getEndPosition();
//        long finished = current-start;
//        long all = end - start;
//        if (all)
        Logger.d("DownloadService...进度更新...Thread:" + threadInfo.getId() + "...StartPos:" + threadInfo.getStartPosition() + "...CurrentPos:" + threadInfo.getCurrentPosition() + "...EndPosition:" + threadInfo.getEndPosition());
    }
}
