package com.mv2studio.tswp.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

public class Department {
	public String name;
	public int id;

	@Override
	public String toString() {
		return name;
	}
	
	public static ArrayList<Faculty> getDepartments(Context c) {
		ArrayList<Faculty> deps = new ArrayList<Faculty>();

		try {
			JSONArray obj = new JSONArray(getJsonFromAssets("school.json", c));
			for (int i = 0; i < obj.length(); i++) {
				JSONObject dep = obj.getJSONObject(i);
				Faculty department = new Faculty();
				department.faculty = dep.getString("FACULTY");

				// LOAD BC
				List<Department> list = new ArrayList<Department>();
				JSONArray array = dep.getJSONArray("DEPARTMENT");
				for (int j = 0; j < array.length(); j++) {
					JSONObject objDep = array.getJSONObject(j);
					Department d = new Department();
					d.id = objDep.getInt("id");
					d.name = objDep.getString("name");
					list.add(d);
				}
				department.dep = list;

				deps.add(department);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return deps;
	}

	private static String getJsonFromAssets(String path, Context c) {
		String jsonString = null;
		AssetManager am = c.getAssets();
		try {
			InputStream is = am.open(path);
			int length = is.available();
			byte[] data = new byte[length];
			is.read(data);
			jsonString = new String(data);
		} catch (IOException e1) {
			// e1.printStackTrace();
		}

		return jsonString;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		return ((Department)o).id == this.id;
	}
	
}
