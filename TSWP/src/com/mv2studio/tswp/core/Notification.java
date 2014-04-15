package com.mv2studio.tswp.core;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.mv2studio.tswp.R;
import com.mv2studio.tswp.model.TClass;
import com.mv2studio.tswp.ui.MainActivity;

public class Notification {
	
	public Notification(Context context, TClass cl) {
		String contentTitle = cl.getName();
		String contentText =  cl.getRoom();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
											 .setContentTitle(contentTitle)
											 .setContentText(contentText)
											 .setSmallIcon(R.drawable.ic_launcher);

		if(Prefs.getBoolValue(Prefs.VIB_TAG, context)) {
			builder.setVibrate(new long[] {1000});
		}
		Log.e("", "NOTIFY VIB: "+Prefs.getBoolValue(Prefs.VIB_TAG, context));
		
		
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		builder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		android.app.Notification notification = builder.build();
		
		// add vibrate flag
		if(Prefs.getBoolValue(Prefs.VIB_TAG, context)) { 
			notification.defaults |= android.app.Notification.DEFAULT_VIBRATE;
		}
		
		// add sound tag
		if(Prefs.getBoolValue(Prefs.SOUND_TAG, context)) {
			notification.defaults |= android.app.Notification.DEFAULT_SOUND;
		}
		
		
		mNotificationManager.notify(1, notification);

	}

}
