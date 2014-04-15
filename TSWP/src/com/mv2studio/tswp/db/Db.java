package com.mv2studio.tswp.db;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mv2studio.tswp.model.EventFile;
import com.mv2studio.tswp.model.TClass;

public class Db {

	private Context context;
	private SQLHelper dbHelper;
	private SQLiteDatabase db;
	private String dateFormat = "yyyy-MMM-dd HH:mm:ss";
	
	public Db(Context context) {
		this.context = context;
		dbHelper = new SQLHelper(context);
	}
	
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		try {
			dbHelper.close();
		} catch (Exception e ) {e.printStackTrace();}
	}
	
	private void insertItems(List<TClass> items, String table) {
		open();
		db.beginTransaction();
		System.out.println("inserting items: "+items.size());
		for(TClass item: items) {
			ContentValues values = getContentValues(item);
			if (table.equals(SQLHelper.TABLE_EVENT)) {
				values.put(SQLHelper.SCHOOL_COLUMN_ID, item.getId());
				values.put(SQLHelper.EVENT_COLUMN_DESC, item.getDesc());
				values.put(SQLHelper.EVENT_COLUMN_FILES, getFilesString(item.getFiles()));
			}
			System.out.println("db: "+db.insert(table, null, values));
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		close();
	}
	
	public void insertClasses(List<TClass> items) {
		insertItems(items, SQLHelper.TABLE_SCHOOL);
	}
	
	public  void insertEvents(List<TClass> events) {
		insertItems(events, SQLHelper.TABLE_EVENT);
	}

	private String getFilesString(ArrayList<EventFile> list) {
		JSONArray array = new JSONArray();
		try {
			for (EventFile f : list) {
				JSONObject obj = new JSONObject();
				obj.put("id", f.id);
				obj.put("n", f.name);
				array.put(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array.toString();
	}
	
	private ArrayList<EventFile> parseFiles(String json) {
		ArrayList<EventFile> ret = new ArrayList<EventFile>();
		try {
			JSONArray array = new JSONArray(json);
			for(int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				ret.add(new EventFile(obj.getString("n"), obj.getInt("id")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	private void insertItem(TClass cl, String table) {
		ContentValues values = getContentValues(cl);
		if (table.equals(SQLHelper.TABLE_EVENT)) {
			values.put(SQLHelper.SCHOOL_COLUMN_ID, cl.getId());
			values.put(SQLHelper.EVENT_COLUMN_DESC, cl.getDesc());
			values.put(SQLHelper.EVENT_COLUMN_FILES, getFilesString(cl.getFiles()));
		}
		open();
		db.insert(table, null, values);
		close();
	}
	
	public void insertEvent(TClass event) {
		insertItem(event, SQLHelper.TABLE_EVENT);
	}
	
	public void insertClass(TClass cl) {
		insertItem(cl, SQLHelper.TABLE_SCHOOL);
	}
	
	
	
	public void removeClass(int id) {
		open();
		db.delete(SQLHelper.TABLE_SCHOOL, SQLHelper.SCHOOL_COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
		close();
	}
	
	public void removeEvent(int id) {
		open();
		db.delete(SQLHelper.TABLE_EVENT, SQLHelper.SCHOOL_COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
		close();
	}
	
	
	
	public void setNotifyOnClass(int id, boolean notify) {
		open();
		ContentValues values = new ContentValues();
		values.put(SQLHelper.SCHOOL_COLUMN_NOTIFY, notify ? 1 : 0);
		db.update(SQLHelper.TABLE_SCHOOL, values, SQLHelper.SCHOOL_COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
		close();
	}
	
	
	private ContentValues getContentValues(TClass cl) {
		String name = cl.getName(), room = cl.getRoom();
		Date start = cl.getStart(), end = cl.getEnd();
		boolean notify = cl.isNotify(), exercise = cl.isExcercise();
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String startDate = sdf.format(start);
		String endDate = sdf.format(end);
		
		ContentValues values = new ContentValues();
		values.put(SQLHelper.SCHOOL_COLUMN_NAME, name);
		values.put(SQLHelper.SCHOOL_COLUMN_ROOM, room);
		values.put(SQLHelper.SCHOOL_COLUMN_START_DATE, startDate);
		values.put(SQLHelper.SCHOOL_COLUMN_END_DATE, endDate);
		values.put(SQLHelper.SCHOOL_COLUMN_EXERCISE, exercise);
		values.put(SQLHelper.SCHOOL_COLUMN_NOTIFY, notify);
		
		return values;
	}
	
	public ArrayList<TClass> getAllEvents(){
		ArrayList<TClass> classes = new ArrayList<TClass>();
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		open();
		Cursor cursor = db.query(SQLHelper.TABLE_EVENT, null, null, null, null, null, SQLHelper.SCHOOL_COLUMN_START_DATE);
		
		if(cursor != null && cursor.moveToFirst()) {
			Date start = null, end = null;
			do {
				int id = cursor.getInt(0);
				String name = cursor.getString(1),
					   desc = cursor.getString(2),
					   room = cursor.getString(3);
				
				try {
					Date newStart = sdf.parse(cursor.getString(4));
					start = newStart;
					end = sdf.parse(cursor.getString(5));
				} catch (ParseException e) {
					e.printStackTrace();
				}	;
				ArrayList<EventFile> files = parseFiles(cursor.getString(8));
				System.out.println("adding: "+name);
				TClass cl = new TClass(id, name, room, start, end, false, false, false);
				cl.setDesc(desc);
				cl.setFiles(files);
				classes.add(cl);
				
			} while(cursor.moveToNext());
			cursor.close();
		}
		close();
		
		return classes;
	}
	
	
	public void cleanEvents() {
		ArrayList<TClass> events = new ArrayList<TClass>();
		open();
		db.beginTransaction();
		for(TClass c: events) {
			if(c.getStart().after(new Date())) {
				db.delete(SQLHelper.TABLE_EVENT, SQLHelper.SCHOOL_COLUMN_ID + " = ?", new String[] {String.valueOf(c.getId())});
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		close();
	}
	
	public ArrayList<TClass> getAllClasses(){
		ArrayList<TClass> classes = new ArrayList<TClass>();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		open();
		Cursor cursor = db.query(SQLHelper.TABLE_SCHOOL, null, null, null, null, null, SQLHelper.SCHOOL_COLUMN_START_DATE);
		System.out.println("cursro: "+cursor.getCount());
		if(cursor != null && cursor.moveToFirst()) {
			boolean newDay = true;
			Date start = null, end = null;
			do {
				int id = cursor.getInt(0);
				String name = cursor.getString(1),
					   room = cursor.getString(2);
				boolean notify = cursor.getInt(6) > 0,
						exercise = cursor.getInt(5) >0;
				
				try {
					Date newStart = sdf.parse(cursor.getString(3));
					newDay = (start == null || start.getDay() != newStart.getDay());
					start = newStart;
					end = sdf.parse(cursor.getString(4));
				} catch (ParseException e) {
					e.printStackTrace();
				}	
				
				classes.add(new TClass(id, name, room, start, end, newDay, exercise, notify));
				
			} while(cursor.moveToNext());
			cursor.close();
		}
		close();
		return classes;
	}
}
