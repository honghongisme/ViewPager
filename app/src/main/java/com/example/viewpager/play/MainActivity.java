package com.example.viewpager.play;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.viewpager.R;
import com.example.viewpager.download.entity.Advertise;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    private static final int SWITCH_TO_NEXT = 0;
    private static final int IMAGE_SHOW_TIME = 3000;

    public static final int DATA_TYPE_IMAGE = 1;
    public static final int DATA_TYPE_VIDEO = 2;
    public static final String[] ADVERTISE_URL = {"https://mp4.vjshi.com/2018-04-07/66101cfce535738b4aab42d669815423.mp4",
    "https://mp4.vjshi.com/2018-10-14/c1493d464e6c91401172449c068b530a.mp4"};

    private ViewPager mViewPager;
    private List<PageBean> mData;
    // 需要下载的url <url,path>
    private LinkedHashMap<String, String> mUrls;
    private VideoBannerManager mManager;
    private MainPresenterImpl mPresenter;

    private boolean mDown, mFlag;
    private int mItemPositionBeforeSpringBack;
    private int mVideoProgress;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SWITCH_TO_NEXT:
                    switchNext();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        mViewPager = findViewById(R.id.view_page);
        mPresenter = new MainPresenterImpl(this);
        mPresenter.loadData(mUrls.keySet());

        mManager = VideoBannerManager.getInstance(this);
        SimplePagerAdapter adapter = new SimplePagerAdapter(mData, this);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new BannerOnPageChangeListener());
    }

    private void initData() {
        PageBean pageBean1 = new PageBean("image1", DATA_TYPE_IMAGE);
        PageBean pageBean2 = new PageBean(ADVERTISE_URL[0], DATA_TYPE_VIDEO);
        PageBean pageBean3 = new PageBean(ADVERTISE_URL[1], DATA_TYPE_VIDEO);
        PageBean pageBean4 = new PageBean("image2", DATA_TYPE_IMAGE);

        mData = new ArrayList<>();
        mData.add(pageBean1);
        mData.add(pageBean2);
        mData.add(pageBean3);
        mData.add(pageBean4);

        mUrls = new LinkedHashMap<>();
        mUrls.put(ADVERTISE_URL[0], null);
        mUrls.put(ADVERTISE_URL[1], null);
    }

    /**
     * 切换下一屏
     */
    private void switchNext() {
        System.out.println("switch");
        int currentItem = mViewPager.getCurrentItem();
        int toItem = currentItem + 1;
        if (toItem < mData.size()) {
            mViewPager.setCurrentItem(toItem, true);
        }
    }

    @Override
    public void addPlay(Advertise advertise) {
        mUrls.put(advertise.getUrl(), advertise.getPath());
        PageBean pageBean = mData.get(mViewPager.getCurrentItem());
        // video页 && 新下载好的广告是当前页的
        if (pageBean.getType() == DATA_TYPE_VIDEO && advertise.getUrl().equals(pageBean.getPath())) {
            playCurrentVideo();
        }
    }

    class BannerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
     /*       PageBean pageBean = mData.get(mViewPager.getCurrentItem());
            if (mDown && (pageBean.getType() == DATA_TYPE_VIDEO)) {
                if (positionOffsetPixels == 0) {
                    System.out.println("mItemPositionBeforeSpringBack = " + mItemPositionBeforeSpringBack);
                    if (mFlag && mItemPositionBeforeSpringBack == mViewPager.getCurrentItem()) { // 松开回弹
                        System.out.println("回到本页 继续播放 mVideoProgress = " + mVideoProgress);
                        //回到本页 继续播放
                        mManager.seekTo(mVideoProgress);
                        mManager.start();
                    }
                    mDown = false;
                    mFlag = false;
                } else {
                    // 拖拽按下  一次拖拽只执行一次
                    if (!mFlag) {
                        mManager.pause();
                        mVideoProgress = mManager.getCuurentPosition();
                        System.out.println("拖拽按下 mVideoProgress = " + mVideoProgress + "    mItemPositionBeforeSpringBack = " + mViewPager.getCurrentItem());
                        mHandler.removeCallbacksAndMessages(null);
                        mItemPositionBeforeSpringBack = mViewPager.getCurrentItem();
                        mFlag = true;
                    }
                }
            }*/
        }

        @Override
        public void onPageSelected(final int position) {
            System.out.println("onPageSelected = " + position);
            doShow();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * show当前页
     */
    public void doShow() {
        mHandler.removeCallbacksAndMessages(null);
        PageBean pageBean = mData.get(mViewPager.getCurrentItem());
        if (pageBean.getType() == DATA_TYPE_IMAGE) {
            if (mManager.isExistView()) {
                mManager.stop();
            }
            mHandler.sendEmptyMessageDelayed(SWITCH_TO_NEXT, IMAGE_SHOW_TIME);
        } else if (pageBean.getType() == DATA_TYPE_VIDEO) {
            playCurrentVideo();
        }
    }

    /**
     * 播放video
     */
    public void playCurrentVideo() {
        View view = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        PageBean pageBean = mData.get(mViewPager.getCurrentItem());
   /*     view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("down");
                    mDown = true;
                }
                return true;
            }
        });*/
        FrameLayout videoContainer = view.findViewById(R.id.video_container);
        final ImageView imageView = view.findViewById(R.id.image);
        if (imageView.getVisibility() == View.INVISIBLE) {
            imageView.setVisibility(View.VISIBLE);
        }
        IVideoAbleView videoView = mManager.getView();
        String path = mUrls.get(pageBean.getPath());
        if (path != null) { // 下载完成
            mManager.setNetworkUri(path);
            mManager.setStatusCallback(new VideoStatusCallback() {
                @Override
                public void onPrepared() {
                    mManager.start();
                    System.out.println("onPrepared");
                }

                @Override
                public void onRender() {
                    imageView.setVisibility(View.INVISIBLE);
                    System.out.println("onRender");
                }

                @Override
                public void onPlay() {
                    System.out.println("onPlay");
                }

                @Override
                public void onProgress(int progress) {
                    System.out.println("onProgress");
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                    imageView.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(SWITCH_TO_NEXT);
                }

                @Override
                public void onRelease() {
                    System.out.println("onRelease");
                }
            });
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            videoContainer.addView((View) videoView, layoutParams);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.post(new Runnable(){
            @Override
            public void run() {
                doShow();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManager.release();
        mPresenter.onDestroy();
    }
}
