package com.floatingmuseum.androidtest.functions.download;

import android.os.Environment;

import com.floatingmuseum.androidtest.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DownloadThread extends Thread {

    //下载文件夹路径
    private String downloadDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/";
    private FileInfo fileInfo;
    private ThreadInfo threadInfo;
    private DBUtil dbUtil;
    private ThreadCallback callback;

    public DownloadThread(FileInfo fileInfo, ThreadInfo threadInfo, DBUtil dbUtil, ThreadCallback callback) {
        this.fileInfo = fileInfo;
        this.threadInfo = threadInfo;
        this.callback = callback;
        this.dbUtil = dbUtil;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        try {
            if (!dbUtil.isExists(threadInfo.getUrl(), threadInfo.getId())) {
                dbUtil.insertOrUpdate(threadInfo);
            }
            //获取文件长度
            URL url = new URL(threadInfo.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            long startPosition = threadInfo.getCurrentPosition();
            long currentPosition = threadInfo.getCurrentPosition();
            connection.setRequestProperty("Range", "bytes=" + startPosition + "-" + threadInfo.getEndPosition());
            long contentLength = -1;
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 206) {
                contentLength = connection.getContentLength();
            }

            if (contentLength <= 0) {
                return;
            }

            File dir = new File(downloadDirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //创建文件
            File file = new File(dir, FileUtil.getUrlFileName(threadInfo.getUrl()));
            //操作的文件，和可操作的模式，读写删
            randomAccessFile = new RandomAccessFile(file, "rwd");
            //设置长度
            randomAccessFile.setLength(contentLength);
            fileInfo.setFileSize(contentLength);
            callback.onFileLength(fileInfo);
            //设置写入位置
            randomAccessFile.seek(startPosition);
            //开始写入
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, len);
                currentPosition += len;
                threadInfo.setCurrentPosition(startPosition);
                callback.onProgress(threadInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
