package com.example.viewpager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SWITCH_TO_VIDEO = 0;
    private static final int SWITCH_TO_IMAGE = 1;
    private static final int IMAGE_SHOW_TIME = 5000;
    private static final int VIDEO_SHOW_TIME = 12000;

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
                    if (toItem == 1 || toItem == 3) { // 正在播放 视频
                        sendEmptyMessageDelayed(SWITCH_TO_IMAGE, VIDEO_SHOW_TIME);
                    }
                    break;
                case SWITCH_TO_IMAGE:
                    toItem = switchNext();
                    System.out.println("当前正在播放" + toItem);
                    if (toItem == 0 || toItem == 2) { // 正在播放 图片
                        sendEmptyMessageDelayed(SWITCH_TO_VIDEO, IMAGE_SHOW_TIME);
                    }
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.addLogAdapter(new AndroidLogAdapter());

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

        ImageView imageViewTwo = new ImageView(this);
        imageViewTwo.setImageResource(R.drawable.image2);
        mViews.add(imageViewTwo);

        VideoView videoViewOne = new VideoView(this);
        videoViewOne.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video1));
        addVideoListener(videoViewOne);
        mViews.add(videoViewOne);

        VideoView videoViewTwo = new VideoView(this);
        videoViewTwo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video2));
        addVideoListener(videoViewTwo);
        mViews.add(videoViewTwo);
    }

    private void addVideoListener(final VideoView videoView) {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                System.out.println("视频加载完成！");
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                System.out.println("视频加载完成！");
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                System.out.println("视频播放出错！");
                return true;
            }
        });
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
   //     mHandler.sendEmptyMessageDelayed(SWITCH_TO_VIDEO, IMAGE_SHOW_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mHandler.removeCallbacksAndMessages(null);
    }
}
