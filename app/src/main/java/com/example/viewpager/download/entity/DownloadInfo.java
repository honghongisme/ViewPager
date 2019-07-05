package com.example.viewpager.download.entity;

public class DownloadInfo {

    private String url;
    private String path;
    private int state;
    private long progress;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", state=" + state +
                ", progress=" + progress +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public int getState() {
        return state;
    }

    public long getProgress() {
        return progress;
    }
}
