package com.example.admin.videoplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer  mMediaPlayer;
    ImageButton mStopImageButton;
    ImageButton mStartImageButton;
    ImageButton mPauseImageButton;

    boolean bIsPlaying=true;
    boolean isPause=true;

    VideoView videoView;

    /**
     * 获取默认的文件路径
     *
     * @return
     */
    public static String getDefaultFilePath() {
        String filepath = "";
        File file = new File(Environment.getExternalStorageDirectory()+ File.separator+"qqqq.3gp");
        if (file.exists()) {
            filepath = file.getAbsolutePath();
        } else {
            filepath = "不适用";
        }
        System.out.println("--------------------------------------");
        System.out.println(filepath);
        System.out.println("--------------------------------------");
        return filepath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSdCardPath();

        videoView= (VideoView) findViewById(R.id.videoView);
        //String path=getDefaultFilePath();
        //videoView.setVideoURI(Uri.parse( "sdcard/qqqq.3gp"));
        //videoView.setVideoPath(Environment.getExternalStorageDirectory()+ File.separator+"qqqq.3gp");
        //videoView.setVideoPath("/sdcard/test.3gp");
        //videoView.setVideoPath(path);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/qqqq"));
        videoView.requestFocus();
        videoView.start();

        mStopImageButton = (ImageButton) findViewById(R.id.StopImageButton);
        mStartImageButton = (ImageButton) findViewById(R.id.StartImageButton);
        mPauseImageButton = (ImageButton) findViewById(R.id.PauseImageButton);

        //绑定播放曲目
        mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.qqqq);
        //添加按钮事件
        //开始按钮的事件操作：
        mStartImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        //暂停按钮的事件操作：
        mPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.pause();
                }else{
                    videoView.start();
                }
            }
        });

        //停止按钮的事件操作：
        mStopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
            }
        });
    }
}
