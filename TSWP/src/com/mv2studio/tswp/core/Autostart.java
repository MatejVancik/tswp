package com.mv2studio.tswp.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Autostart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, NotificationService.class);
		context.startService(i);

	}

}
