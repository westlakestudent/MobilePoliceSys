package com.zjedu.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zjedu.entity.*;
import com.zjedu.utils.DBHelper;

public class HelpDao {

	private static final String TAG = "HelpDao";
	private DBHelper mDBHelper = null;
	
	public HelpDao(Context context){
		mDBHelper = new DBHelper(context);
	}
	
	
	
	public void insertHelps(List<Help> helps){
		if(helps.size() > 0){
			for(Help h : helps)
				insertHelp(h);
		}
	}
	public void insertHelp(Help help){
		SQLiteDatabase db = mDBHelper.getWritableDatabase(); 
		if(checkInsert(help,db)){
			db.close();
			return;
		}
		try{
			String sql = "insert into help (helpid,content,username,time,addr) values(?,?,?,?,?)";
			db.execSQL(sql, new Object[]{help.getId(),help.getContent(),help.getUsername(),help.getTime(),help.getAddr()});
			Log.d(TAG, TAG + "insert success ----" + help.getContent());
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}finally{
			db.close();
		}
	}
	
	public JSONObject check(JSONArray arr){
		if(arr == null)
			return null;
		SQLiteDatabase db = mDBHelper.getWritableDatabase(); 
		boolean same = false;
		JSONArray newarry = new JSONArray();
		JSONObject obj = null;
		try{
			String sql = "select * from help";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			Log.d(TAG, "count:"+count);
			if( count == 0){
				newarry = arr;
				return new JSONObject().put("newarray", newarry);
			}
			for(int j = 0;j<arr.length();j++)
			{
				obj = arr.getJSONObject(j);
				Log.d(TAG, "helpid:" + obj.optInt("id"));
				same = false;
				while(cursor.moveToNext()){
					int helpid = cursor.getInt(cursor.getColumnIndex("helpid"));
					if(helpid == obj.optInt("id"))
						same = true;
				}
				if(!same)
					newarry.put(obj);
			}
			
			return new JSONObject().put("newarray", newarry);
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
			return null;
		}finally{
			db.close();
		}
	}
	
	private boolean checkInsert(Help help,SQLiteDatabase db){
		boolean success = false;
		try{
			String sql = "select * from help where helpid = ?";
			Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(help.getId())});
			if(cursor.getCount() != 0)
				success = true;
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}
		return success;
	}
	
	
	
	public List<Help> findHelps(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase(); 
		List<Help> helps = new ArrayList<Help>();
		try{
			String sql = "select * from help order by time desc";
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				Help help = new Help();
				help.setContent(cursor.getString(cursor.getColumnIndex("content")));
				help.setId(cursor.getInt(cursor.getColumnIndex("helpid")));
				help.setTime(cursor.getString(cursor.getColumnIndex("time")));
				help.setUsername(cursor.getString(cursor.getColumnIndex("username")));
				help.setAddr(cursor.getString(cursor.getColumnIndex("addr")));
				Log.d(TAG, TAG + "helpid---->" + cursor.getString(cursor.getColumnIndex("helpid")));
				helps.add(help);
			}
			
		}catch(SQLException e){
			Log.e(TAG, TAG + e);
		}finally{
			db.close();
		}
		return helps;
	}
}

