package com.zjedu.dao;

import java.util.ArrayList;
import java.util.List;

import com.zjedu.entity.Event;
import com.zjedu.utils.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * @author westlakeboy
 *
 */
public class EventDao {

	private static final String TAG = "EventDao";
	private DBHelper mDBHelper = null;
	
	public EventDao(Context context){
		mDBHelper = new DBHelper(context);
	}
	
	
	private boolean checkevent(Event event,SQLiteDatabase db){
		boolean success = false;
		try{
			String sql = "select * from event where eventid = ?";
			Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(event.getId())});
			if(cursor.getCount() != 0)
				success = true;
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}
		return success;
		
	}
	
	
	public void insertEvent(Event event){
		SQLiteDatabase db = mDBHelper.getWritableDatabase(); 
		if(checkevent(event,db)){
			db.close();
			return;
		}
		try{
			String sql = "insert into event (status,time,content,location,title,eventid,path) values(?,?,?,?,?,?,?)";
			db.execSQL(sql, new Object[]{event.getStatus(),event.getTime(),event.getContent(),event.getLocation(),event.getTitle(),event.getId(),event.getPath()});
			Log.d(TAG, TAG + "insert success ----" + event.getTitle());
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}finally{
			db.close();
		}
	}
	
	public List<Event> findEvents(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase(); 
		List<Event> events = new ArrayList<Event>();
		try{
			String sql = "select * from event order by time desc";
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				Event event = new Event();
				event.setContent(cursor.getString(cursor.getColumnIndex("content")));
				event.setId(cursor.getInt(cursor.getColumnIndex("eventid")));
				event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
				event.setStatus(cursor.getString(cursor.getColumnIndex("status")));
				event.setTime(cursor.getString(cursor.getColumnIndex("time")));
				event.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				event.setPath(cursor.getString(cursor.getColumnIndex("path")));
				Log.d(TAG, TAG + "---->" + cursor.getString(cursor.getColumnIndex("title")));
				events.add(event);
			}
			
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}finally{
			db.close();
		}
		return events;
	}
	
	
	public Event findEvent(int eventid){
		Event event = new Event();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		try{
			String sql = "select * from event where eventid = ?";
			Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(eventid)});
			while(cursor.moveToNext()){
				event.setContent(cursor.getString(cursor.getColumnIndex("content")));
				event.setId(cursor.getInt(cursor.getColumnIndex("eventid")));
				event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
				event.setStatus(cursor.getString(cursor.getColumnIndex("status")));
				event.setTime(cursor.getString(cursor.getColumnIndex("time")));
				event.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				event.setPath(cursor.getString(cursor.getColumnIndex("path")));
				Log.d(TAG, TAG + "---->" + cursor.getString(cursor.getColumnIndex("title")));
			}
			
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}finally{
			db.close();
		}
		
		return event;
	}
	
	
	public void deleteEvent(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		try{
			String sql = "delete from event";
			db.execSQL(sql);
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}finally{
			db.close();
		}
	}
}
