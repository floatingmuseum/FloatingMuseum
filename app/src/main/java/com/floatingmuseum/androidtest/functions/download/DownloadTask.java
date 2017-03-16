package com.floatingmuseum.androidtest.functions.download;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.floatingmuseum.androidtest.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadTask implements InitCallback, ThreadCallback {

    private Context context;
    private String downloadUrl;
    private DBUtil dbUtil;
    private List<DownloadThread> threads;
    private ThreadCallback callback;
    private int threadCount = 5;
    private List<ThreadInfo> threadInfoList;
    private Map<Integer, Long> blocksSize;
    private String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/";
    private String fileName;
    private final DownloadInfo downloadInfo;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public DownloadTask(Context context, String downloadUrl) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        dbUtil = new DBUtil(context);
        threads = new ArrayList<>();
        blocksSize = new HashMap<>();
        fileName = FileUtil.getUrlFileName(downloadUrl);

        downloadInfo = new DownloadInfo(downloadUrl, fileName, downloadUrl, dirPath, dirPath + fileName, 0, 0, 0, 0, 0);
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
        long currentLength = 0;
        for (ThreadInfo info : threadInfoList) {
            downloadInfo.setTotalLength(info.getFileSize());
            // TODO: 2017/3/16 如果有线程执行完了，有的没执行完，这里就拿不到正确的size，据估计
            currentLength += (info.getCurrentPosition() - info.getStartPosition());
            blocksSize.put(info.getId(), info.getCurrentPosition() - info.getStartPosition());
            Logger.d("DownloadService...线程" + info.getId() + "号...初始位置:" + info.getStartPosition() + "...当前位置:" + info.getCurrentPosition() + "...末尾位置:" + info.getEndPosition());
            DownloadThread thread = new DownloadThread(info, dirPath, fileName, dbUtil, this);
            threads.add(thread);
        }
        downloadInfo.setCurrentLength(currentLength);
        downloadInfo.setProgress(getProgress(currentLength, downloadInfo.getTotalLength()));
    }

    public void start() {
        Logger.d("DownloadService...start:" + threads.size());
        for (DownloadThread thread : threads) {
            Logger.d("DownloadService...start:" + thread.getName() + "..." + thread.getState() + "..." + thread.isAlive());
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
        threadInfoList = new ArrayList<>();
        long blockLength = contentLength / threadCount;

        for (int x = 1; x <= threadCount; x++) {
            long start = x == 1 ? 0 : blockLength * (x - 1) + 1;
            long end = x == threadCount ? contentLength : blockLength * x;
            long current = start;
            ThreadInfo threadInfo = new ThreadInfo(x, downloadUrl, start, end, current, contentLength);
            threadInfoList.add(threadInfo);
            dbUtil.insertOrUpdate(threadInfo);//第一次初始化，存储线程信息到数据库
        }
        initDownloadThreadInfo(threadInfoList);
        start();
    }

    @Override
    public void onError() {

    }

    @Override
    public void onProgress(ThreadInfo threadInfo) {
//        blocksSize.put(threadInfo.getId(), threadInfo.getCurrentPosition() - threadInfo.getStartPosition());
//        long currentFileSize = 0;
//        for (Integer id : blocksSize.keySet()) {
//            currentFileSize += blocksSize.get(id);
//        }
        Logger.d("DownloadService...进度更新...Thread:" + threadInfo.getId() + "...StartPos:" + threadInfo.getStartPosition() + "...CurrentPos:" + threadInfo.getCurrentPosition() + "...EndPosition:" + threadInfo.getEndPosition());
//        Logger.d("DownloadService...当前进度:" + currentFileSize + "...总长度:" + threadInfo.getFileSize() + "...百分比:" + (double) currentFileSize / (double) threadInfo.getFileSize());
    }

    private void buildMessage(ThreadInfo info, int state) {
        Bundle bundle = new Bundle();
        bundle.putLong("currentLength", 1);
        bundle.putInt("progress", getProgress(1, 1));
        bundle.putInt("state", state);
//        handler.obtainMessage().setData(bundle);
    }

    @Override
    public void onFinished(int threadId) {
        threadCount--;
        if (threadCount == 0) {
            Logger.d("DownloadService...onFinished...下载结束");
        }
    }

    private int getProgress(long currentLength, long totalLength) {
        // TODO: 2017/3/16 不准
        return (int) ((double) currentLength / (double) totalLength);
    }
}
