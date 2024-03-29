package com.example.viewpager.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;


public class VideoBannerManager {

    @SuppressLint("StaticFieldLeak")
    private static VideoBannerManager mInstance;
    private Context mContext;
    private IVideoAbleView mView;

    private VideoBannerManager(Context context) {
        mContext = context.getApplicationContext();
    }

    static VideoBannerManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VideoBannerManager.class) {
                if (mInstance == null) {
                    mInstance = new VideoBannerManager(context);
                }
            }
        }
        return mInstance;
    }

    public IVideoAbleView getView() {
        if (mView == null) {
            mView = new BannerTextureView(mContext);
        }else {
            recycleView();
        }
        return (IVideoAbleView) mView.getView();
    }

    public void setLocalUri(String videoName) {
        if (videoName != null) {
            mView.setVideoURI(Uri.parse("android.resource://"
                    + mContext.getPackageName() + "/raw/"
                    + mContext.getResources().getIdentifier(videoName, "raw", "com.example.viewpager")));
        }
    }

    public void setNetworkUri(String url) {
        if (url != null) {
            mView.setVideoURI(Uri.parse(url));
        }
    }

    public void addStatusCallback(VideoStatusCallback callback) {
        if (mView != null) {
            mView.addStatusCallback(callback);
        }
    }

    private void recycleView() {
        if (mView != null && mView.getView().getParent() != null) {
            ViewGroup parent = (ViewGroup) mView.getView().getParent();
            parent.removeView((View) mView);
        }
    }

    public boolean isExistView() {
        return mView != null;
    }

    public void start() {
        if (mView != null) {
            mView.start();
        }
    }

    public void pause() {
        if (mView != null) {
            mView.pause();
        }
    }

    public void resume() {
        if (mView != null) {
            mView.resume();
        }
    }

    public int getCuurentPosition() {
        if (mView != null) {
            return mView.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int milliseconds) {
        if (mView != null) {
            mView.seekTo(milliseconds);
        }
    }

    public void stop() {
        if (mView != null) {
            mView.stop();
        }
    }

    public void release() {
        stop();
        mView = null;
    }

}
