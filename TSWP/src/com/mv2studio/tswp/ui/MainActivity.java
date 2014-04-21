package com.mv2studio.tswp.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.astuetz.PagerSlidingTabStrip;
import com.mv2studio.mynsa.R;
import com.mv2studio.tswp.core.Prefs;

public class MainActivity extends FragmentActivity {
	
	protected Typeface tLight, tCond, tCondBold, tCondLight, tThin;
	
	private ImageButton refreshButton, settingsButton;
	
	protected OnBackPressedListener onBackPressedListener;
	protected OnRefreshClickListener onRefreshListener;
	public static String P_LOGGED_KEY = "logged",
					     P_USER_TYPE_KEY = "user",
					     P_STUDENT_KEY = "student",
					     P_TEACHER_KEY = "teacher";
	
	protected void onCreateActionBar() {
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.action_bar_student);
		bar.setDisplayShowCustomEnabled(true);
		
		View barView = bar.getCustomView();
		
		
		OnClickListener actionBarClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.action_bar_refresh:
					if(onRefreshListener != null) onRefreshListener.onRefresh(refreshButton);
					break;
				case R.id.action_bar_settings:
					Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
					startActivity(i);
					break;
				}
			}
		};
		
		settingsButton = (ImageButton) barView.findViewById(R.id.action_bar_settings);
		settingsButton.setOnClickListener(actionBarClickListener);
		refreshButton = (ImageButton) barView.findViewById(R.id.action_bar_refresh);
		refreshButton.setOnClickListener(actionBarClickListener);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreateActionBar();
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
			refreshButton.setVisibility(View.VISIBLE);
			if(isTeacher) {
				settingsButton.setVisibility(View.GONE);
				replaceFragment(new TeacherMainFragment());
			} else {
				findViewById(R.id.activity_main_student_layout).setVisibility(View.VISIBLE);
				ViewPager pager = (ViewPager) findViewById(R.id.student_pager);
				pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
				PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.student_tabs);
				tabs.setViewPager(pager);
				setRefreshEnabled(false);
				tabs.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageSelected(int arg0) {
						setRefreshEnabled(arg0 == 1); // enable refresh only on second page
					}
					@Override public void onPageScrolled(int arg0, float arg1, int arg2) {}
					@Override public void onPageScrollStateChanged(int arg0) {}
				});
				
			}
			
		} else {
			settingsButton.setVisibility(View.GONE);
			
			replaceFragment(new WizardFragment());
		}
		
	}


	public void replaceFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_content_view, fragment).commit();
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
	
	public void setRrefreshVisibility(boolean visible) {
		refreshButton.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	public void setRefreshEnabled(boolean enabled) {
		refreshButton.setEnabled(enabled);
		refreshButton.setAlpha(enabled ? 1f : 0.2f);
	}
	
	public void setOnRefreshClickListener(OnRefreshClickListener listener) {
		onRefreshListener = listener;
	}
	
	public interface OnRefreshClickListener {
		public void onRefresh(ImageButton button);
	}

}
