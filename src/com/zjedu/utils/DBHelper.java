package com.zjedu.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author westlakeboy
 *
 */
public class DBHelper extends SQLiteOpenHelper{

	private static final String DB_NAME = "mobilepolicesys";
	private static final int BD_VERSION = 1;
	public DBHelper(Context context) {
		super(context,DB_NAME,null,BD_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE event (id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,eventid INTEGER,status  TEXT,content  TEXT,time  TEXT,location  TEXT,title TEXT,path TEXT)");
		db.execSQL("CREATE TABLE help (id  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,helpid  INTEGER,content  TEXT,username  TEXT,time  TEXT,addr  TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exisis event");
		db.execSQL("drop table if exisis help");
		onCreate(db);
	}

}
