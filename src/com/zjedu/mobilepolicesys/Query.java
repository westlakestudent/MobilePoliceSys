package com.zjedu.mobilepolicesys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zjedu.mobilepolicesys.R;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zjedu.entity.Params;
import com.zjedu.widget.MobileSysDialog;

public class Query extends Activity{

	private Button mButton = null;
	private ListView mListView = null;
	private ProgressDialog mDialog = null;
	private static final String TAG = "Query";
	
	private Adapter adapter = null;
	private List<com.zjedu.entity.Message> messages = new ArrayList<com.zjedu.entity.Message>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query);
		initUI();
		doQuerymsg();
	}

	private void initUI(){
		mButton = (Button)findViewById(R.id.msg_refresh);
		mButton.setOnClickListener(mOnClickListener);
		mListView = (ListView)findViewById(R.id.msg_list);
		
		mDialog = new ProgressDialog(Query.this);
		mDialog.setMessage("查询消息ing，请稍候...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}
	
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			doQuerymsg();
			mDialog.show();
		}
	};
	
	
	private void doQuerymsg(){
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.GET, SystemConfig.URL_GETMSG, new queryTask());
	}
	
	
	private class queryTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.d(TAG, TAG + msg);
			mDialog.dismiss();
			MobileSysDialog.show(Query.this, "错误提示", "网络连接失败");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, TAG + info.result);
			mDialog.dismiss();
			try {
				Gson gson = new Gson();
				JSONObject obj = new JSONObject(info.result);
				String type = obj.optString(Params.TYPE);
				if(type.equalsIgnoreCase(Params.SUCCESS)){
					JSONArray arr = obj.getJSONArray(Params.VALUE);
					if(arr.length() > 0){
						messages.clear();
						for(int  i = 0;i < arr.length();i++){
							com.zjedu.entity.Message msg = gson.fromJson(arr.getJSONObject(i).toString(), com.zjedu.entity.Message.class);
							messages.add(msg);
						}
						mHandler.sendEmptyMessage(SystemConfig.MSG_ARRIVE);
					}
				}else{
					String value = obj.optString(Params.VALUE);
					MobileSysDialog.show(Query.this, "提示", value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				MobileSysDialog.show(Query.this, "错误提示", e.getMessage());
			}
		}
		
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == SystemConfig.MSG_ARRIVE){
				adapter = new Adapter(messages);
				mListView.setAdapter(adapter);
			}
			super.handleMessage(msg);
		}
		
	};
	
	
	private class Adapter extends BaseAdapter{

		private List<com.zjedu.entity.Message> messages = null;
		public Adapter(List<com.zjedu.entity.Message> messages){
			this.messages = messages;
		}
		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int location) {
			return messages.get(location);
		}

		@Override
		public long getItemId(int location) {
			return location;
		}

		@Override
		public View getView(int location, View v, ViewGroup arg2) {
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			View layout = LayoutInflater.from(Query.this).inflate(R.layout.msg_item, null);
			TextView nametext = (TextView)layout.findViewById(R.id.msg_name);
			TextView datetext = (TextView)layout.findViewById(R.id.msg_date);
			TextView contentext = (TextView)layout.findViewById(R.id.msg_content);
			
			String name = messages.get(location).getUsername();
			String date = messages.get(location).getSendtime();
			String content = messages.get(location).getMsg_content();
			Date d = new Date(Long.valueOf(date));
			String time = sfd.format(d);
			nametext.setText(name);
			datetext.setText(time);
			contentext.setText(content);
			
			return layout;
		}
		
	}
}
