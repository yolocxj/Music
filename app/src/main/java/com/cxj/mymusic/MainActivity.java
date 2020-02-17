package com.cxj.mymusic;

import android.Manifest;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import cxj.com.myplayer.CjTimeInfoBean;
import cxj.com.myplayer.listener.CjOnCompleteListener;
import cxj.com.myplayer.listener.CjOnErrorListener;
import cxj.com.myplayer.listener.CjOnLoadListener;
import cxj.com.myplayer.listener.CjOnParparedListener;
import cxj.com.myplayer.listener.CjOnPauseResumeListener;
import cxj.com.myplayer.listener.CjOnRecordTimeListener;
import cxj.com.myplayer.listener.CjOnTimeInfoListener;
import cxj.com.myplayer.listener.CjOnValumeDBListener;
import cxj.com.myplayer.log.MyLog;
import cxj.com.myplayer.muteenum.MuteEnum;
import cxj.com.myplayer.opengl.CjGLSurfaceView;
import cxj.com.myplayer.player.CjPlayer;
import cxj.com.myplayer.util.CjTimeUtil;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private CjGLSurfaceView cjGLSurfaceView;
    private CjPlayer cjPlayer;
    private TextView tvTime;
    private TextView tvVolume;
    private SeekBar seekBarSeek;
    private SeekBar seekBarVolume;
    private int position = 0;
    private boolean isSeekBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityPermissionsDispatcher.permissionsWithPermissionCheck(this);
        tvTime = findViewById(R.id.tv_time);
        cjGLSurfaceView = findViewById(R.id.cjglsurfaceview);
        seekBarSeek = findViewById(R.id.seekbar_seek);
        seekBarVolume = findViewById(R.id.seekbar_volume);
        tvVolume = findViewById(R.id.tv_volume);
        cjPlayer = new CjPlayer();
        cjPlayer.setVolume(50);
        cjPlayer.setCjGLSurfaceView(cjGLSurfaceView);



        //默认变调又变速

        cjPlayer.setPitch(1.0f);
        cjPlayer.setSpeed(1.0f);
        cjPlayer.setMute(MuteEnum.MUTE_LEFT);
        tvVolume.setText("音量：" + cjPlayer.getVolumePercent() + "%");
        seekBarVolume.setProgress(cjPlayer.getVolumePercent());
        cjPlayer.setWlOnParparedListener(new CjOnParparedListener() {
            @Override
            public void onParpared() {
                MyLog.d("准备好了，可以开始播放声音了");
                cjPlayer.start();
            }
        });
        cjPlayer.setCjOnLoadListener(new CjOnLoadListener() {
            @Override
            public void onLoad(boolean load) {
                if(load)
                {
                    MyLog.d("加载中...");
                }
                else
                {
                    MyLog.d("播放中...");
                }
            }
        });
        cjPlayer.setCjOnPauseResumeListener(new CjOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if(pause)
                {
                    MyLog.d("暂停中...");
                }
                else
                {
                    MyLog.d("播放中...");
                }
            }
        });
        cjPlayer.setCjOnTimeInfoListener(new CjOnTimeInfoListener() {
            @Override
            public void onTimeInfo(CjTimeInfoBean timeInfoBean) {
//                MyLog.d(timeInfoBean.toString());
                Message message = Message.obtain();
                message.what = 1;
                message.obj = timeInfoBean;
                handler.sendMessage(message);

            }
        });
        cjPlayer.setCjOnErrorListener(new CjOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:" + code + ", msg:" + msg);
            }
        });
        cjPlayer.setCjOnValumeDBListener(new CjOnValumeDBListener() {
            @Override
            public void onDbValue(int db) {
     //           MyLog.d("db is: " + db);
            }
        });
        cjPlayer.setCjOnRecordTimeListener(new CjOnRecordTimeListener() {
            @Override
            public void onRecordTime(int recordTime) {
                MyLog.d("record time is " + recordTime);
            }
        });
        cjPlayer.setCjOnCompleteListener(new CjOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("播放完成了");
            }
        });
        seekBarSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    position = cjPlayer.getDuration() * progress / 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cjPlayer.seek(position);
                isSeekBar = false;
            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cjPlayer.setVolume(progress);
                tvVolume.setText("音量：" + cjPlayer.getVolumePercent() + "%");
                Log.d("cxj", "progress is " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    public void begin(View view) {
        cjPlayer.setSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hwd.mp4");
        //cjPlayer.setSource("/mnt/shared/Other/CC.mp4");
        //cjPlayer.setSource("/mnt/shared/Other/KK.mp4");
        //cjPlayer.setSource("/mnt/shared/Other/mydream.m4a");
        //cjPlayer.setSource("/mnt/shared/Other/刀剑.ape");
        //cjPlayer.setSource("http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3");
        //cjPlayer.setSource("http://ngcdn004.cnr.cn/live/dszs/index12.m3u8");
        cjPlayer.parpared();

    }

    public void pause(View view) {
        cjPlayer.pause();
    }

    public void resume(View view) {
        cjPlayer.resume();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1)
            {

                    CjTimeInfoBean cjTimeInfoBean = (CjTimeInfoBean) msg.obj;
                    tvTime.setText(CjTimeUtil.secdsToDateFormat(cjTimeInfoBean.getTotalTime(), cjTimeInfoBean.getTotalTime())
                            + "/" + CjTimeUtil.secdsToDateFormat(cjTimeInfoBean.getCurrentTime(), cjTimeInfoBean.getTotalTime()));
                    seekBarSeek.setProgress(cjTimeInfoBean.getCurrentTime() * 100 / cjTimeInfoBean.getTotalTime());
                if(!isSeekBar && cjTimeInfoBean.getTotalTime() > 0)
                {
                    seekBarSeek.setProgress(cjTimeInfoBean.getCurrentTime() * 100 / cjTimeInfoBean.getTotalTime());
                }
               }
        }
    };

    public void stop(View view) {
        cjPlayer.stop();
    }


    public void next(View view) {
        cjPlayer.playNext(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dz.mp4");
                //"http://ngcdn004.cnr.cn/live/dszs/index.m3u8"
                //"/mnt/shared/Other/刀剑.ape");
                //"/mnt/shared/Other/KK.mp4");
    }

    public void left(View view) {
        cjPlayer.setMute(MuteEnum.MUTE_LEFT);
    }

    public void right(View view) {
        cjPlayer.setMute(MuteEnum.MUTE_RIGHT);
    }

    public void center(View view) {
        cjPlayer.setMute(MuteEnum.MUTE_CENTER);
    }

    public void speed(View view) {
        cjPlayer.setSpeed(1.5f);
        cjPlayer.setPitch(1.0f);
    }

    public void pitch(View view) {
        cjPlayer.setPitch(1.5f);
        cjPlayer.setSpeed(1.0f);
    }

    public void speedpitch(View view) {
        cjPlayer.setSpeed(1.5f);
        cjPlayer.setPitch(1.5f);
    }

    public void normalspeedpitch(View view) {
        cjPlayer.setSpeed(1.0f);
        cjPlayer.setPitch(1.0f);
    }

    public void start_record(View view) {
        //cjPlayer.startRecord(new File("/mnt/shared/Other/textplayer.aac"));
        cjPlayer.startRecord(new File("/mnt/shared/Other/yy.mp4"));
    }

    public void pause_record(View view) {
        cjPlayer.pauseRecord();
    }

    public void goon_record(View view) {
        cjPlayer.resumeRcord();
    }

    public void stop_record(View view) {
        cjPlayer.stopRecord();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void permissions() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
