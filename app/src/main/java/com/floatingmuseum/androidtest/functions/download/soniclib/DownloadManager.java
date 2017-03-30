package com.floatingmuseum.androidtest.functions.download.soniclib;

/**
 * Created by Floatingmuseum on 2017/3/30.
 */

public class DownloadManager {

    private int threadNum = 3;
    private int activeNumber = 3;
    private String dirPath;

    public DownloadManager(){

    }

    public void setMaxThreads(int threadNum) {
        this.threadNum = threadNum;
    }

    public void setActiveTaskNumber(int activeNumber) {
        this.activeNumber = activeNumber;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }
}
