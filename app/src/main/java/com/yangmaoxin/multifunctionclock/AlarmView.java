package com.yangmaoxin.multifunctionclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Yang maoxin on 2018/1/26.
 */

public class AlarmView extends LinearLayout
{
    private Button btnAddAlarm;
    private ListView lvAlarmList;
    private static final String KEY_ALARM_LIST = "alarmList";
    private ArrayAdapter<AlarmData> adapter;
    private AlarmManager alarmManager;

    public AlarmView(Context context) {
        super(context);
        init();
    }

    public AlarmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlarmView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        btnAddAlarm=findViewById(R.id.btnAddAlarm);
        lvAlarmList=findViewById(R.id.lvAlarmList);

        adapter = new ArrayAdapter<AlarmData>(getContext(), android.R.layout.simple_list_item_1);
        lvAlarmList.setAdapter(adapter);
        readSavedAlarmList();

        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addAlarm();
            }
        });

        //删除闹钟
        lvAlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                new AlertDialog.Builder(getContext()).setTitle("操作选项").setItems(
                        new CharSequence[]{"删除"}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                deleteAlarm(position);
                                break;
                            default:
                                break;
                        }

                    }
                }).setNegativeButton("取消", null).show();

                return true;
            }
        });

    }

    //添加闹钟
    private void addAlarm()
    {
        Calendar c = Calendar.getInstance();

        //创建时间对话框
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {


            //点击对话框的set按钮之后执行以下代码设置闹钟
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                // 根据用户选择的闹钟时间来设置Calendar对象
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                //当前时间的Calendar对象
                Calendar currentTime = Calendar.getInstance();

                //如果设置的闹钟时间小于当前时间,则往后推一天
                if (calendar.getTimeInMillis()<=currentTime.getTimeInMillis())
                {
                    calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);
                }

                //将设置的闹钟时间加入适配器
                AlarmData ad = new AlarmData(calendar.getTimeInMillis());
                adapter.add(ad);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,      //闹钟类型
                        ad.getTime(),                                     //闹钟首次执行时间
                        5*60*1000,                          //闹钟两次执行的间隔时间
                        PendingIntent.getBroadcast(getContext(), ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0)//闹钟响应动作
                );
                saveAlarmList();

                // 显示闹铃设置成功的提示信息
                Toast.makeText(getContext(), "闹钟设置成功啦!"
                        , Toast.LENGTH_SHORT).show();


            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
        {
            /**
            *这里就比较坑爹了，如果不重写onStop()函数，就会出现设置一次闹钟，列表里
            *却出现两个闹钟的情况，查了好久，原来点击确定后会调用该事件监听器的时间，
            *在关闭这个Dialog的时候也会调用一次，解决方法即为重写onStop()方法，
            *并把super.onStop()注释掉
            */
            @Override
            protected void onStop()     //重写该方法是为了避免调用两次onTimeSet
            {
                //super.onStop();
            }
        }.show();


    }

    //删除闹钟
    private void deleteAlarm(int position)
    {
        AlarmData ad = adapter.getItem(position);
        adapter.remove(ad);
        saveAlarmList();

        alarmManager.cancel(PendingIntent.getBroadcast(getContext(), ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
    }


    /**
     * 添加了闹钟之后退出程序再次打开，之前创建的闹钟没了，
     *原因是虽然把数据临时的保存在了ListView中，但是没有长时间的保存
     *因此需要编写保存闹钟列表的方法
    */
    private void saveAlarmList()
    {
        //创建Editor容器用来存放闹钟列表
        SharedPreferences.Editor editor = getContext().getSharedPreferences(AlarmView.class.getName(), Context.MODE_PRIVATE).edit();

        //将设置的闹钟时间放入缓存区
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < adapter.getCount(); i++)
        {
            sb.append(adapter.getItem(i).getTime()).append(",");
        }

        //将设置的闹钟时间从缓存区放入Editor容器
        if (sb.length()>1)
        {
            String content = sb.toString().substring(0, sb.length()-1);

            editor.putString(KEY_ALARM_LIST, content);

            System.out.println(content);
        }
        else
        {
            editor.putString(KEY_ALARM_LIST, null);
        }

        editor.commit();
    }

    //读取闹钟列表
    private void readSavedAlarmList()
    {
        //取出闹钟并以字符串的形式放入content
        SharedPreferences sp = getContext().getSharedPreferences(AlarmView.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);

        if (content!=null)
        {
            String[] timeStrings = content.split(",");  //去掉，
            //放回适配器
            for (String string : timeStrings)
            {
                adapter.add(new AlarmData(Long.parseLong(string)));
            }
        }
    }




    private static class AlarmData
    {
        //构造函数，根据传进来的时间（毫秒）来设置相应的日期
        public AlarmData(long time)
        {
            this.time = time;

            date = Calendar.getInstance();
            date.setTimeInMillis(time);

            timeLabel = String.format("%d月%d日 %d:%d",
                    date.get(Calendar.MONTH)+1,
                    date.get(Calendar.DAY_OF_MONTH),
                    date.get(Calendar.HOUR_OF_DAY),
                    date.get(Calendar.MINUTE));
        }

        public long getTime() {
            return time;
        }

        public String getTimeLabel() {
            return timeLabel;
        }

        @Override
        public String toString() {
            return getTimeLabel();
        }

        public int getId(){
            return (int)(getTime()/1000/60);
        }

        private String timeLabel="";
        private long time = 0;
        private Calendar date;
    }
}
