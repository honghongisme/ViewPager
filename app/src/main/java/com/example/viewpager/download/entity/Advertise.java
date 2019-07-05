package com.example.viewpager.download.entity;

public class Advertise {

    private String url;
    private String path;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Advertise{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
