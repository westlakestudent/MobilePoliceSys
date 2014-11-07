package com.zjedu.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zjedu.mobilepolicesys.SystemConfig;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WeatherInfoProvider {
	public final String TAG = this.getClass().getName();
	private Handler mHandler; 
	private String mCityID;
	private String mTemp;
	private String mWeather;
	
	public WeatherInfoProvider(Handler handler) {
		mHandler = handler;
	}	
	
	public void startGetWeatherInfo(String cityID) {
		mCityID = cityID;
		doweather();
		dotemp();
	}	
	
	private void parseTemp(JSONObject jsonObj) {
		if (jsonObj == null) 
			mTemp = null;
		
		try {
			mTemp = jsonObj.getJSONObject("weatherinfo").getString("temp").toString();
			Log.i(TAG, "current temperature:" + mTemp);	
			notifyTempInfoReady(mTemp);				
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: parseTemp");	
			e.printStackTrace();
			mTemp = null;
		}
	}
	
	private void parseWeather(JSONObject jsonObj) {
		if (jsonObj == null) 
			mWeather = null;
		
		try {
			mWeather = jsonObj.getJSONObject("weatherinfo").getString("weather").toString();
			notifyWeatherInfoReady(mWeather);			
			Log.i(TAG, "current weather:" + mWeather);			
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: parseWeather");	
			e.printStackTrace();
			mWeather = null;
		}
	}
	
	
	private void doweather(){
		String strUrlWeather = "http://www.weather.com.cn/data/cityinfo/" + mCityID + ".html";
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.GET, strUrlWeather, new weatherTask());
		
	}
	
	private class weatherTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			Log.d(TAG, arg0 + arg1 + "");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, info.result);
			try {
				parseWeather(new JSONObject(info.result));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
	private void dotemp(){
		String strUrlTemp = "http://www.weather.com.cn/data/sk/" + mCityID + ".html";
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.GET, strUrlTemp, new tempTask());
		
	}
	
	private class tempTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			Log.d(TAG, arg0 + arg1 + "");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, info.result);
			try {
				parseTemp(new JSONObject(info.result));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void notifyTempInfoReady(String temp) {	
		Message msg = new Message();
		msg.what = SystemConfig.MSG_TEMP_INFO_READY;
		msg.obj = temp;
		mHandler.sendMessage(msg);
	}
	
	private void notifyWeatherInfoReady(String weather) {	
		Message msg = new Message();
		msg.what = SystemConfig.MSG_WEATHER_INFO_READY;
		msg.obj = weather;
		mHandler.sendMessage(msg);
	}
	
	public static Map<String, String> loadMappingTable(Context context) {    	
    	AssetManager am = context.getAssets();
    	Map<String, String> cityToID = new HashMap<String, String>();
		
		try {
			InputStream in = am.open("city_to_id.txt");				
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "Unicode"));
			
			String strLine = null;		            
            while ((strLine = reader.readLine()) != null) {
            	String[] strIDtoCity = strLine.split("\t");
            	if (strIDtoCity.length != 2) 
            		continue;
            	cityToID.put(strIDtoCity[1], strIDtoCity[0]);				    
			}   
            in.close();			
		  } catch (IOException e) {
			  Log.e("", "city_to_id.txt exception");
			  e.printStackTrace();
		  }	    
		
		return cityToID;
    }
}
