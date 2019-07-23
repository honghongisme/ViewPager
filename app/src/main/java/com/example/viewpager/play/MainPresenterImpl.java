package com.example.viewpager.play;

import android.content.Context;

import com.example.viewpager.download.DownloadManager;
import com.example.viewpager.download.SimpleOnDownloadListener;
import com.example.viewpager.download.dao.DownloadInfoDao;
import com.example.viewpager.download.entity.Advertise;
import com.example.viewpager.download.entity.DownloadInfo;
import com.example.viewpager.download.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainPresenterImpl implements MainContract.Presenter {

    public static final int DOWNLOAD_STATE_NOT_FINISHED = 0;
    public static final int DOWNLOAD_STATE_FINISHED = 1;

    private MainContract.View mView;
    private DownloadInfoDao mDAO;
    private DownloadManager mManager;
    // 保存listener  不然弱引用get会为空
    private HashMap<String, SimpleOnDownloadListener> mListenerMap;


    public MainPresenterImpl(MainContract.View mView) {
        this.mView = mView;
        mDAO = new DownloadInfoDao((Context) mView, null);
        mManager = DownloadManager.getInstance((Context) mView);
        mManager.setDownloadDirPath(((Context) mView).getExternalFilesDir(null).getAbsolutePath());
        mListenerMap = new HashMap<>();
    }

    @Override
    public void loadData(Set<String> urlList) {
        List<DownloadInfo> list = mDAO.queryDownloadInfo(urlList);
        for (DownloadInfo info : list) {
            if (info.getState() == DOWNLOAD_STATE_NOT_FINISHED) { // 未完成的 加入下载队列 继续下载
                if (info.getPath() != null) { // 下载一半的
                    // 查找进度
                    info.setProgress(FileUtil.getFileLength(info.getPath()));
                }
                System.out.println("下载列表 " + info);
                SimpleOnDownloadListener listener = new SimpleOnDownloadListener() {
                    @Override
                    public void onFinished(Advertise advertise) {
                        mView.addPlay(advertise);
                    }
                };
                mListenerMap.put(info.getUrl(), listener);
                mManager.addDownload(info, listener);
            } else if (info.getState() == DOWNLOAD_STATE_FINISHED){ // 完成的 播放
                Advertise advertise = new Advertise();
                advertise.setPath(info.getPath());
                advertise.setUrl(info.getUrl());
                mView.addPlay(advertise);
            }
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
    }


}
