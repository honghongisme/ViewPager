package com.example.viewpager.play;

public class PageBean {

    private String path;
    private int type;

    public PageBean(String resName, int type) {
        this.path = resName;
        this.type = type;
    }

    public String getPath() {
        return path;
    }


    public int getType() {
        return type;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
