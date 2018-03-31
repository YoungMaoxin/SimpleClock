package com.yangmaoxin.multifunctionclock;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Yang maoxin on 2018/1/29.
 */

public class PlayAlarmActivity extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alarm);

        //开始播放铃声
        mediaPlayer=MediaPlayer.create(this,R.raw.music);
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        mediaPlayer.release();
    }


}
