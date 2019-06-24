package com.example.viewpager;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.VideoView;

public class MyVideoView extends VideoView {

    private Context mContext;
    private boolean mPrepared;
    private boolean mCompleted;
    private OnVideoRendringListener mListener;

    public MyVideoView(Context context) {
        super(context);
        this.mContext = context;
        setBackgroundResource(R.drawable.loading);
        setTag("VideoView");
    }

    public void setVideoUri(String videoName) {
        if (videoName != null) {
            setVideoURI(Uri.parse("android.resource://"
                    + mContext.getPackageName() + "/raw/"
                    + mContext.getResources().getIdentifier(videoName, "raw", "com.example.viewpager")));
        }
    }

    private void setVideoStateChangeListener() {
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                System.out.println("加载完毕，等待播放");
                mPrepared = true;
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            System.out.println("视频渲染完毕，开始播放");
                            mListener.onRendingFinish();
                        }
                        return true;
                    }
                });
            }
        });
        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                System.out.println("播放结束");
                mCompleted = true;
            }
        });
    }

    public void setOnVideoRendringListener(OnVideoRendringListener listener) {
        this.mListener = listener;
        setVideoStateChangeListener();
    }

    public boolean isPrepared() {
        return mPrepared;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    interface OnVideoRendringListener {
        void onRendingFinish();
    }
}
