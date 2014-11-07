package com.zjedu.service;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zjedu.mobilepolicesys.R;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zjedu.dao.HelpDao;
import com.zjedu.entity.Help;
import com.zjedu.entity.Params;
import com.zjedu.mobilepolicesys.SystemConfig;

public class HelpService extends Service{

	
	private static final String KEEP_ALIVE_INTERVAL = "30";
	private static final String TAG = "HelpService";
	private static final String ACTION_START = "com.cd.help.START";
	private static final String ACTION_STOP = "com.cd.help.STOP";
	private static final String ACTION_KEEPALIVE = "com.cd.help.KEEP_ALIVE";
	
	private HelpDao dao = new HelpDao(this);
	private ConnectivityManager mConnMan = null;
	private boolean mStarted = false;
	private boolean previous_pull = false;
	private SharedPreferences mPrefs = null;
	private SharedPreferences mPref = null;
	private static final String PREF_STARTED = "isStarted";
	
	private Object lock=new Object();
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		mPref.edit().putInt(SystemConfig.NOTIFICATIONID, 0).commit();
		mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		handleCrashedService();
		super.onCreate();
	}
	
	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, HelpService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		long hearttime = Long.valueOf(KEEP_ALIVE_INTERVAL) * 1000;
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + hearttime, hearttime, pi);
	}

	private void stopKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, HelpService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private boolean wasStarted() {
		return mPrefs.getBoolean(PREF_STARTED, false);
	}

	private void setStarted(boolean started) {
		mPrefs.edit().putBoolean(PREF_STARTED, started).commit();
		mStarted = started;
	}
	
	
	public static void actionStart(Context ctx) {
		Intent i = new Intent(ctx, HelpService.class);
		i.setAction(ACTION_START);
		ctx.startService(i);
	}

	public static void actionStop(Context ctx) {
		Intent i = new Intent(ctx, HelpService.class);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}

	private void handleCrashedService() {
		if (wasStarted() == true) {
			stopKeepAlives();
			start();
		}
	}

	private synchronized void start() {
		if (mStarted == true) {
			Log.w(TAG, "Attempt to start connection that is already active");
			return;
		}
		setStarted(true);
		registerReceiver(mConnectivityChanged, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		// 启动定时拉取消息的任务
		startKeepAlives();
	}
	
	private synchronized void stop() {
		if (mStarted == false) {
			Log.w(TAG, "Attempt to stop connection not active.");
			return;
		}
		setStarted(false);
		unregisterReceiver(mConnectivityChanged);
		stopKeepAlives();
	}
	
	private synchronized void repullmsgIfNecessary() {
		if (!previous_pull && isNetworkAvailable()) {
			pullHelp();
		}
	}
	
	private void heartPullhelp(){
		synchronized (lock) {
			if (isNetworkAvailable()) {
				pullHelp();
			} else {
				previous_pull = false;
			}
		}
	}
	
	private void pullHelp() {
		Log.i(TAG, "pullHelp");
		String username = mPref.getString(SystemConfig.USERNAME, null);
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.USERNAME, username);
		HttpUtils http = new HttpUtils(1000 * 100);
		http.send(HttpMethod.POST, SystemConfig.URL_HELPMSG, params, new pullHelpTask());
	}
	
	private class pullHelpTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.e(TAG, msg + e);
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, info.result);
			try {
				Gson gson = new Gson();
				JSONObject newarray = null;
				JSONObject obj = new JSONObject(info.result);
				String type = obj.optString(Params.TYPE);
				if(type.equalsIgnoreCase(Params.SUCCESS)){
					JSONArray arr = obj.optJSONArray(Params.VALUE);
					newarray = dao.check(arr);
					Log.d(TAG, "newarray:" + newarray);
					if(newarray != null){
						JSONArray newarrs = newarray.optJSONArray("newarray");
						if(newarrs.length() > 0){
							StringBuffer buf = new StringBuffer();
							for(int i =0;i<newarrs.length();i++){
								Help help = gson.fromJson(newarrs.getJSONObject(i).toString(), Help.class);
								buf.append(help.getId() + ",");
								dao.insertHelp(help);
								showNotification(help);
							}
							updatereceive(buf.toString());
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void updatereceive(String helpids){
		Log.i(TAG, "updatereceive");
		String username = mPref.getString(SystemConfig.USERNAME, null);
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.USERNAME, username);
		params.addBodyParameter(Params.HELPIDS, helpids);
		HttpUtils http = new HttpUtils(1000 * 100);
		http.send(HttpMethod.POST, SystemConfig.URL_HELPRECV, params, new updateTask());
	}
	
	
	
	private class updateTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.e(TAG, msg + e);
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, info.result);
		}
		
	}
	
	private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			boolean hasConnectivity = (info != null && info.isConnected()) ? true
					: false;
			if (hasConnectivity)
				repullmsgIfNecessary();
		}
	};
	
	
	private boolean isNetworkAvailable() {
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null)
			return false;
		return info.isConnected();
	}
	
	@Override
	public void onDestroy() {
		if (mStarted == true)
			stop();
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String actions = intent.getAction();
		if (ACTION_STOP.equals(actions) == true) {
			stop();
			stopSelf();
		} else if (ACTION_START.equals(actions) == true){
			start();
		}
		else if (ACTION_KEEPALIVE.equals(actions) == true){
			heartPullhelp();
		}
		else {
			return START_NOT_STICKY;
		}
		return START_STICKY;
	}

	
	@SuppressWarnings("deprecation")
	private void showNotification(Help help){
		int id = mPref.getInt(SystemConfig.NOTIFICATIONID, -1);
		NotificationManager notificationManager = (NotificationManager)    
	            this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;  
		CharSequence tickerText = "请求支援";  
		long when = System.currentTimeMillis();  
		Notification notification = new Notification(icon, tickerText, when);  
	    CharSequence contentTitle ="请求支援"; 
        CharSequence contentText =help.getContent();   
        Intent notificationIntent =new Intent(HelpService.this, com.zjedu.mobilepolicesys.Help.class);   
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent contentIntent = PendingIntent.getActivity(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
	    notification.setLatestEventInfo(HelpService.this, contentTitle, contentText, contentIntent);
	    notification.defaults = Notification.DEFAULT_SOUND;
	    notificationManager.notify(id, notification);
	    Log.d(TAG, " ---->id" + id);
	    mPref.edit().putInt(SystemConfig.NOTIFICATIONID, ++id).commit();
	}
	
	
}
