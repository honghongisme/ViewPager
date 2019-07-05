package com.example.viewpager.download;


import com.example.viewpager.download.entity.Advertise;
import com.example.viewpager.download.entity.DownloadInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask implements Runnable, OnDownloadListener{

    private OkHttpClient mClient;
    private DownloadInfo mDownloadInfo;
    private Set<WeakReference<OnDownloadListener>> mListeners=new LinkedHashSet<>();
    private String mDownloadPath;


    public DownloadTask(DownloadInfo info, String downloadPath, OnDownloadListener onDownloadListener) {
        mClient = new OkHttpClient.Builder().build();
        mDownloadInfo = info;
        mListeners.add(onDownloadListener);
        mDownloadPath = downloadPath;
    }

    public void addListener(OnDownloadListener listener){
        mListeners.add(listener);
    }

    @Override
    public void run() {
        Request request = new Request.Builder().url(mDownloadInfo.getUrl()).build();
        onStart();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    InputStream inputStream = response.body().byteStream();
                    mDownloadInfo.setPath(mDownloadPath);
                    File file = new File(mDownloadPath);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    long total = response.body().contentLength();
                    int len;
                    long sum = 0;
                    byte[] buf = new byte[2048];
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum/total);
                      onProgress(progress);
                    }
                    // 下载完成
                    outputStream.close();
                    inputStream.close();
                    mDownloadInfo.setProgress(sum);
                    mDownloadInfo.setState(1);
                    Advertise advertise = new Advertise();
                    advertise.setUrl(mDownloadInfo.getUrl());
                    advertise.setPath(mDownloadPath);
                    onFinished(advertise);
                }
            }
        });
    }

    @Override
    public void onFailed() {
        for (OnDownloadListener listener:mListeners) {
            listener.onFailed();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onFinished(Advertise advertise) {

    }
}
