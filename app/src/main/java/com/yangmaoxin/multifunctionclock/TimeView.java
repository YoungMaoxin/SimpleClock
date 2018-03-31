package com.yangmaoxin.multifunctionclock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.zip.DataFormatException;

/**
 * Created by Yang maoxin on 2018/1/24.
 */

public class TimeView extends LinearLayout {

    private TextView tvTime;

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate()        //初始化完成执行的操作
    {
        super.onFinishInflate();

        tvTime=findViewById(R.id.tvTime);

    }

    @Override
    protected void onVisibilityChanged(View changedView,int visibility)
    {
        super.onVisibilityChanged(changedView,visibility);

        //判断，如果可见，则发送空消息；否则，把所有的消息移除
        if(visibility==View.VISIBLE)
        {
            timerHandler.sendEmptyMessage(0);
        }
        else
        {
            timerHandler.removeMessages(0);
        }

    }

//刷新时间
    private void refreshTime()
    {
        //获取当前时间
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
//        Date curDate = new Date(System.currentTimeMillis());//获取当前时间

        //格式化显示
        String str = String.format("%d年%d月%d日 %d:%d:%d ",year,month,day,hour,minute,second);

        tvTime.setText(str);
    }



    private Handler timerHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            refreshTime();
            if(getVisibility()== View.VISIBLE)          //可见的时候每隔1秒刷新一次
                timerHandler.sendEmptyMessageDelayed(0,1000);
        }
    };


}
