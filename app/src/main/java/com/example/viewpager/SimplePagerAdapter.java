package com.example.viewpager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SimplePagerAdapter extends PagerAdapter {

    private List<PageBean> mData;
    private Context mContext;
    private VideoViewLoader mLoader;
    private static final int DATA_TYPE_IMAGE = 0;
    public static final int DATA_TYPE_VIDEO = 1;

    public SimplePagerAdapter(List<PageBean> data, Context context) {
        this.mData = data;
        this.mContext = context;
        mLoader = VideoViewLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PageBean pageBean = mData.get(position);
        if (pageBean.getType() == DATA_TYPE_IMAGE ) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(mContext.getResources().getIdentifier(pageBean.getResName(), "drawable", "com.example.viewpager"));
            container.addView(imageView);
            return imageView;
        } else if (pageBean.getType() == DATA_TYPE_VIDEO) {
            MyVideoView cache = mLoader.getView();
            final MyVideoView videoView ;
            if (cache == null) {
                videoView = new MyVideoView(mContext);
            } else {
                videoView = cache;
            }
            videoView.setVideoUri(pageBean.getResName());
            videoView.setOnVideoRendringListener(new MyVideoView.OnVideoRendringListener() {
                @Override
                public void onRendingFinish() {
                    videoView.setBackground(null);
                }
            });
            container.addView(videoView);
            return videoView;
        }
        return null;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (mData.get(position).getType() == DATA_TYPE_VIDEO) {
            mLoader.addView((MyVideoView) object);
        }
        container.removeView((View) object);
    }
}
