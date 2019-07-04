package com.example.viewpager.download;

import com.example.viewpager.download.enties.Advertise;

import java.util.List;

public interface MainContract {

    interface Presenter {
        void getData(List<String> urlList);
    }

    interface View {
        void addPlay(Advertise advertise);
    }
}
