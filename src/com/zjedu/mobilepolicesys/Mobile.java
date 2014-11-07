package com.zjedu.mobilepolicesys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.zjedu.mobilepolicesys.R;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zjedu.dao.EventDao;
import com.zjedu.entity.Event;
import com.zjedu.entity.Params;
import com.zjedu.service.HelpService;
import com.zjedu.widget.MobileSysDialog;
import com.zjedu.widget.MobileSysTextView;
import com.zjedu.widget.MobileSysToast;

/**
 * 
 * @author westlakeboy
 *
 */
public class Mobile extends Activity {

	/**
	 * 
	 */
	//private String path = "http://192.168.137.1:8080/MobilePoiceSysServer";
	private String photourl = "/mnt/sdcard/mobilepolicesys";
	private static final String TAG = "Mobile";
	private SharedPreferences mPref = null;
	private int [] res = {R.drawable.img_showpic,R.drawable.img_showpic_1};
	private ImageView mImageView = null;
	private LocationClient mLocationClient = null;
	private String username = null;
	private TextView mNews1 = null;
	private TextView mNews2 = null;
	private TextView mNews3 = null;
	private int mNews1_id = 0;
	private int mNews2_id = 0;
	private int mNews3_id = 0;
	private int iImage = SystemConfig.FIRST_IMG;
	private List<Event> events = new ArrayList<Event>();
	private Timer mImageTimer = null;
	private Timer mUpdateTimer = null;
	
	private Button mSearchButton = null;
	private Button mEventButton = null;
	private Button mPeopleButton = null;
	private Button mLocationButton = null;
	private Button mHelpButton = null;
	private Button mSocialButton = null;
	private Button mMoreButton = null;
	private Button mWeiXinButton = null;
	private Button mAboutButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		setContentView(R.layout.main);
		
		mImageTimer = new Timer();
		mUpdateTimer = new Timer();
		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);

		username = mPref.getString(SystemConfig.USERNAME, null);
		StringBuffer buf = new StringBuffer();
		if(username != null)
			buf.append(username + " / 正在定位...");
		setTitle(buf.toString());
		if(mImageTimer != null){
			mImageTimer.schedule(mImageTask, 1000, 30 * 1000);
		}
		if(mUpdateTimer != null){
			mUpdateTimer.schedule(mUpdateEventTask, 1000, 60 * 1000);
		}
		
		pullEvents();
		HelpService.actionStart(Mobile.this);
	}

	private void initUI(){
		mImageView = (ImageView)findViewById(R.id.img);
		mNews1 = (MobileSysTextView)findViewById(R.id.news1);
		mNews2 = (MobileSysTextView)findViewById(R.id.news2);
		mNews3 = (MobileSysTextView)findViewById(R.id.news3);
		
		mNews1.setOnClickListener(mClickListener);
		mNews2.setOnClickListener(mClickListener);
		mNews3.setOnClickListener(mClickListener);
		
		
		mPeopleButton = (Button)findViewById(R.id.btn3);
		mSearchButton = (Button)findViewById(R.id.btn1);
		mEventButton = (Button)findViewById(R.id.btn2);
		mSocialButton = (Button)findViewById(R.id.btn4);
		mLocationButton = (Button)findViewById(R.id.btn5);
		mHelpButton = (Button)findViewById(R.id.btn6);
		mMoreButton = (Button)findViewById(R.id.btn7);
		mWeiXinButton = (Button)findViewById(R.id.btn8);
		mAboutButton = (Button)findViewById(R.id.btn9);
		
		
		mMoreButton.setOnClickListener(mButtonClickListener);
		mWeiXinButton.setOnClickListener(mButtonClickListener);
		mAboutButton.setOnClickListener(mButtonClickListener);
		mSocialButton.setOnClickListener(mButtonClickListener);
		mHelpButton.setOnClickListener(mButtonClickListener);
		mPeopleButton.setOnClickListener(mButtonClickListener);
		mSearchButton.setOnClickListener(mButtonClickListener);
		mEventButton.setOnClickListener(mButtonClickListener);
		mLocationButton.setOnClickListener(mButtonClickListener);
	}
	
	
	private OnClickListener mButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn1:
				intent.setClass(Mobile.this,SearchInfo.class);
				break;
			case R.id.btn2:
				intent.setClass(Mobile.this,MeEvent.class);		
				break;
			case R.id.btn3:
				intent.setClass(Mobile.this,PeopleServer.class);
				break;
			case R.id.btn4:
				intent.setClass(Mobile.this,Social.class);
				break;
			case R.id.btn5:
				intent.setClass(Mobile.this,Location.class);
				break;
			case R.id.btn6:
				intent.setClass(Mobile.this,Help.class);
				break;
			case R.id.btn7:
				intent.setClass(Mobile.this,More.class);
				startActivity(intent);
				finish();
				break;
			case R.id.btn8:
				intent.setClass(Mobile.this,WeiXin.class);
				break;
			case R.id.btn9:
				intent.setClass(Mobile.this,About.class);
				break;
			}
			if(v.getId() != R.id.btn7)
				startActivity(intent);
		}
	};
	
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Mobile.this, NewsDetail.class);
			Bundle bundle = new Bundle();
			switch (v.getId()) {
			case R.id.news1:
				bundle.putInt(SystemConfig.EVENTID, mNews1_id);
				break;
			case R.id.news2:
				bundle.putInt(SystemConfig.EVENTID, mNews2_id);
				break;
			case R.id.news3:
				bundle.putInt(SystemConfig.EVENTID, mNews3_id);
				break;
			}
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};
	
	
	private BDLocationListener mLocationListener = new BDLocationListener(){

		
		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d(TAG, TAG + "---> onReceiveLocation" );
			if (location == null)
				return ;
			Log.d(TAG, TAG + "addr---" + location.getAddrStr() + "city--" + location.getCity());
			String addr = location.getAddrStr();
			String city = location.getCity();
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			mPref.edit().putInt(SystemConfig.LAT, (int)(lat * 1e6)).commit();
			mPref.edit().putInt(SystemConfig.LON, (int)(lon * 1e6)).commit();
			mPref.edit().putString(SystemConfig.CITY, city).commit();
			mPref.edit().putString(SystemConfig.ADDRESS, addr).commit();
			if(addr != null)
				Mobile.this.setTitle(username + " / " + addr);
		}

		@Override
		public void onReceivePoi(BDLocation location) {
		}
		
	};
	
	@Override
	protected void onResume() {
		initUI();
		if(mLocationClient != null){
			mLocationClient.start();
			if(mLocationClient.isStarted())
				mLocationClient.requestLocation();
		}
		Log.d(TAG, TAG + "--->onresume");
		super.onResume();
	}
	
	
	

	@Override
	protected void onDestroy() {
		if(mLocationClient != null){
			mLocationClient.stop();
			mLocationClient = null;
		}
		if(mImageTimer != null){
			mImageTask.cancel();
			mImageTimer.cancel();
		}
		if(mUpdateTimer != null){
			mUpdateEventTask.cancel();
			mUpdateTimer.cancel();
		}
		HelpService.actionStop(Mobile.this);
		Log.d(TAG, TAG + "--->onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if(mLocationClient != null){
			mLocationClient.stop();
		}
		Log.d(TAG, TAG + "--->onPause");
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
			new AlertDialog.Builder(Mobile.this)
					.setMessage("确认退出吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Mobile.this.finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SystemConfig.NEWS:
				setNews();
				updateReveice(username, events);	
				break;
			case SystemConfig.POI:
				if(mLocationClient != null){
					if(mLocationClient.isStarted())
						mLocationClient.requestLocation();
				}
				break;
			case SystemConfig.UPDATE:
				pullEvents();
				break;
			case SystemConfig.ALREADY_NEW:
				EventDao dao = new EventDao(Mobile.this);
				events = dao.findEvents();
				setNews();
				break;
			default:
				mImageView.setBackgroundResource(res[msg.what]);
				break;
			}
		}
			
				
		
	};
	
	
	private void setNews(){
		if(events == null || events.isEmpty())
			return;
		mNews1.setText(events.get(0).getTitle());
		mNews1_id = events.get(0).getId();
		if(events.size() > 1){
			mNews2.setText(events.get(1).getTitle());
			mNews2_id = events.get(1).getId();
		}
		if(events.size() > 2){
			mNews3.setText(events.get(2).getTitle());
			mNews3_id = events.get(2).getId();
		}
	}
	/**
	 * 
	 *定时获取最新事件线程
	 */
	
	private TimerTask mUpdateEventTask = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(SystemConfig.UPDATE);
			}
		};
		
	private TimerTask mImageTask = new TimerTask() {
		
		@Override
		public void run() {
			
			if(iImage == SystemConfig.FIRST_IMG){
				mHandler.sendEmptyMessage(iImage);
				iImage = SystemConfig.SECOND_IMG;
			}else if(iImage == SystemConfig.SECOND_IMG){
				mHandler.sendEmptyMessage(iImage);
				iImage = SystemConfig.FIRST_IMG;
			}
			mHandler.sendEmptyMessage(SystemConfig.POI);
		}
	};
	
	
	
	
	private void pullEvents(){
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.STATUS, "N");
		params.addBodyParameter(Params.USERNAME, username);
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.POST, SystemConfig.URL_GET_EVENT, params ,new eventTask());
	}
	
	
	private class eventTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.e(TAG, TAG + msg);
			MobileSysDialog.show(Mobile.this, "错误提示", "服务器连接失败...");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, TAG + "===info" + info.result);
			try {
				if(!events.isEmpty()){
					events.clear();
				}
				Gson gson = new Gson();
				JSONObject Obj = new JSONObject(info.result);
				String type = Obj.optString(Params.TYPE);
				if(type.equalsIgnoreCase(Params.SUCCESS)){
					JSONArray arr = Obj.getJSONArray(Params.VALUE);
					for(int i = 0;i < arr.length();i++){
						JSONObject jsonobj = arr.getJSONObject(i);
						Event event = gson.fromJson(jsonobj.toString(), Event.class);
						events.add(event);
					}
					if(!events.isEmpty()){
						saveEvents(events);
						mHandler.sendEmptyMessage(SystemConfig.NEWS);
					}
				}else if(type.equalsIgnoreCase(Params.FAIL)){
					String msg = Obj.optString(Params.VALUE);
					MobileSysToast.toast(Mobile.this,msg);
					mHandler.sendEmptyMessage(SystemConfig.ALREADY_NEW);
				}
			} catch (JSONException e) {
				Log.e(TAG, TAG + "----" + e);
				e.printStackTrace();
			}
		}
		
	}
	
	
	private void saveEvents(List<Event> events){
		EventDao dao = new EventDao(Mobile.this);
		if(events == null || events.isEmpty()){
			Log.d(TAG, TAG + "events is null");
			return;
		}
		for(Event event : events){
			String tmp = event.getPath();
			int index = tmp.lastIndexOf("/");
			photourl = photourl + "/upload" + tmp.substring(index, tmp.length());
			String url = tmp;
			event.setPath(photourl);
			dao.insertEvent(event);
			HttpUtils http = new HttpUtils(1000 * 100);
			http.send(HttpMethod.GET,url , new imageTask());
		}
			
		//mPref.edit().putBoolean(SystemConfig.EVENT_PULLED, true).commit();
	}
	
	private class imageTask extends RequestCallBack<File>{

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			Log.d(TAG, "iamge--" + arg0.getMessage() + "arg1----" + arg1);
		}

		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			Log.d(TAG, "iamge--" + arg0.result);
			try {
		           InputStream in = null ;
		           OutputStream out = null ;
		            try {       
		            	File f = new File("/mnt/sdcard/mobilepolicesys/upload/");
		            	if(!f.exists()){
		            		f.mkdirs();
		            	}
		                in = new BufferedInputStream( new FileInputStream(arg0.result), 1024);
		                out = new BufferedOutputStream( new FileOutputStream(photourl), 1024);
		                byte [] buffer = new byte [1024];
		                while (in.read(buffer) > 0 ) {
		                   out.write(buffer);
		               } 
		           } finally {
		                if ( null != in) {
		                   in.close();
		               } 
		                if ( null != out) {
		                   out.close();
		               } 
		           } 
		       } catch (Exception e) {
		           e.printStackTrace();
		       } 
		}
		
	}
	
	
	private void updateReveice(String username,List<Event> events){
		StringBuffer buf = new StringBuffer();
		for(Event event : events){
			buf.append(event.getId() + ",");
		}
		HttpUtils http = new HttpUtils(100 * 1000);
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.EVENTIDS, buf.toString());
		params.addBodyParameter(Params.USERNAME, username);
		http.send(HttpMethod.POST, SystemConfig.URL_UPDATE_REV, params,new updateTask());
		
	}
	
	
	private class updateTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			MobileSysDialog.show(Mobile.this, "错误提示", "服务器连接失败...");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, TAG + "===info" + info.result);
		}
		
	}

	
}
