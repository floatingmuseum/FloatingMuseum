package com.floatingmuseum.androidtest.thirdpartys.exo;

/**
 * Created by Floatingmuseum on 2017/7/5.
 */

public class MusicInfo {
    private long id;
    private String title;
    private String artist;
    private String album;
    private String uri;
    private long duration;
    private long albumID;
    private long fileSize;
    private int year;

    public MusicInfo(long id, String title, String artist, String album, String uri, long duration, long albumID, long fileSize, int year) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.uri = uri;
        this.duration = duration;
        this.albumID = albumID;
        this.fileSize = fileSize;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getAlbumID() {
        return albumID;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", uri='" + uri + '\'' +
                ", duration=" + duration +
                ", albumID=" + albumID +
                ", fileSize=" + fileSize +
                ", year=" + year +
                '}';
    }
}
