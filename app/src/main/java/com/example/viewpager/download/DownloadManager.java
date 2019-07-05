package com.example.viewpager.download;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.viewpager.download.dao.DownloadInfoDao;
import com.example.viewpager.download.entity.Advertise;
import com.example.viewpager.download.entity.DownloadInfo;

import java.util.HashMap;
import java.util.List;

import static com.example.viewpager.download.util.FileUtil.deleteFiles;
import static com.example.viewpager.download.util.FileUtil.getExternalStoragePath;
import static com.example.viewpager.download.util.FileUtil.getFileName;

public class DownloadManager {

    private HashMap<DownloadInfo, OnDownloadListener> mDownloadList;
    private DownloadInfoDao mDAO;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private DownloadManager() {
        mDownloadList = new HashMap<>();
        mDAO = new DownloadInfoDao(mContext);
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
        if (!mDownloadList.containsKey(downloadInfo)) {
            mDownloadList.put(downloadInfo, listener);
            download(downloadInfo, listener);
        }
    }

    private void download(final DownloadInfo info, final OnDownloadListener listener) {
        DownloadTask task = new DownloadTask(info, getExternalStorageDownloadPath(info.getUrl()),listener){
            @Override
            public void onFinished(Advertise advertise) {
                super.onFinished(advertise);
                System.out.println("下载完成:   " + info);
                updateDownloadInfo(info);
                mDownloadList.remove(info);
            }
        };
        DownLoadExecutor.execute(task);
    }

    private String getExternalStorageDownloadPath(String url) {
        return getExternalStoragePath(mContext, getFileName(url));
    }

    private void updateDownloadInfo(DownloadInfo info) {
        mDAO.updateDownloadInfoByUrl(info);
    }

    public void deleteDownload() {
        List<String> list = mDAO.queryAllDownloadPath();
        deleteFiles(list);
        mDAO.deleteAll();
    }

}
