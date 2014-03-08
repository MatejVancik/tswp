package com.mv2studio.tswp.db;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
	
	public void insertItems(List<TClass> items) {
		open();
		db.beginTransaction();
		for(TClass item: items) {
			ContentValues values = getContentValues(item);
			db.insert(SQLHelper.TABLE_SCHOOL, null, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		close();
	}
	
	public void insertItem(TClass cl) {
		ContentValues values = getContentValues(cl);
		open();
		db.insert(SQLHelper.TABLE_SCHOOL, null, values);
		close();
	}
	
	public void removeItem(int id) {
		open();
		db.delete(SQLHelper.TABLE_SCHOOL, SQLHelper.SCHOOL_COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
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
		boolean notify = cl.isNotify();
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String startDate = sdf.format(start);
		String endDate = sdf.format(end);
		
		ContentValues values = new ContentValues();
		values.put(SQLHelper.SCHOOL_COLUMN_NAME, name);
		values.put(SQLHelper.SCHOOL_COLUMN_ROOM, room);
		values.put(SQLHelper.SCHOOL_COLUMN_START_DATE, startDate);
		values.put(SQLHelper.SCHOOL_COLUMN_END_DATE, endDate);
		values.put(SQLHelper.SCHOOL_COLUMN_NOTIFY, notify);
		
		return values;
	}
	
	public ArrayList<TClass> getAllClasses(){
		ArrayList<TClass> classes = new ArrayList<TClass>();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		open();
		Cursor cursor = db.query(SQLHelper.TABLE_SCHOOL, null, null, null, null, null, null);
		if(cursor != null && cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(0);
				String name = cursor.getString(1),
					   room = cursor.getString(2);
				boolean notify = cursor.getInt(5) > 0;
				Date start = null, end = null;
				try {
					start = sdf.parse(cursor.getString(3));
					end = sdf.parse(cursor.getString(4));
				} catch (ParseException e) {
					e.printStackTrace();
				}	
				
				classes.add(new TClass(id, name, room, start, end, notify));
				
			} while(cursor.moveToNext());
			cursor.close();
		}
		close();
		return classes;
	}
	
}
