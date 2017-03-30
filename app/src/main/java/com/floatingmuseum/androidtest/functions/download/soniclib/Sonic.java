package com.floatingmuseum.androidtest.functions.download.soniclib;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.floatingmuseum.androidtest.utils.FileUtil;
import com.floatingmuseum.androidtest.utils.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Floatingmuseum on 2017/3/16.
 */

public class Sonic {

    private static final String TAG = Sonic.class.getName();
    private static Context context;
    private static Sonic sonic;
    private String dirPath;
    private Map<String, DownloadTask> tasks;
    private DownloadListener listener;
    private final DBManager dbManager;
    private final DownloadManager downloadManager;

    private Sonic() {
        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//        Log.i(TAG, "Default save dir path:" + dirPath);
        downloadManager = new DownloadManager();
        dbManager = new DBManager(context);
        List<DownloadTask> allTask = dbManager.getAllDownloadTask();
        tasks = new HashMap<>();
        if (!ListUtil.isEmpty(allTask)) {
            for (DownloadTask downloadTask : allTask) {
                tasks.put(downloadTask.getTag(), downloadTask);
            }
        }
    }

    public static void init(Context applicationContext) {
        context = applicationContext;
    }

    public static Sonic getInstance() {
        if (sonic == null) {
            synchronized (Sonic.class) {
                if (sonic == null) {
                    sonic = new Sonic();
                }
            }
        }
        return sonic;
    }

    public Sonic setMaxThreads(int threadNum) {
        downloadManager.setMaxThreads(threadNum);
        return this;
    }

    public Sonic setActiveTaskNumber(int activeNumber) {
        downloadManager.setActiveTaskNumber(activeNumber);
        return this;
    }

    public Sonic setDirPath(String dirPath) {
        downloadManager.setDirPath(dirPath);
        return this;
    }


    public Sonic registerDownloadListener(DownloadListener listener) {
        this.listener = listener;
        return this;
    }

    public void addTask(String downloadUrl) {
        addTask(downloadUrl, downloadUrl, FileUtil.getUrlFileName(downloadUrl));
    }

    public void addTask(String downloadUrl, String tag) {
        addTask(downloadUrl, tag, FileUtil.getUrlFileName(downloadUrl));
    }

    public void addTask(String downloadUrl, String tag, String fileName) {
        //check is this task inside active tasks map
        if (!tasks.containsKey(tag)) {
            DownloadTask task = new DownloadTask(downloadUrl, tag, fileName, dirPath, dirPath + fileName, 0, 0, 0, 0, 0);
            startDownload(task, false);
        }
    }

    /**
     * start an exist download task
     */
    public void addTask(DownloadTask task) {
        if (tasks.containsKey(task.getTag())) {
            startDownload(task, true);
        }
    }

    private void startDownload(DownloadTask task, boolean isExist) {
        if (!isExist) {
            dbManager.insertTaskInfo(task);
            tasks.put(task.getTag(), task);
        }
    }
}
