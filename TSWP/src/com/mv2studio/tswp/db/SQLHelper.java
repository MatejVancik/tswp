package com.mv2studio.tswp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "school.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_SCHOOL = "school",
							   TABLE_EVENT = "event",
							   SCHOOL_COLUMN_ID = "_id",
							   SCHOOL_COLUMN_NAME = "name",
							   SCHOOL_COLUMN_ROOM = "room",
							   SCHOOL_COLUMN_START_DATE = "startdate",
							   SCHOOL_COLUMN_END_DATE = "enddate",
							   SCHOOL_COLUMN_EXERCISE = "exercise",
							   SCHOOL_COLUMN_NOTIFY = "notify",
							   EVENT_COLUMN_DESC = "desc",
							   EVENT_COLUMN_FILES = "files";
	
	private static final String CREATE_TABLE_SCHOOL = "create table IF NOT EXISTS " + TABLE_SCHOOL +
													  "(" + SCHOOL_COLUMN_ID + " integer primary key autoincrement, " +
														    SCHOOL_COLUMN_NAME + " text not null, " +
														    SCHOOL_COLUMN_ROOM + " text not null, " +
														    SCHOOL_COLUMN_START_DATE + " date not null, " +
														    SCHOOL_COLUMN_END_DATE + " date not null, " +
														    SCHOOL_COLUMN_EXERCISE + " boolean not null," +
														    SCHOOL_COLUMN_NOTIFY + " boolean not null);";
	
	private static final String CREATE_TABLE_EVENT = "create table IF NOT EXISTS " + TABLE_EVENT +
			  "(" + SCHOOL_COLUMN_ID + " integer primary key, " +
				    SCHOOL_COLUMN_NAME + " text not null, " +
				    EVENT_COLUMN_DESC + " text not null, " +
				    SCHOOL_COLUMN_ROOM + " text not null, " +
				    SCHOOL_COLUMN_START_DATE + " date not null, " +
				    SCHOOL_COLUMN_END_DATE + " date not null, " +
				    SCHOOL_COLUMN_EXERCISE + " boolean not null," +
				    SCHOOL_COLUMN_NOTIFY + " boolean not null, "+
				    EVENT_COLUMN_FILES + " text not null);";
	
	
	
	public SQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SCHOOL);
		db.execSQL(CREATE_TABLE_EVENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHOOL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
		onCreate(db);
	}

}
