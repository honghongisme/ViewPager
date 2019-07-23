package com.example.viewpager.play;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import java.io.IOException;

public class BannerTextureView extends TextureView implements IVideoAbleView, TextureView.SurfaceTextureListener {

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private VideoStatusCallback mCallback;
    private Surface mSurface;

    public BannerTextureView(Context context) {
        super(context);
        this.mContext = context;
        mMediaPlayer = new MediaPlayer();
        this.setSurfaceTextureListener(this);
        initListener();
    }

    private void initListener() {
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mCallback != null) {
                    mCallback.onComplete();
                }
            }
        });
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void setVideoURI(Uri uri) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int milliseconds) {

    }

    @Override
    public void addStatusCallback(VideoStatusCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        mMediaPlayer.setSurface(mSurface);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
