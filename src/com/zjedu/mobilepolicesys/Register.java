package com.zjedu.mobilepolicesys;

import org.json.JSONException;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author westlakeboy
 *
 */
public class Register extends Activity{

	private EditText mUsername = null;
	private EditText mPassword = null;
	private Button mRegisterButton = null;
	private ProgressDialog mDialog = null;
	private SharedPreferences mPref = null;
	private static final String TAG = "Register";
	private String username = null;
	private String password = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		setContentView(R.layout.register);
		setTitle("注册子系统");
		mDialog = new ProgressDialog(Register.this);
		mDialog.setMessage("注册ing，请稍候...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
	}

	
	
	
	@Override
	protected void onResume() {
		initUI();
		super.onResume();
	}




	private void initUI(){
		mUsername = (EditText)findViewById(R.id.register_username);
		mPassword = (EditText)findViewById(R.id.register_password);
		mRegisterButton = (Button)findViewById(R.id.register);
		mRegisterButton.setOnClickListener(mOnClickListener);
		
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			username = mUsername.getText().toString().trim();
			password = mPassword.getText().toString().trim();
			if(username.equals("") || username == null){
				MobileSysDialog.show(Register.this, "提示", "请输入账号");
				return;
			}
			if(password.equals("") || password == null){
				MobileSysDialog.show(Register.this, "提示", "请输入密码");
				return;
			}
			
			mDialog.show();
			doRegister(username, password);
			 
		}
	};
	
	
	private void doRegister(String username,String password){
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.USERNAME, username);
		params.addBodyParameter(Params.PASSWORD, password);
		HttpUtils http = new HttpUtils(100 * 1000);
		Log.d(TAG, TAG + "url--->" + SystemConfig.URL_REGISTER);
		http.send(HttpMethod.POST, SystemConfig.URL_REGISTER, params ,new registerTask());
	}
	
	
	private class registerTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.e(TAG, TAG + "----" + msg);
			mDialog.dismiss();
			MobileSysDialog.show(Register.this, "错误提示", "网络连接失败...");
		}

		@Override
		public void onSuccess(ResponseInfo<String> ResponseInfo) {
			Log.d(TAG, TAG + "---" + ResponseInfo.result);
			try {
				JSONObject Obj = new JSONObject(ResponseInfo.result);
				String type = Obj.optString(Params.TYPE);
				if(type.equalsIgnoreCase(Params.SUCCESS)){
					mDialog.dismiss();
					MobileSysToast.toast(Register.this, "恭喜亲! 注册成功..");
					mPref.edit().putString(SystemConfig.USERNAME, username).commit();
					mPref.edit().putString(SystemConfig.PASSWORD, password).commit();
					Intent intent = new Intent(Register.this,Mobile.class);
					startActivity(intent);
					finish();
				}else if(type.equalsIgnoreCase(Params.FAIL)){
					mDialog.dismiss();
					MobileSysDialog.show(Register.this, "提示", "注册失败...");
				}
			} catch (JSONException e) {
				Log.e(TAG, TAG + "----" + e);
				mDialog.dismiss();
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
			Intent intent = new Intent(Register.this,Login.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
