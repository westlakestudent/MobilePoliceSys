package com.zjedu.mobilepolicesys;

import java.util.Date;

import org.json.JSONObject;

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

public class ForHelp extends Activity{

	private static final String TAG = "ForHelp";
	private EditText mEditText = null;
	private Button mButton = null;
	private SharedPreferences mPref = null;
	private ProgressDialog mDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forhelp);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		initUI();
	}

	private void initUI(){
		mEditText = (EditText)findViewById(R.id.help_msg);
		mButton = (Button)findViewById(R.id.help_send);
		mButton.setOnClickListener(mOnClickListener);
		
		mDialog = new ProgressDialog(ForHelp.this);
		mDialog.setMessage("请求支援ing，请稍候...");
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
				doforHelp(content);
				mDialog.show();
			}
		};
	
	private void doforHelp(String content){
		long time = new Date().getTime();
		String addr = mPref.getString(SystemConfig.ADDRESS, null);
		String username = mPref.getString(SystemConfig.USERNAME, null);
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.HELP_ADDR, addr);
		params.addBodyParameter(Params.USERNAME, username);
		params.addBodyParameter(Params.HELP_CONTENT, content);
		params.addBodyParameter(Params.HELP_TIME, String.valueOf(time));
		
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.POST, SystemConfig.URL_FORHELP, params, new forhelpTask());
	}
	
	
	private class forhelpTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			mDialog.dismiss();
			Log.e(TAG, TAG + msg);
			MobileSysDialog.show(ForHelp.this, "错误提示", "服务器连接失败...");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			mDialog.dismiss();
			Log.d(TAG, TAG + "===info" + info.result);
			try{
				JSONObject obj = new JSONObject(info.result);
				String msg = obj.optString(Params.VALUE);
				MobileSysToast.toast(ForHelp.this, msg);
			}catch(Exception e){
				MobileSysToast.toast(ForHelp.this, e.getMessage());
			}
		}
		
	}
}
