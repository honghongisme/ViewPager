package com.example.viewpager.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.viewpager.R;

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
        if (pageBean.getType() == Constants.DATA_TYPE_IMAGE ) {
            view = LayoutInflater.from(mContext).inflate(R.layout.image_page, container, false);
            ImageView imageView = view.findViewById(R.id.image);
            imageView.setImageResource(mContext.getResources().getIdentifier(pageBean.getPath(), "drawable", "com.example.viewpager"));
        } else if (pageBean.getType() == Constants.DATA_TYPE_VIDEO) {
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
