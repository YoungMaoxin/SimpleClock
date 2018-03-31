package com.yangmaoxin.multifunctionclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by Yang maoxin on 2018/1/23.
 */

public class MainActivity extends AppCompatActivity {

    private TabHost tabHost;
    private StopWatchView stopWatchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost=findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("tabTime").setIndicator("时钟").setContent(R.id.tabTime));
        tabHost.addTab(tabHost.newTabSpec("tabAlarm").setIndicator("闹钟").setContent(R.id.tabAlarm));
        tabHost.addTab(tabHost.newTabSpec("tabTimer").setIndicator("计时器").setContent(R.id.tabTimer));
        tabHost.addTab(tabHost.newTabSpec("tabStopWatch").setIndicator("秒表").setContent(R.id.tabStopWatch));

        stopWatchView = findViewById(R.id.tabStopWatch);
    }
    @Override
    protected void onDestroy() {

        stopWatchView.onDestory();

        super.onDestroy();
    }


}
