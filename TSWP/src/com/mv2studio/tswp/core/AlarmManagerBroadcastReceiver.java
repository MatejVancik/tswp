package com.mv2studio.tswp.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mv2studio.tswp.model.TClass;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String act = intent.getAction();
		Log.e("", "RECIEVED: "+act);
		if(act.equals(NotificationService.ALARM_TAG)) {
			TClass cl = (TClass) intent.getSerializableExtra(NotificationService.TCLASS_KEY);
			new Notification(context, cl);
		}
		
	}

}
