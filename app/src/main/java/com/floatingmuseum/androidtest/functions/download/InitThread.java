package com.floatingmuseum.androidtest.functions.download;

import android.os.Environment;

import com.floatingmuseum.androidtest.utils.FileUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Floatingmuseum on 2017/3/15.
 */

public class InitThread extends Thread {

    private String downloadUrl;
    private InitCallback callback;
    private String downloadDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/";

    public InitThread(String downloadUrl, InitCallback callback) {
        this.downloadUrl = downloadUrl;
        this.callback = callback;
    }

    @Override
    public void run() {
        URL url = null;
        RandomAccessFile randomAccessFile = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 206) {
                long contentLength = connection.getContentLength();
                if (contentLength <= 0) {
                    callback.onError();
                    return;
                }
                File dir = new File(downloadDirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //创建文件
                File file = new File(dir, FileUtil.getUrlFileName(downloadUrl));
                //操作的文件，和可操作的模式，读写删
                randomAccessFile = new RandomAccessFile(file, "rwd");
                //设置长度
                randomAccessFile.setLength(contentLength);
                callback.onGetContentLength(contentLength);
                return;
            } else {
                callback.onError();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
