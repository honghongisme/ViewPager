package com.example.viewpager.play;

import android.content.Context;

import com.example.viewpager.download.DownloadManager;
import com.example.viewpager.download.OnDownloadListener;
import com.example.viewpager.download.SimpleOnDownloadListener;
import com.example.viewpager.download.dao.DownloadInfoDao;
import com.example.viewpager.download.entity.Advertise;
import com.example.viewpager.download.entity.DownloadInfo;

import java.util.List;

public class MainPresenterImpl implements MainContract.Presenter {

    private MainContract.View mView;
    private DownloadInfoDao mDAO;
    private DownloadManager mManager;


    public MainPresenterImpl(MainContract.View mView) {
        this.mView = mView;
        mDAO = new DownloadInfoDao((Context) mView);
        mManager = DownloadManager.getInstance((Context) mView);
    }

    @Override
    public void loadData(List<String> urlList) {
        List<DownloadInfo> list = mDAO.queryDownloadInfo(urlList);
        System.out.println("数据库 ：" + list);
        for (DownloadInfo info : list) {
            if (info.getState() == 0) { // 未完成的 加入下载队列 继续下载
                mManager.addDownload(info, new SimpleOnDownloadListener() {
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
