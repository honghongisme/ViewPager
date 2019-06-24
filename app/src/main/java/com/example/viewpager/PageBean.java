package com.example.viewpager;

public class PageBean {

    private String resName;
    private int Type;

    public PageBean(String resName, int type) {
        this.resName = resName;
        Type = type;
    }

    public String getResName() {
        return resName;
    }


    public int getType() {
        return Type;
    }

}
