package com.zjedu.mobilepolicesys;

import java.util.Date;

import org.json.JSONObject;

import com.zjedu.mobilepolicesys.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zjedu.entity.Params;
import com.zjedu.widget.MobileSysDialog;
import com.zjedu.widget.MobileSysToast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Release extends Activity{

	private static final String TAG = "Release";
	private EditText mEditText = null;
	private Button mButton = null;
	private SharedPreferences mPref = null;
	private ProgressDialog mDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		setContentView(R.layout.release);
		initUI();
	}

	
	private void initUI(){
		mEditText = (EditText)findViewById(R.id.msg);
		mButton = (Button)findViewById(R.id.send);
		mButton.setOnClickListener(mOnClickListener);
		
		mDialog = new ProgressDialog(Release.this);
		mDialog.setMessage("发布消息ing，请稍候...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String content = mEditText.getText().toString().trim();
			if(content == null || content.equals(""))
				return;
			doRelease(content);
			mDialog.show();
		}
	};
	
	private void doRelease(String content){
		long time = new Date().getTime();
		String city = mPref.getString(SystemConfig.CITY, null);
		String username = mPref.getString(SystemConfig.USERNAME, null);
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.MSG_CITY, city);
		params.addBodyParameter(Params.USERNAME, username);
		params.addBodyParameter(Params.MSG_CONTENT, content);
		params.addBodyParameter(Params.MSG_TIME, String.valueOf(time));
		
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.POST, SystemConfig.URL_RELEASE, params,new ReleaseTask());
		
	}
	
	
	private class ReleaseTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.e(TAG, TAG + msg);
			mDialog.dismiss();
			MobileSysDialog.show(Release.this, "错误提示", "服务器连接失败...");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			mDialog.dismiss();
			Log.d(TAG, TAG + "===info" + info.result);
			try{
				JSONObject obj = new JSONObject(info.result);
				String msg = obj.optString(Params.VALUE);
				MobileSysToast.toast(Release.this, msg);
			}catch(Exception e){
				MobileSysToast.toast(Release.this, e.getMessage());
			}
			
			
			
		}
		
	}
}
