package com.example.viewpager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final int SWITCH_TO_NEXT = 0;
    private static final int IMAGE_SHOW_TIME = 3000;

    private ViewPager mViewPager;
    private List<PageBean> mData;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SWITCH_TO_NEXT:
                    int toItem = switchNext();
                    System.out.println("当前正在播放" + toItem);
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.view_page);
        mData = new ArrayList<>();

        initData();
        SimplePagerAdapter simplePagerAdapter = new SimplePagerAdapter(mData, this);
        mViewPager.setAdapter(simplePagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("onPageSelected = " + position);
                int index;
                if (mViewPager.getChildCount() == 2 && position == 0) {
                    index = 0;
                } else {
                    index = 1;
                }
                final View view = mViewPager.getChildAt(index);
                if (view instanceof MyVideoView) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                if (((MyVideoView)view).isPrepared()) {
                                    ((MyVideoView)view).start();
                                    break;
                                } else {
                                    try {
                                        sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            while (true) {
                                if (((MyVideoView)view).isCompleted()) {
                                    System.out.println("播放结束");
                                    mHandler.sendEmptyMessage(SWITCH_TO_NEXT);
                                    break;
                                } else {
                                    try {
                                        sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();
                } else {
                    mHandler.sendEmptyMessageDelayed(SWITCH_TO_NEXT, IMAGE_SHOW_TIME);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initData() {
        PageBean pageBean1 = new PageBean("image1", 0);
        PageBean pageBean2 = new PageBean("video1", 1);
        PageBean pageBean3 = new PageBean("video2", 1);
        PageBean pageBean4 = new PageBean("image2", 0);
        mData.add(pageBean1);
        mData.add(pageBean2);
        mData.add(pageBean3);
        mData.add(pageBean4);
    }

    /**
     * 切换下一屏
     * @return 下一屏的position
     */
    private int switchNext() {
        int currentItem = mViewPager.getCurrentItem();
        int toItem = currentItem + 1;
        if (toItem < mData.size()) {
            mViewPager.setCurrentItem(toItem, true);
        }
        return toItem;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(SWITCH_TO_NEXT, IMAGE_SHOW_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }
}
