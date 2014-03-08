package com.mv2studio.tswp.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;

import com.mv2studio.tswp.R;

public class MainActivity extends Activity {
	
	protected Typeface tLight, tCond, tCondBold, tCondLight, tThin;
	protected OnBackPressedListener onBackPressedListener;
	public static String P_LOGGED_KEY = "logged",
					     P_USER_TYPE_KEY = "user",
					     P_STUDENT_KEY = "student",
					     P_TEACHER_KEY = "teacher";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
		tCondLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-CondensedLight.ttf");
		tLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		tCond = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Condensed.ttf");
		tCondBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BoldCondensed.ttf");
//		MaisCalendarParser.readAsset(this);
		
//		
//		// test DB
//		Db db = new Db(this);
//		for(int i = 1; i < 10; i++) {
//			Calendar c = Calendar.getInstance();
//			c.add(Calendar.MINUTE, i);
//			Date start = c.getTime();
//			c.add(Calendar.MINUTE, i*10);
//			Date end = c.getTime();
//			
//			TClass t = new TClass("name"+i, "room"+i, start, end, true);
//			db.insertItem(t);
//		}
//		Intent ii = new Intent(this, NotificationService.class);
//		startService(ii);
		// load prefs
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isLogged = prefs.getBoolean(P_LOGGED_KEY, false);
		boolean isTeacher = prefs.getBoolean(P_USER_TYPE_KEY, false);
		
		int contentView = R.id.activity_main_content_view;
		
		// choose the right fragment
		Fragment fragment;
		if(isLogged) {
			if(isTeacher) {
				fragment = new TeacherMainFragment();
			} else {
				fragment = new StudentMainFragment();
			}
			
		} else {
			fragment = new WizardFragment();
		}
		
		getFragmentManager().beginTransaction().replace(contentView, fragment).commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	
	public interface OnBackPressedListener {
		public void doBack();
	}

	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
	    this.onBackPressedListener = onBackPressedListener;
	}
	
	@Override
	public void onBackPressed() {
		if(onBackPressedListener != null) {
			try {
			onBackPressedListener.doBack();
			} catch (IllegalArgumentException ex) { super.onBackPressed(); }
		} else {
			super.onBackPressed();
		}	
	}

}
