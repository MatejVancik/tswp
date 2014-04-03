package com.mv2studio.tswp.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.mv2studio.tswp.R;
import com.mv2studio.tswp.core.MaisCalendarParser;
import com.mv2studio.tswp.core.Prefs;

public class MainActivity extends FragmentActivity {
	
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

		// load prefs
		boolean isLogged = Prefs.getBoolValue(P_LOGGED_KEY, this);
		boolean isTeacher = Prefs.getBoolValue(P_TEACHER_KEY, this);
		
		// choose the right fragment
		if(isLogged) {
			if(isTeacher) {
				replaceFragment(new TeacherMainFragment());
			} else {
				findViewById(R.id.activity_main_student_layout).setVisibility(View.VISIBLE);
				ViewPager pager = (ViewPager) findViewById(R.id.student_pager);
				pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
				PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.student_tabs);
				tabs.setViewPager(pager);
			}
		} else {
			replaceFragment(new WizardFragment());
		}
		
	}

	public void replaceFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_content_view, fragment).commit();
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
	
	private class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int arg0) {
			
			switch(arg0) {
			case 0:
				return new StudentScheduleFragment();
			case 1:
				return new StudentEventFragment();
			}
			
			return null;
		}
		
		@Override
		public int getCount() {
			return 2;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			switch(position) {
			case 0:
				return "rozvrh";
			case 1:
				return "udalosti";
			default: return "";
			}
			
		}
		
	}

}
