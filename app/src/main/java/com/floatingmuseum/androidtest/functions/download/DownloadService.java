package com.floatingmuseum.androidtest.functions.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_STOP_ALL = "ACTION_STOP_ALL";
    public static final String EXTRA_URL = "EXTRA_URL";
    //    private List<TaskInfo> tasks;
    private Map<String, DownloadTask> tasks;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tasks = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {//开始
            String downloadUrl = intent.getStringExtra(EXTRA_URL);
            Logger.d("DownloadService...ACTION_START:" + downloadUrl);
            startTask(downloadUrl);
        } else if (ACTION_STOP.equals(action)) {//停止
            String downloadUrl = intent.getStringExtra(EXTRA_URL);
            Logger.d("DownloadService...ACTION_STOP:" + downloadUrl);
            stopTask(downloadUrl);
        } else if (ACTION_STOP_ALL.equals(action)) {//停止全部
            stopAllTask();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTask(String downloadUrl) {
        DownloadTask downloadTask = new DownloadTask(this, downloadUrl);
        tasks.put(downloadUrl, downloadTask);
        downloadTask.start();
    }

    private void stopTask(String downloadUrl) {
        if (tasks.containsKey(downloadUrl)) {
            //停止线程
            tasks.get(downloadUrl).stop();
            //移除任务
            tasks.remove(downloadUrl);
        }
    }

    private void stopAllTask() {
        for (String key : tasks.keySet()) {
            tasks.get(key).stop();
        }
    }
}
