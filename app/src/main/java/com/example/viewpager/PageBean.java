package com.example.viewpager;

public class PageBean {

    private String url;
    private int Type;

    public PageBean(String resName, int type) {
        this.url = resName;
        Type = type;
    }

    public String getUrl() {
        return url;
    }


    public int getType() {
        return Type;
    }

}
