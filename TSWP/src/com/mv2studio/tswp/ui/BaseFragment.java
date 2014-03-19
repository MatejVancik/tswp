package com.mv2studio.tswp.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

	protected Typeface tLight, tCond, tCondBold, tCondLight, tThin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context c = getActivity();
		tThin = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Thin.ttf");
		tCondLight = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-CondensedLight.ttf");
		tLight = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Light.ttf");
		tCond = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Condensed.ttf");
		tCondBold = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
	}
	
}
