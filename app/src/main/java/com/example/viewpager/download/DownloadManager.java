package com.example.viewpager.download;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.viewpager.download.dao.DownloadInfoDao;
import com.example.viewpager.download.entity.Advertise;
import com.example.viewpager.download.entity.DownloadInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.viewpager.download.util.FileUtil.deleteFiles;
import static com.example.viewpager.download.util.FileUtil.getAbsolutePath;

public class DownloadManager {

    private Map<String, DownloadTask> mDownloadTasks;
    private DownloadInfoDao mDAO;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private String mDownloadDirPath;

    private DownloadManager() {
        mDownloadTasks = Collections.synchronizedMap( new HashMap<String, DownloadTask>());
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
        DownloadTask task = mDownloadTasks.get(downloadInfo.getUrl());
        if (task != null) { // 已有下载任务
            task.addListener(listener);
        } else { // 新建任务
            download(downloadInfo, listener);
        }
    }

    private void download(final DownloadInfo info, final OnDownloadListener listener) {
        if (info.getPath() == null) { // 新的下载项
            String downloadPath = getAbsolutePath(mDownloadDirPath, info.getUrl());
            info.setPath(downloadPath);
            mDAO.addDownloadInfo(info);
        }
        DownloadTask task = new DownloadTask(info, listener){
            @Override
            public void onFinished(Advertise advertise) {
                super.onFinished(advertise);
                System.out.println("下载完成:   " + info);
                mDAO.updateDownloadInfoStatus(info);
                mDownloadTasks.remove(info.getUrl());
            }
        };
        mDownloadTasks.put(info.getUrl(), task);
        DownLoadExecutor.execute(task);
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
