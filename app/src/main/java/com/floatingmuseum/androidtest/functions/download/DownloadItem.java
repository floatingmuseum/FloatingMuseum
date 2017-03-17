package com.floatingmuseum.androidtest.functions.download;

/**
 * Created by Floatingmuseum on 2017/3/8.
 */

public class DownloadItem {
    private String fileName;
    private String url;
    private int percent;
    //定义下载状态常量 DownloadManager中的状态
//    public static final int NONE = 0;         //无状态  --> 等待
//    public static final int WAITING = 1;      //等待    --> 下载，暂停
//    public static final int DOWNLOADING = 2;  //下载中  --> 暂停，完成，错误
//    public static final int PAUSE = 3;        //暂停    --> 等待，下载
//    public static final int FINISH = 4;       //完成    --> 重新下载
//    public static final int ERROR = 5;        //错误    --> 等待
    private int downloadState;
    private long netSpeed;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public long getNetSpeed() {
        return netSpeed;
    }

    public void setNetSpeed(long netSpeed) {
        this.netSpeed = netSpeed;
    }
}
