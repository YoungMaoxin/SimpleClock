package com.yangmaoxin.multifunctionclock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yang maoxin on 2018/1/30.
 */

public class StopWatchView extends LinearLayout {

    private int tenMSecs = 0;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;

    private TextView tvHour,tvMin,tvSec,tvMSec;
    private Button btnStart,btnResume,btnReset,btnPause,btnLap;

    private ArrayAdapter<String> adapter;
    private static final int MSG_WHAT_SHOW_TIME = 1;

    public StopWatchView(Context context) {
        super(context);
    }

    public StopWatchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StopWatchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //初始均设置为0
        tvHour = findViewById(R.id.timeHour);
        tvHour.setText("0");
        tvMin = findViewById(R.id.timeMin);
        tvMin.setText("0");
        tvSec = findViewById(R.id.timeSec);
        tvSec.setText("0");
        tvMSec =  findViewById(R.id.timeMSec);
        tvMSec.setText("0");

        btnStart = (Button) findViewById(R.id.btnSWStart);
        btnStart.setOnClickListener(new View.OnClickListener() {

            /**点击开始按钮后开始计时，执行startTimer()方法，并且把
             * 开始按钮设置为不可见，把暂停和计次按钮设置为可见
             */
            @Override
            public void onClick(View v) {
                startTimer();

                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnLap.setVisibility(View.VISIBLE);
            }
        });

        btnLap = findViewById(R.id.btnSWLap);
        btnLap.setOnClickListener(new View.OnClickListener() {

            //计次
            @Override
            public void onClick(View v) {
                adapter.insert(String.format("%d:%d:%d.%d", tenMSecs/100/60/60,tenMSecs/100/60%60,tenMSecs/100%60,tenMSecs%100), 0);
            }
        });

        btnPause =  findViewById(R.id.btnSWPause);
        btnPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /**点击暂停按钮后暂停计时，执行stopTimer()方法，并且把
                 * 暂停、计次按钮设置为不可见，把继续、重置按钮设置为可见
                 */
                stopTimer();

                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
                btnLap.setVisibility(View.GONE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });

        btnResume = findViewById(R.id.btnSWResume);
        btnResume.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /**点击继续按钮后继续计时，执行startTimer()方法，并且把
                 * 继续、重置按钮设置为不可见，把暂停、计次按钮设置为可见
                 */
                startTimer();
                btnResume.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.GONE);
                btnLap.setVisibility(View.VISIBLE);
            }
        });

        btnReset =  findViewById(R.id.btnSWReset);
        btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /**点击重置按钮后清零，执行stopTimer()方法，并且
                 * 只呈现开始按钮
                 */
                stopTimer();
                tenMSecs = 0;
                adapter.clear();

                btnLap.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
                btnResume.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });


        btnLap.setVisibility(View.GONE);
        btnPause.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        btnResume.setVisibility(View.GONE);

        ListView lvTimeList = findViewById(R.id.lvWatchTimeList);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        lvTimeList.setAdapter(adapter);

        TimerTask showTimeTask = new TimerTask() {

            @Override
            public void run() {
                hander.sendEmptyMessage(MSG_WHAT_SHOW_TIME);
            }
        };
        timer.schedule(showTimeTask, 200, 200);
    }

    //开始计时的方法
    private void startTimer()
    {
        if (timerTask==null) {
            timerTask = new TimerTask() {

                @Override
                public void run() {
                    tenMSecs++;
                }
            };
            timer.schedule(timerTask, 10, 10);
        }
    }

    //停止倒计时
    private void stopTimer()
    {
        if (timerTask!=null) {
            timerTask.cancel();
            timerTask=null;
        }
    }



    private Handler hander = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_SHOW_TIME:
                    tvHour.setText(tenMSecs/100/60/60+"");
                    tvMin.setText(tenMSecs/100/60%60+"");
                    tvSec.setText(tenMSecs/100%60+"");
                    tvMSec.setText(tenMSecs%100+"");
                    break;
                default:
                    break;
            }
        };
    };



    public void onDestory() {
        timer.cancel();
    }
}
