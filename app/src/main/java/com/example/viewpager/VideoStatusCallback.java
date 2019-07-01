package com.example.viewpager;

public interface VideoStatusCallback {
    //准备成功回调
    //播放回调
    //进度回调
    //结束回调
    //。。。。release回调

    void onPrepared();
    void onRender();
    void onPlay();
    void onProgress(int progress);
    void onComplete();
    void onRelease();
}
