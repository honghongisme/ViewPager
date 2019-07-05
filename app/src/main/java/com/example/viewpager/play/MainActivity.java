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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.viewpager.R;
import com.example.viewpager.download.entity.Advertise;

import java.util.ArrayList;
import java.util.List;

import static com.example.viewpager.play.Constants.DATA_TYPE_VIDEO;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    private static final int SWITCH_TO_NEXT = 0;
    private static final int IMAGE_SHOW_TIME = 3000;

    private ViewPager mViewPager;
    private List<PageBean> mData;
    private List<String> mUrls;
    private VideoBannerManager mManager;
    private BannerOnPageChangeListener mPageChangeListener;
    private MainPresenterImpl mPresenter;

    private boolean mDown, mFlag;
    private int mItemPositionBeforeSpringBack;
    private int mVideoProgress;

    private boolean mIsWaitingDownload;

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
        mPresenter = new MainPresenterImpl(this);
        mPresenter.loadData(mUrls);

        mViewPager = findViewById(R.id.view_page);
        mManager = VideoBannerManager.getInstance(this);

        SimplePagerAdapter adapter = new SimplePagerAdapter(mData, this);
        mPageChangeListener = new BannerOnPageChangeListener();
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    private void initData() {
        PageBean pageBean1 = new PageBean("image1", 0);
        PageBean pageBean2 = new PageBean(null, 1);
        PageBean pageBean3 = new PageBean(null, 1);
        /*PageBean pageBean2 = new PageBean("video2", 1);
        PageBean pageBean3 = new PageBean("video1", 1);*/
        PageBean pageBean4 = new PageBean("image2", 0);

        mData = new ArrayList<>();
        mData.add(pageBean1);
        mData.add(pageBean2);
        mData.add(pageBean3);
        mData.add(pageBean4);

        mUrls = new ArrayList<>();
        mUrls.add("https://mp4.vjshi.com/2018-04-07/66101cfce535738b4aab42d669815423.mp4");
        mUrls.add("https://mp4.vjshi.com/2018-10-14/c1493d464e6c91401172449c068b530a.mp4");

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
        for (PageBean pageBean : mData) {
            if (pageBean.getType() == DATA_TYPE_VIDEO && pageBean.getPath() == null) {
                pageBean.setPath(advertise.getPath());
                break;
            }
        }
        if (mIsWaitingDownload) {
            mPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            mIsWaitingDownload = false;
        }
    }

    class BannerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  //          System.out.println("onPageScrolled = (" + positionOffset + ", " + positionOffsetPixels + ")");
            PageBean pageBean = mData.get(mViewPager.getCurrentItem());
   //         System.out.println("positionOffset = " + positionOffset + "    mDown = " + mDown + "     pageBean.getType() == DATA_TYPE_VIDEO ->" + (pageBean.getType() == DATA_TYPE_VIDEO));
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
            }
        }

        @Override
        public void onPageSelected(final int position) {
            mHandler.removeCallbacksAndMessages(null);
            System.out.println("onPageSelected = " + position);
            View view = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
            PageBean pageBean = mData.get(position);
            if (pageBean.getType() == Constants.DATA_TYPE_IMAGE) {
                if (mManager.isExistView()) {
                    mManager.stop();
                }
        /*        ImageView imageView = view.findViewById(R.id.image);
                imageView.setImageResource(getApplicationContext().getResources().getIdentifier(pageBean.getPath(), "drawable", "com.example.viewpager"));*/
                mHandler.sendEmptyMessageDelayed(SWITCH_TO_NEXT, IMAGE_SHOW_TIME);
            } else if (pageBean.getType() == DATA_TYPE_VIDEO) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            System.out.println("down");
                            mDown = true;
                        }
                        return true;
                    }
                });
                FrameLayout videoContainer = view.findViewById(R.id.video_container);
                final ImageView imageView = view.findViewById(R.id.image);
                if (imageView.getVisibility() == View.INVISIBLE) {
                    imageView.setVisibility(View.VISIBLE);
                }
                IVideoAbleView videoView = mManager.getView();
                if (pageBean.getPath() != null) { // 下载完成
                    mManager.setNetworkUri(pageBean.getPath());
                    setVideoView(imageView, videoContainer, (View) videoView);
                } else { // 正在下载
                    mIsWaitingDownload = true;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void setVideoView(final ImageView placeHolder, ViewGroup videoContainer, View videoView) {
        mManager.setStatusCallback(new VideoStatusCallback() {
            @Override
            public void onPrepared() {
                mManager.start();
                System.out.println("onPrepared");
            }

            @Override
            public void onRender() {
                placeHolder.setVisibility(View.INVISIBLE);
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
                placeHolder.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessage(SWITCH_TO_NEXT);
            }

            @Override
            public void onRelease() {
                System.out.println("onRelease");
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoContainer.addView(videoView, layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.post(new Runnable(){
            @Override
            public void run() {
                mPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
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
    }
}
