package com.example.viewpager.download;

import android.content.Context;

import com.example.viewpager.download.dao.MainDAO;
import com.example.viewpager.download.enties.Advertise;
import com.example.viewpager.download.enties.DownloadInfo;

import java.util.List;

public class MainPresenterImpl implements MainContract.Presenter {

    private MainContract.View mView;
    private MainDAO mDAO;
    private DownloadManager mManager;


    public MainPresenterImpl(MainContract.View mView) {
        this.mView = mView;
        mDAO = new MainDAO((Context) mView);
        mManager = DownloadManager.getInstance();
    }

    @Override
    public void getData(List<String> urlList) {
        List<DownloadInfo> list = mDAO.queryDownloadInfo(urlList);
        for (DownloadInfo info : list) {
            if (info.getState() == 0) { // 未完成的 继续下载
                mManager.addDownload(info, new OnDownloadListener() {
                    @Override
                    public void onFailed() {

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
                        mView.addPlay(advertise);
                    }
                });
            } else { // 完成的 播放
                Advertise advertise = new Advertise();
                advertise.setPath(info.getPath());
                advertise.setUrl(info.getUrl());
                mView.addPlay(advertise);
            }
        }
    }
}
