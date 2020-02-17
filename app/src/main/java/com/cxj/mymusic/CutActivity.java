package com.cxj.mymusic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cxj.com.myplayer.CjTimeInfoBean;
import cxj.com.myplayer.listener.CjOnParparedListener;
import cxj.com.myplayer.listener.CjOnPcmInfoListener;
import cxj.com.myplayer.listener.CjOnTimeInfoListener;
import cxj.com.myplayer.log.MyLog;
import cxj.com.myplayer.player.CjPlayer;


public class CutActivity extends AppCompatActivity{


    private CjPlayer cjPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutaudio);
        cjPlayer = new CjPlayer();

        cjPlayer.setWlOnParparedListener(new CjOnParparedListener() {
            @Override
            public void onParpared() {
                cjPlayer.cutAudioPlay(20, 40, true);
            }
        });

        cjPlayer.setCjOnTimeInfoListener(new CjOnTimeInfoListener() {
            @Override
            public void onTimeInfo(CjTimeInfoBean timeInfoBean) {
                MyLog.d(timeInfoBean.toString());
            }
        });

        cjPlayer.setCjOnPcmInfoListener(new CjOnPcmInfoListener() {
            @Override
            public void onPcmInfo(byte[] buffer, int buffersize) {
                MyLog.d("buffersize: " + buffersize);
            }

            @Override
            public void onPcmRate(int samplerate, int bit, int channels) {
                MyLog.d("samplerate: " + samplerate);
            }
        });
    }

    public void cutaudio(View view) {

        cjPlayer.setSource("/mnt/shared/Other/刀剑.ape");
        cjPlayer.parpared();

    }
}
