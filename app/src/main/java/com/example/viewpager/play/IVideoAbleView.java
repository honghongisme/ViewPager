package com.example.viewpager.play;

import android.net.Uri;
import android.view.View;

public interface IVideoAbleView {

    View getView();
    void start();
    void pause();
    void resume();
    void stop();
    void setVideoURI(Uri uri);
    int getCurrentPosition();
    void seekTo(int milliseconds);
    void addStatusCallback(VideoStatusCallback callback);

}
