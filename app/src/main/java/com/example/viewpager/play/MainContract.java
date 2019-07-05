package com.example.viewpager.play;

import com.example.viewpager.download.entity.Advertise;

import java.util.List;

public interface MainContract {

    interface Presenter {
        void loadData(List<String> urlList);
    }

    interface View {
        void addPlay(Advertise advertise);
    }
}
