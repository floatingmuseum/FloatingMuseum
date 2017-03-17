package com.floatingmuseum.androidtest.functions.download;

/**
 * Created by Floatingmuseum on 2017/3/16.
 */

public class DownloadInfo {

    private String taskTag;
    private String name;
    private String url;
    private String filePath;
    private String dirPath;
    private long totalLength;
    private long currentLength;
    private int progress;
    private long speed;
    private int state;

    public DownloadInfo(String taskTag, String name, String url, String filePath, String dirPath, long totalLength, long currentLength, int progress, long speed, int state) {
        this.taskTag = taskTag;
        this.name = name;
        this.url = url;
        this.filePath = filePath;
        this.dirPath = dirPath;
        this.totalLength = totalLength;
        this.currentLength = currentLength;
        this.progress = progress;
        this.speed = speed;
        this.state = state;
    }

    public String getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(String taskTag) {
        this.taskTag = taskTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
