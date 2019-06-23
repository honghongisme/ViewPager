package com.example.viewpager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SWITCH_TO_VIDEO = 0;
    private static final int SWITCH_TO_IMAGE = 1;
    private static final int IMAGE_SHOW_TIME = 3000;

    private ViewPager mViewPager;
    private List<View> mViews;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SWITCH_TO_VIDEO:
                    int toItem = switchNext();
                    System.out.println("当前正在播放" + toItem);
                    break;
                case SWITCH_TO_IMAGE:
                    toItem = switchNext();
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
        mViews = new ArrayList<>();

        initData();
        SimplePagerAdapter simplePagerAdapter = new SimplePagerAdapter(mViews);
        mViewPager.setAdapter(simplePagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("onPageSelected = " + position);
                final View view = mViews.get(position);
                if (view.getTag() != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                if (((MyVideoView)view).isPrepared()) {
                                    ((MyVideoView)view).start();
                                    break;
                                } else {
                                    try {
                                        wait(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initData() {
        ImageView imageViewOne = new ImageView(this);
        imageViewOne.setImageResource(R.drawable.image1);
        mViews.add(imageViewOne);

        final MyVideoView videoViewOne = new MyVideoView(this);
        videoViewOne.setVideoUri("video1");
        videoViewOne.setOnVideoRendringListener(new MyVideoView.OnVideoRendringListener() {
            @Override
            public void onRendingFinish() {
                videoViewOne.setBackground(null);
            }
        });
        videoViewOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("播放结束");
                mHandler.sendEmptyMessage(SWITCH_TO_VIDEO);
            }
        });
        mViews.add(videoViewOne);

        final MyVideoView videoViewTwo = new MyVideoView(this);
        videoViewTwo.setVideoUri("video2");
        videoViewTwo.setOnVideoRendringListener(new MyVideoView.OnVideoRendringListener() {
            @Override
            public void onRendingFinish() {
                videoViewTwo.setBackground(null);
            }
        });
        videoViewTwo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("播放结束");
                mHandler.sendEmptyMessage(SWITCH_TO_IMAGE);
            }
        });
        mViews.add(videoViewTwo);

        ImageView imageViewTwo = new ImageView(this);
        imageViewTwo.setImageResource(R.drawable.image2);
        mViews.add(imageViewTwo);
    }

    /**
     * 切换下一屏
     * @return 下一屏的position
     */
    private int switchNext() {
        int totalCount = mViews.size();
        int currentItem = mViewPager.getCurrentItem();
        int toItem = (currentItem + 1) % totalCount;
        mViewPager.setCurrentItem(toItem, true);
        return toItem;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(SWITCH_TO_VIDEO, IMAGE_SHOW_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }
}
