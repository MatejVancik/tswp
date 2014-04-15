package com.mv2studio.tswp.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mv2studio.tswp.R;
import com.mv2studio.tswp.core.NotificationService;
import com.mv2studio.tswp.core.Prefs;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}
	
	
	private class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.student_prefs);
	    }
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			System.out.println("something changed: "+key);
			if(key.equals(Prefs.TIME_TAG)) {
				String time = sharedPreferences.getString(key, "60");
				try {
					int timeInt = Integer.valueOf(time);
					Prefs.storeIntValue(Prefs.TIME_TAG_INT, timeInt, getApplicationContext());
				} catch(NumberFormatException e) {
					Prefs.storeIntValue(Prefs.TIME_TAG_INT, 60, getApplicationContext());
				}
				System.out.println("chaingint gime");
				Intent i = new Intent(getApplicationContext(), NotificationService.class);
				startService(i);
				
			} else if (key.equals(Prefs.VIB_TAG)) {
				Prefs.storeBoolValue(key, sharedPreferences.getBoolean(key, true), getApplicationContext());
			} else if (key.equals(Prefs.SOUND_TAG)) {
				Prefs.storeBoolValue(Prefs.SOUND_TAG, sharedPreferences.getBoolean(key, true), getApplicationContext());
			}
		}
		
		@Override
		public void onResume() {
		    super.onResume();
		    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
		    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		    super.onPause();
		}
		
	}
	
	
	
	
}
