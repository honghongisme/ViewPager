package com.example.viewpager;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.VideoView;

public class BannerVideoView extends VideoView implements IVideoAbleView{

    private VideoStatusCallback mCallback;

    public BannerVideoView(Context context) {
        super(context);
        initListener();
    }

    public void setVideoStatusCallback(VideoStatusCallback callback) {
        this.mCallback = callback;
    }

    private void initListener() {
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mCallback.onPrepared();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            mCallback.onRender();
                        }
                        return true;
                    }
                });
            }
        });
        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mCallback.onComplete();
            }
        });
    }

    @Override
    public View getView() {
        return this;
    }

}
