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
    public static final String EXTRA_FILE_INFO = "EXTRA_FILE_INFO";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra(EXTRA_FILE_INFO);
            Logger.d("DownloadService...ACTION_START:" + fileInfo.toString());
            DownloadTask downloadTask = new DownloadTask(this, fileInfo, this);
            downloadTask.start();
        } else if (ACTION_STOP.equals(action)) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra(EXTRA_FILE_INFO);
            Logger.d("DownloadService...ACTION_STOP:" + fileInfo.toString());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onFileLength(FileInfo fileInfo) {
        Logger.d("DownloadService...onFileLength:" + fileInfo.toString());
    }

    @Override
    public void onProgress(ThreadInfo threadInfo) {
        Logger.d("DownloadService...onProgress...StartPos:" + threadInfo.getStartPosition() + "...CurrentPos:" + threadInfo.getCurrentPosition() + "...EndPosition:" + threadInfo.getEndPosition());
    }
}
