package com.mv2studio.tswp.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * application preferences. User can set notifications (time, vibrations, sound)
 */
public abstract class Prefs {
	
	public static final String  TIME_TAG = "pref_time",
								TIME_TAG_INT = "pref_time_int",
								VIB_TAG = "pref_vib",
								SOUND_TAG = "pref_sound";
	
	public static boolean getBoolValue(String key, Context context) {
		SharedPreferences prefs = getDataPrefs(context);
		return prefs.getBoolean(key, false);
	}

	public static void storeBoolValue(String key, boolean value, Context context) {
		SharedPreferences prefs = getDataPrefs(context);
		Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static int getIntValue(String key, Context context) {
		SharedPreferences prefs = getDataPrefs(context);
		return prefs.getInt(key, -1);
	}

	public static void storeIntValue(String key, int value, Context context) {
		SharedPreferences prefs = getDataPrefs(context);
		Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static String getString(String key, Context context) {
		SharedPreferences prefs = getDataPrefs(context);
		return prefs.getString(key, "");
	}
	
	

	public static void storeString(String key, String value, Context context) {
		SharedPreferences prefs = getDataPrefs(context);
		Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void deleteValueFromPrefs(String key, Context context) {
		SharedPreferences prefs = getDataPrefs(context);
		Editor editor = prefs.edit();
		editor.remove(key);
		editor.commit();
	}

	private static SharedPreferences getDataPrefs(Context context) {
		return context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
	}

}
