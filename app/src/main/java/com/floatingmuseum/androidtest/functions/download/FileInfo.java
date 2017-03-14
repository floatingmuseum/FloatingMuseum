package com.floatingmuseum.androidtest.functions.download;

import java.io.Serializable;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class FileInfo implements Serializable{

    private int id;
    private String url;
    private String filename;
    private long fileSize;
    private long currentSize;

    public FileInfo() {
    }

    public FileInfo(int id, String url, String filename, long fileSize, long currentSize) {
        this.id = id;
        this.url = url;
        this.filename = filename;
        this.fileSize = fileSize;
        this.currentSize = currentSize;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", filename='" + filename + '\'' +
                ", fileSize=" + fileSize +
                ", currentSize=" + currentSize +
                '}';
    }
}
