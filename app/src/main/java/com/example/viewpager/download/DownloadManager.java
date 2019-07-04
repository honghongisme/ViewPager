package com.example.viewpager.download;

import android.content.Context;

import com.example.viewpager.download.dao.MainDAO;
import com.example.viewpager.download.enties.Advertise;
import com.example.viewpager.download.enties.DownloadInfo;

import java.util.HashMap;

public class DownloadManager {

    private HashMap<DownloadInfo, OnDownloadListener> mDownloadList;
 //   private HashMap<DownloadInfo, DownloadTask> mDownloadTasks;
    private MainDAO mDAO;

    private DownloadManager() {
        mDownloadList = new HashMap<>();
  //      mDownloadTasks = new HashMap<>();
        // 怎么持有context？？
        mDAO = new MainDAO();
    }

    public static DownloadManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final DownloadManager INSTANCE = new DownloadManager();
    }

    public void addDownload(DownloadInfo downloadInfo, OnDownloadListener listener) {
        if (!mDownloadList.containsKey(downloadInfo)) {
            mDownloadList.put(downloadInfo, listener);
        }
    }

    public void download(final DownloadInfo info, final OnDownloadListener listener) {
        DownloadTask task = new DownloadTask(info, new OnDownloadListener() {
            @Override
            public void onFailed() {
                listener.onFailed();
            }

            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onPause() {
                listener.onPause();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinished(Advertise advertise) {
                listener.onFinished(advertise);
                updateDownloadInfo(info);
                mDownloadList.remove(info);
            }
        });
        DownLoadExecutor.execute(task);
    }

    public void updateDownloadInfo(DownloadInfo info) {
        mDAO.updateDownloadInfoByUrl(info);
    }

    public void delete() {

    }

}
