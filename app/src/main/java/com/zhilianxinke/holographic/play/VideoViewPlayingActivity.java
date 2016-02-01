package com.zhilianxinke.holographic.play;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.zhilianxinke.holographic.R;
import com.zhilianxinke.holographic.utils.L;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VideoViewPlayingActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    MediaController mediaco;
    @Bind(R.id.video1)
    VideoView video1;
    @Bind(R.id.time)
    TextView time;
    private Uri uri;
    private Uri path;
    private int countSeconds = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 不锁屏
        setContentView(R.layout.controllerplaying);
        ButterKnife.bind(this);
        mediaco = new MediaController(this);
        path = getIntent().getData();
        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beforehand_loading);
        video1.setVideoURI(uri);
        video1.setMediaController(mediaco);
        mediaco.setMediaPlayer(video1);
        // 让VideiView获取焦点
        video1.start();
        video1.setOnCompletionListener(this);
        video1.setOnPreparedListener(this);
        startCountBack();
    }

    @Override
    protected void onDestroy() {
        // Ctrl + D 删除一行
        super.onDestroy();
        video1.stopPlayback();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        L.i("wo de lujing :" + path);
        video1.setVideoURI(path);
        video1.start();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
       /* mediaPlayer.start();
        mediaPlayer.setLooping(true);*/
    }

    @SuppressLint("HandlerLeak")
    private Handler mCountHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (countSeconds > 0) {
                --countSeconds;
                time.setText("倒计时" + countSeconds+ "秒");
                mCountHanlder.sendEmptyMessageDelayed(0, 1000);
            } else {
                time.setVisibility(View.GONE);
            }
        }
    };

    // 开始倒计时
    private void startCountBack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time.setText("倒计时" + countSeconds + "秒");
                mCountHanlder.sendEmptyMessage(0);
            }
        });

    }
}
