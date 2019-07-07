package com.example.viewpager.download;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.viewpager.download.dao.DownloadInfoDao;
import com.example.viewpager.download.entity.Advertise;
import com.example.viewpager.download.entity.DownloadInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.viewpager.download.util.FileUtil.deleteFiles;
import static com.example.viewpager.download.util.FileUtil.getAbsolutePath;

public class DownloadManager {

 //   private HashMap<String, OnDownloadListener> mDownloadList;
    private HashMap<String, DownloadTask> mDownloadTasks;
    private DownloadInfoDao mDAO;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private String mDownloadDirPath;

    private DownloadManager() {
 //       mDownloadList = new HashMap<>();
        mDownloadTasks = new HashMap<>();
        mDAO = new DownloadInfoDao(mContext, null);
    }

    public static DownloadManager getInstance(Context context) {
        mContext = context.getApplicationContext();
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final DownloadManager INSTANCE = new DownloadManager();
    }

    public void addDownload(DownloadInfo downloadInfo, OnDownloadListener listener) {
        DownloadTask task = getExistTask(downloadInfo.getUrl());
        if (task != null) { // 已有下载任务
            task.addListener(listener);
        } else { // 新建任务
 //           mDownloadList.put(downloadInfo.getUrl(), listener);
            download(downloadInfo, listener);
        }
    }

    private void download(final DownloadInfo info, final OnDownloadListener listener) {
        String downloadPath = getAbsolutePath(mDownloadDirPath, info.getUrl());
        DownloadTask task = new DownloadTask(info, downloadPath, listener){
            @Override
            public void onFinished(Advertise advertise) {
                super.onFinished(advertise);
                System.out.println("下载完成:   " + info);
                updateDownloadInfo(info);
    //            mDownloadList.remove(info.getUrl());
                mDownloadTasks.remove(info.getUrl());
            }
        };
        mDownloadTasks.put(info.getUrl(), task);
        DownLoadExecutor.execute(task);
    }

    private DownloadTask getExistTask(String url) {
        for (Map.Entry<String, DownloadTask> task : mDownloadTasks.entrySet()) {
            if (url.equals(task.getKey())) {
                return task.getValue();
            }
        }
        return null;
    }

    private void updateDownloadInfo(DownloadInfo info) {
        mDAO.updateDownloadInfoByUrl(info);
    }

    public void deleteDownload() {
        List<String> list = mDAO.queryAllDownloadPath();
        deleteFiles(list);
        mDAO.deleteAll();
    }

    public void setDownloadDirPath(String downloadDirPath) {
        this.mDownloadDirPath = downloadDirPath;
    }

}
