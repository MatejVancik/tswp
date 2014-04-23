package com.mv2studio.tswp.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter {

	private Typeface tLight, tCond, tCondBold, tCondLight, tThin;
	
	public SpinnerAdapter(Context c, int resource) {
		super(c, resource);
		
	}

	public SpinnerAdapter(Context c, int resource, List objects) {
		super(c, resource, objects);
		tThin = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Thin.ttf");
		tCondLight = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-CondensedLight.ttf");
		tLight = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Light.ttf");
		tCond = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Condensed.ttf");
		tCondBold = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
	} 

	public TextView getView(int position, View convertView, ViewGroup parent) {
		TextView v = (TextView) super.getView(position, convertView, parent);
		v.setTypeface(tCondBold);
		v.setTextSize(20);
		v.setText(getItem(position).toString());
		return v;
	}

	public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView v = (TextView) super.getView(position, convertView, parent);
		v.setTypeface(tCondLight);
		v.setTextSize(20);
		v.setPadding(20, 15, 20, 15);
		v.setText(getItem(position).toString());
		return v;
	}

}
