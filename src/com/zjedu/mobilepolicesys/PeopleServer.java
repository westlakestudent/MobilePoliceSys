package com.zjedu.mobilepolicesys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.zjedu.mobilepolicesys.R;
import com.zjedu.utils.WeatherInfoProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author westlakeboy
 *
 */
public class PeopleServer extends Activity{

	private Map<String, String> mCityToID = null;
	private WeatherInfoProvider mWeatherInfoProvider = null;
	private ImageView mImage = null;
	private TextView mWeather = null;
	private TextView mTemp = null;
	private TextView mPeopleText = null;
	private boolean running = true;
	private SharedPreferences mPref = null;
	private static final String TAG = "PeopleServer";
	private ProgressDialog mDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		mCityToID = WeatherInfoProvider.loadMappingTable(this);
		mWeatherInfoProvider = new WeatherInfoProvider(mHandler);
		setContentView(R.layout.people_server);
		initUI();
		new weatherThread().start();
	}

	private void initUI(){
		mImage = (ImageView)findViewById(R.id.people_img);
		mWeather = (TextView)findViewById(R.id.weather);
		mTemp  = (TextView)findViewById(R.id.temp);
		mPeopleText = (TextView)findViewById(R.id.people_text);
		
		mDialog = new ProgressDialog(PeopleServer.this);
		mDialog.setMessage("获取天气ing，请稍候...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
		
		setTitle("定位ing...");
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SystemConfig.MSG_TEMP_INFO_READY:
				String temp = (String)msg.obj;
				if(temp != null)
					mTemp.setText(temp + "℃");
				break;
			case SystemConfig.MSG_WEATHER_INFO_READY:
				String weather = (String)msg.obj;
				if(weather != null){
					mWeather.setText(weather);
					checkPic(weather);
					mDialog.dismiss();
				}
				break;
			case SystemConfig.WEATHER_UPDATE:
				String city = mPref.getString(SystemConfig.CITY, null);
				if(city != null){
					setTitle(city);
					int index = city.lastIndexOf("市");
					String mRealCity = city.substring(0, index);
					Log.d(TAG, TAG + mRealCity);
					mWeatherInfoProvider.startGetWeatherInfo(mCityToID.get(mRealCity));
				}else{
					setTitle("杭州市");
					mWeatherInfoProvider.startGetWeatherInfo(mCityToID.get("杭州"));
				}
				break;
			}
		}
		
	};
	
	
	private class weatherThread extends Thread{

		@Override
		public void run() {
			while(running){
				mHandler.sendEmptyMessage(SystemConfig.WEATHER_UPDATE);
				try {
					Thread.sleep(10 * 1000);
					Log.d(TAG, TAG + "---->sleep 10s");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	private void checkPic(String weather){
		if(weather.contains("晴")){
			mImage.setBackgroundResource(R.drawable.day_sun);
			String text = checkPeoText("high_temp.txt");
			if(text != null)
				mPeopleText.setText("高温开车注意事项\n" + text);
		}else if(weather.contains("多云")){
			mImage.setBackgroundResource(R.drawable.day_cloudy);
			String text = checkPeoText("high_temp.txt");
			if(text != null)
				mPeopleText.setText("高温开车注意事项\n" + text);
		}else if(weather.contains("阴")){
			mImage.setBackgroundResource(R.drawable.day_yin);
			String text = checkPeoText("high_temp.txt");
			if(text != null)
				mPeopleText.setText("高温开车注意事项\n" + text);
		}else if(weather.contains("小雨")){
			mImage.setBackgroundResource(R.drawable.day_small_rain);
			String text = checkPeoText("heavey_rain.txt");
			if(text != null)
				mPeopleText.setText("雨天开车注意事项\n" + text);
		}else if(weather.contains("阵雨")){
			mImage.setBackgroundResource(R.drawable.day_thunder_shower);
			String text = checkPeoText("heavey_rain.txt");
			if(text != null)
				mPeopleText.setText("雨天开车注意事项\n" + text);
		}else if(weather.contains("中雨")){
			mImage.setBackgroundResource(R.drawable.day_heavy_rain);
			String text = checkPeoText("heavey_rain.txt");
			if(text != null)
				mPeopleText.setText("雨天开车注意事项\n" + text);
		}else if(weather.contains("雪")){
			mImage.setBackgroundResource(R.drawable.day_small_snow);
			String text = checkPeoText("snow.txt");
			if(text != null)
				mPeopleText.setText("雪天开车注意事项\n" + text);
		}else if(weather.contains("雾")){
			mImage.setBackgroundResource(R.drawable.day_fog);
			String text = checkPeoText("fog.txt");
			if(text != null)
				mPeopleText.setText("雾天开车注意事项\n" + text);
		}
		
	}
	
	private String checkPeoText(String txt){
		AssetManager am = this.getAssets();
		StringBuffer buf = new StringBuffer();
		try {
			InputStream in = am.open(txt);				
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tmp = null;
            while ((tmp = reader.readLine()) != null) {
            	buf.append(tmp);
			}   
            in.close();			
		  } catch (IOException e) {
			  Log.e("", "*.txt exception");
			  e.printStackTrace();
		  }	  
		return buf.toString();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, TAG + "----->onDestroy; running: true---> false");
		running = false;
		super.onDestroy();
	}
	
	
	
}
