package com.example.viewpager;

import java.util.ArrayList;
import java.util.List;

public class VideoViewLoader {

    private static VideoViewLoader mInstance;
    private List<MyVideoView> mViewCache;

    private VideoViewLoader() {
        mViewCache = new ArrayList<>(2);
    }

    public static VideoViewLoader getInstance() {
        if (mInstance == null) {
            synchronized (VideoViewLoader.class) {
                if (mInstance == null) {
                    mInstance = new VideoViewLoader();
                }
            }
        }
        return mInstance;
    }

    public MyVideoView getView() {
        if (mViewCache.size() != 0) {
            return mViewCache.get(0);
        }
        return null;
    }

    public void addView(MyVideoView videoView) {
        mViewCache.add(videoView);
    }

}
