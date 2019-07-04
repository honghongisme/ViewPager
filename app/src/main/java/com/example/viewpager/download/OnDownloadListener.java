package com.example.viewpager.download;

import com.example.viewpager.download.enties.Advertise;

public interface OnDownloadListener {

    void onFailed();
    void onStart();
    void onPause();
    void onProgress(int progress);

    /**
     * 下载完成，回调下载完成的广告
     * @param advertise
     */
    void onFinished(Advertise advertise);
}
