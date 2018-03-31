package com.yangmaoxin.multifunctionclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Yang maoxin on 2018/1/27.
 */

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("闹钟执行了");

		//闹钟执行后删除
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(PendingIntent.getBroadcast(context, getResultCode(), new Intent(context, AlarmReceiver.class), 0));

		//闹钟执行后打开PlayAlarmActivity
		Intent i = new Intent(context, PlayAlarmActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}
