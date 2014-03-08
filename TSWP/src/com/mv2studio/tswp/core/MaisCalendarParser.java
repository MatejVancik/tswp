package com.mv2studio.tswp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class MaisCalendarParser {

	public static void parseCalendar(String ical) {
		
		
	}

	public static void readAsset(Context context) {
		StringBuilder buf = new StringBuilder();

		try {
			InputStream json = context.getAssets().open("report0.ICS");

			BufferedReader in = new BufferedReader(new InputStreamReader(json));
			String str;

			while ((str = in.readLine()) != null) {
				buf.append(str);
			}
			in.close();

			parseCalendar(buf.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
