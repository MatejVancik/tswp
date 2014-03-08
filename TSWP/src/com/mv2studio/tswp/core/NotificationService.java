package com.mv2studio.tswp.core;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mv2studio.tswp.db.Db;
import com.mv2studio.tswp.model.TClass;

public class NotificationService extends Service {
	
	public static String BASE_TAG = "com.mv2studio.tswp.",
						 ALARM_TAG = BASE_TAG + "alarm",
						 TCLASS_KEY = BASE_TAG + "tclass";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
	
	@Override
	public void onCreate() {
		setAllAlarms();
		super.onCreate();
		stopSelf();
	}
	
	private void setAllAlarms() {
		ArrayList<TClass> classes = new Db(this).getAllClasses();
		for(TClass cl: classes) {
			if(cl.isNotify()) setAlarm(cl);
		}
	}

	public void setAlarm(TClass tClass) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(tClass.getStart());

		Calendar now = Calendar.getInstance();
		
		while(calendar.before(now)) {
			calendar.add(Calendar.DAY_OF_MONTH, 7);
		}
		Log.e("", "SETTING NOTIFICATION AT: "+calendar.getTimeInMillis());
		
		
		AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmManagerBroadcastReceiver.class);
		intent.setAction(ALARM_TAG);
		intent.putExtra(TCLASS_KEY, tClass);
		PendingIntent pi = PendingIntent.getBroadcast(this, tClass.getId(), intent, 0);
//		AlarmManager.INTERVAL_DAY * 7
//		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pi);
	}
}
