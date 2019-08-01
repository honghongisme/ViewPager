package com.example.viewpager.download;

import com.example.viewpager.download.dao.DownloadInfoDao;
import com.example.viewpager.download.entity.Advertise;
import com.example.viewpager.download.entity.DownloadInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.LinkedHashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadTask implements Runnable, OnDownloadListener{

    private OkHttpClient mClient;
    private DownloadInfo mDownloadInfo;
    private Set<WeakReference<OnDownloadListener>> mListeners = new LinkedHashSet<>();
    private DownloadInfoDao mDAO;

    public DownloadTask(OkHttpClient client, DownloadInfoDao dao, DownloadInfo info, OnDownloadListener onDownloadListener) {
        mClient = client;
        mDAO = dao;
        mDownloadInfo = info;
        mListeners.add(new WeakReference<OnDownloadListener>(onDownloadListener));
    }

    public void addListener(OnDownloadListener listener){
        mListeners.add(new WeakReference<OnDownloadListener>(listener));
    }

    @Override
    public void run() {
        final Request request = new Request.Builder()
                .url(mDownloadInfo.getUrl())
                .header("RANGE", "bytes=" + mDownloadInfo.getProgress() + "-")
                .build();
        onStart();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null) {
                    if (response.code() == HttpURLConnection.HTTP_PARTIAL) {
                        // 支持断点续传
                        System.out.println("断点续传 : " + mDownloadInfo);
                        breakPointDownload(response.body());
                    } else {
                        // 不支持断点续传
                        System.out.println("普通下载 : " + mDownloadInfo);
                        normalDownload(response.body());
                    }
                }
            }
        });
    }

    private void normalDownload(ResponseBody body) {
        InputStream inputStream = body.byteStream();
        File file = new File(mDownloadInfo.getPath());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            long total = body.contentLength();
            int len;
            long sum = 0;
            byte[] buf = new byte[2048];
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
                sum += len;
                mDownloadInfo.setProgress(sum);
                mDAO.updateDownloadInfoProgress(mDownloadInfo);
                int progress = (int) (sum * 1.0 / total * 100);
                onProgress(progress);
            }
            // 下载完成
            outputStream.close();
            inputStream.close();
            mDownloadInfo.setState(1);
            mDAO.updateDownloadInfoStatus(mDownloadInfo);
            Advertise advertise = new Advertise();
            advertise.setUrl(mDownloadInfo.getUrl());
            advertise.setPath(mDownloadInfo.getPath());
            onFinished(advertise);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void breakPointDownload(ResponseBody body) {
        long sum = mDownloadInfo.getProgress();
        long total = body.contentLength() + sum;
        InputStream inputStream = body.byteStream();
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(mDownloadInfo.getPath(), "rw");
            file.seek(sum);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                file.write(buffer, 0, len);
                sum = sum + len;
                mDownloadInfo.setProgress(sum);
                mDAO.updateDownloadInfoProgress(mDownloadInfo);
                // 两个long类型的数值相除，结果会自动取整
                int progress = (int) (sum * 1.0 / total * 100);
                onProgress(progress);
                if (progress % 20 == 0) {
                    System.out.println("继续下载中.....进度 =  " + progress + " %");
                }
            }
            // 下载完成
            file.close();
            inputStream.close();
            mDownloadInfo.setState(1);
            mDAO.updateDownloadInfoStatus(mDownloadInfo);
            Advertise advertise = new Advertise();
            advertise.setUrl(mDownloadInfo.getUrl());
            advertise.setPath(mDownloadInfo.getPath());
            onFinished(advertise);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onFailed() {
        for (WeakReference<OnDownloadListener> listener : mListeners) {
            if (listener.get() != null) {
                listener.get().onFailed();
            }
        }
    }

    @Override
    public void onStart() {
        for (WeakReference<OnDownloadListener> listener : mListeners) {
            if (listener.get() != null) {
                listener.get().onStart();
            }
        }
    }

    @Override
    public void onPause() {
        for (WeakReference<OnDownloadListener> listener : mListeners) {
            if (listener.get() != null) {
                listener.get().onPause();
            }
        }
    }

    @Override
    public void onProgress(int progress) {
        for (WeakReference<OnDownloadListener> listener : mListeners) {
            if (listener.get() != null) {
                listener.get().onProgress(progress);
            }
        }
    }

    @Override
    public void onFinished(Advertise advertise) {
        for (WeakReference<OnDownloadListener> listener : mListeners) {
            if (listener.get() != null) {
                listener.get().onFinished(advertise);
            }
        }
    }
}
