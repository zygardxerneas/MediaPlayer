package com.example.admin.musicplayer;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MediaPlayer  mMediaPlayer;
    ImageButton mStopImageButton;
    ImageButton mStartImageButton;
    ImageButton mPauseImageButton;
    ImageButton mNextImageButton;
    ImageButton mLastImageButton;
    ImageButton mBackImageButton;
    TextView mTextView;
    SeekBar seekBar;
    Timer timer;

    boolean bIsPlaying=false;
    boolean isPause=true;
    boolean isSeekBarChanging;
    int currentPosition;

    int currenti=0;
    ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();

    ListView myListView;

    public void scanAllAudioFiles(){

        //查询媒体数据库
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if(cursor.moveToFirst()){

            while (!cursor.isAfterLast()) {

                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲名
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if(size>1024*800){//如果文件大小大于800K，将该文件信息存入到map集合中
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("musicId", id);
                    map.put("musicTitle", tilte);
                    map.put("musicFileUrl", url);
                    map.put("music_file_name", tilte);
                    map.put("music_author",author);
                    map.put("music_url",url);
                    map.put("music_duration",duration);
                    map.put("music_img",R.drawable.img);
                    mylist.add(map);
                }
                cursor.moveToNext();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanAllAudioFiles();

        //绑定播放曲目
        mMediaPlayer=new MediaPlayer();
        try {
            mMediaPlayer.setDataSource((String) mylist.get(currenti).get("music_url"));
            mMediaPlayer.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        displaySecond();
    }

    void displayMain()
    {
        setContentView(R.layout.activity_main);

        mStopImageButton=(ImageButton) findViewById(R.id.StopImageButton);
        mStartImageButton=(ImageButton) findViewById(R.id.StartImageButton);
        mPauseImageButton=(ImageButton) findViewById(R.id.PauseImageButton);
        mNextImageButton=(ImageButton) findViewById(R.id.NextImageButton);
        mLastImageButton=(ImageButton) findViewById(R.id.LastImageButton);
        mBackImageButton=(ImageButton) findViewById(R.id.BackImageButton);
        mTextView=(TextView) findViewById(R.id.mTextView);
        seekBar=(SeekBar) findViewById(R.id.seekBar);

        //监听滚动条事件
        seekBar.setOnSeekBarChangeListener(new MySeekBar());

        //添加按钮事件
        //开始按钮的事件操作：
        mStartImageButton.setOnClickListener(new ImageButton.OnClickListener()
        {

            public void onClick(View v)
            {
                try
                {
                    if ( !bIsPlaying )
                    {
                        bIsPlaying = true;
				/* 装载资源中的音乐 */
                        myplay(currenti);
                        isPause=false;

                    }
                }
                catch (IllegalStateException e)
                {
                    e.printStackTrace();
                }
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    public void onCompletion(MediaPlayer arg0)
                    {
                        mMediaPlayer.release();
                    }
                });
            }
        });

        //暂停按钮的事件操作：
        mPauseImageButton.setOnClickListener(new ImageButton.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                if (!isPause)
                {
                    if(mMediaPlayer.isPlaying())
                    {
                        mMediaPlayer.pause();//暂停播放
                        currentPosition=mMediaPlayer.getCurrentPosition();
                        timer.purge();
                        mTextView.setText("已经暂停，请再次按暂停键继续播放！");
                    }else{
                        mMediaPlayer.start();//继续播放
                        mTextView.setText("《"+(String) mylist.get(currenti).get("musicTitle")+"》恢复播放！");
                    }
                }
            }
        });

        //停止按钮的事件操作：
        mStopImageButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                bIsPlaying=false;
                isPause=true;
                currentPosition=0;
                timer.purge();
                mTextView.setText("当前为停止状态，请按开始键播放音乐！");
            }
        });

        //下一首按钮添加事件
        mNextImageButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currenti=(currenti+1) % mylist.size();
                currentPosition=0;
                myplay(currenti);
            }
        });

        //上一首按钮添加事件
        mLastImageButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currenti=(currenti+mylist.size()-1) % mylist.size();
                currentPosition=0;
                myplay(currenti);
            }
        });

        mBackImageButton.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displaySecond();
            }
        });
    }

    void displaySecond()
    {
        setContentView(R.layout.activity_second);
        myListView=(ListView) findViewById(R.id.myListView);
        SimpleAdapter adapter=new SimpleAdapter(this,
                mylist,
                R.layout.list_item,
                new String[]{"musicTitle","music_author","music_img"},
                new int[]{R.id.musictitle,R.id.musicauther,R.id.img});
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //获得选中项的HashMap对象
                currenti=arg2;
                currentPosition=0;
                displayMain();
                myplay(currenti);

            }
        });
    }

    void myplay(int i)
    {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(((String) mylist.get(currenti).get("music_url")));
            mMediaPlayer.prepare();
            //mMediaPlayer.start();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPosition);
                    seekBar.setMax(mediaPlayer.getDuration());
                }
            });
            timer=new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    if(!isSeekBarChanging){
                        seekBar.setProgress(mMediaPlayer.getCurrentPosition());
                    }
                }
            },0,50);
            bIsPlaying=true;
            isPause=false;
            /* 设置是否循环 */
            mMediaPlayer.setLooping(true);
            mTextView.setText("正在播放《"+(String) mylist.get(i).get("musicTitle")+"》");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mMediaPlayer.seekTo(seekBar.getProgress());
        }
    }

}
