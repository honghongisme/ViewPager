package com.example.viewpager.play;

import com.example.viewpager.download.entity.Advertise;

import java.util.List;
import java.util.Set;

public interface MainContract {

    interface Presenter {
        void loadData(Set<String> urlList);
        void onDestroy();
    }

    interface View {
        void addPlay(Advertise advertise);
    }
}
