package com.floatingmuseum.androidtest.functions.download;

import android.content.Context;

import com.floatingmuseum.androidtest.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadTask implements InitCallback {

    private Context context;
    private String downloadUrl;
    private DBUtil dbUtil;
    private List<DownloadThread> threads;
    private ThreadCallback callback;
    private int threadCount = 3;

    public DownloadTask(Context context, String downloadUrl, ThreadCallback callback) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        dbUtil = new DBUtil(context);
        threads = new ArrayList<>();
        this.callback = callback;
        initDownloadThread();
    }

    private void initDownloadThread() {
        List<ThreadInfo> threadInfoList = dbUtil.getAllThreadInfo(downloadUrl);
        Logger.d("DownloadService...initDownloadThread:" + threadInfoList.size());

        if (threadInfoList.size() == 0) {//Means first time.
            //获取文件长度
            new InitThread(downloadUrl, this).start();
        } else {
            initDownloadThreadInfo(threadInfoList);
        }
    }

    private void initDownloadThreadInfo(List<ThreadInfo> threadInfoList) {
        Logger.d("DownloadService...initDownloadThreadInfo:" + threadInfoList.size());
        for (ThreadInfo info : threadInfoList) {
            Logger.d("DownloadService...线程" + info.getId() + "号...初始位置:" + info.getStartPosition() + "...当前位置:" + info.getCurrentPosition() + "...末尾位置:" + info.getEndPosition());
            DownloadThread thread = new DownloadThread(info, dbUtil, callback);
            threads.add(thread);
        }
    }

    public void start() {
        Logger.d("DownloadService...start:" + threads.size());
        for (DownloadThread thread : threads) {
            thread.start();
        }
    }

    public void stop() {
        for (DownloadThread thread : threads) {
            thread.stopThread();
        }
    }

    @Override
    public void onGetContentLength(long contentLength) {
        Logger.d("DownloadService...onGetContentLength总文件大小:" + contentLength + "..." + FileUtil.bytesToMb(contentLength) + "mb");
        List<ThreadInfo> threadInfoList = new ArrayList<>();
        long blockLength = contentLength / threadCount;

        for (int x = 1; x <= threadCount; x++) {
            long start = x == 1 ? 0 : blockLength * (x - 1) + 1;
            long end = x == threadCount ? contentLength : blockLength * x;
            long current = start;
            ThreadInfo threadInfo = new ThreadInfo(x, downloadUrl, start, end, current, contentLength);
            threadInfoList.add(threadInfo);
        }
        //区块大小
//        ThreadInfo threadInfo1 = new ThreadInfo(1, downloadUrl, 0, blockLength, 0, contentLength);
//        ThreadInfo threadInfo2 = new ThreadInfo(2, downloadUrl, blockLength + 1, blockLength * 2, blockLength + 1, contentLength);
//        ThreadInfo threadInfo3 = new ThreadInfo(3, downloadUrl, blockLength * 2 + 1, contentLength, blockLength * 2 + 1, contentLength);
//
//        threadInfoList.add(threadInfo1);
//        threadInfoList.add(threadInfo2);
//        threadInfoList.add(threadInfo3);
        initDownloadThreadInfo(threadInfoList);
        start();
    }

    @Override
    public void onError() {

    }
}
