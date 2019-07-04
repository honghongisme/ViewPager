package com.example.viewpager.download;


import com.example.viewpager.download.enties.Advertise;
import com.example.viewpager.download.enties.DownloadInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.viewpager.download.util.FileUtil.getExternalStoragePath;


public class DownloadTask implements Runnable {

    private OkHttpClient mClient;
    private DownloadInfo mDownloadInfo;
    private OnDownloadListener mListener;


    public DownloadTask(DownloadInfo info, OnDownloadListener onDownloadListener) {
        mClient = new OkHttpClient.Builder().build();
        mDownloadInfo = info;
        mListener = onDownloadListener;
    }

    @Override
    public void run() {
        Request request = new Request.Builder().url(mDownloadInfo.getUrl()).build();
        mListener.onStart();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mListener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    InputStream inputStream = response.body().byteStream();
                    String path = getExternalStoragePath(mDownloadInfo.getUrl());
                    mDownloadInfo.setPath(path);
                    File file = new File(path);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    long total = response.body().contentLength();
                    int len;
                    long sum = 0;
                    byte[] buf = new byte[2048];
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum/total);
                        mListener.onProgress(progress);
                    }
                    // 下载完成
                    outputStream.close();
                    inputStream.close();
                    mDownloadInfo.setState(1);
                    Advertise advertise = new Advertise();
                    advertise.setUrl(mDownloadInfo.getUrl());
                    advertise.setPath(path);
                    mListener.onFinished(advertise);
                }
            }
        });
    }
}
