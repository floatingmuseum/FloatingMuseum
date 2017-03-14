package com.floatingmuseum.androidtest.functions.download;

import android.content.Context;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadTask {

    private Context context;
    private FileInfo fileInfo;
    private DBUtil dbUtil;
    private DownloadThread downloadThread;

    public DownloadTask(Context context, FileInfo fileInfo,ThreadCallback callback) {
        this.context = context;
        this.fileInfo = fileInfo;
        dbUtil = new DBUtil(context);
        downloadThread = new DownloadThread(fileInfo,new ThreadInfo(),dbUtil,callback);
    }

    public void start(){
        downloadThread.start();
    }
}
