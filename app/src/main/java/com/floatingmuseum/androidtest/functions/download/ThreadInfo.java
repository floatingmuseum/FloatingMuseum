package com.floatingmuseum.androidtest.functions.download;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class ThreadInfo {

    private int id;
    private String url;
    private long startPosition;
    private long endPosition;
    private long currentPosition;

    public ThreadInfo() {
    }

    public ThreadInfo(int id, String url, long startPosition, long endPosition, long currentPosition) {
        this.id = id;
        this.url = url;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.currentPosition = currentPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", currentPosition=" + currentPosition +
                '}';
    }
}
