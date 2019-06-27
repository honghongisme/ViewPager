package com.example.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SimplePagerAdapter extends PagerAdapter {

    private List<PageBean> mData;
    private Context mContext;

    public SimplePagerAdapter(List<PageBean> data, Context context) {
        this.mData = data;
        this.mContext = context;
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
        View view = null;
        if (pageBean.getType() == Constans.DATA_TYPE_IMAGE ) {
            view = LayoutInflater.from(mContext).inflate(R.layout.image_page, container, false);
        } else if (pageBean.getType() == Constans.DATA_TYPE_VIDEO) {
            view = LayoutInflater.from(mContext).inflate(R.layout.video_banner, container, false);
        }
        if (view != null) {
            view.setTag(position);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
