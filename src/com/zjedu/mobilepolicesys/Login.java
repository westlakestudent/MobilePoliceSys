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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author westlakeboy
 *
 */
public class Login extends Activity{

	private EditText mUsername = null;
	private EditText mPassword = null;
	private CheckBox mRemName = null;
	private CheckBox mRemPass = null;
	private Button mLogin_btn = null;
	private TextView mRegister_btn = null;
	private SharedPreferences mPref = null;
	private ProgressDialog mDialog = null;
	private static final String TAG = "Login";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		mDialog = new ProgressDialog(Login.this);
		mDialog.setMessage("登录ing，请稍候...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		setTitle("用户" + "/未登入");
	}
	
	@Override
	protected void onResume() {
		initUI();
		super.onResume();
	}



	private void initUI(){
		mUsername = (EditText)findViewById(R.id.username);
		mPassword = (EditText)findViewById(R.id.password);
		mRemName = (CheckBox)findViewById(R.id.remname);
		mRemPass = (CheckBox)findViewById(R.id.rempass);
		mRegister_btn = (TextView)findViewById(R.id.register);
		mRegister_btn.setOnClickListener(mRegisterListener);
		mLogin_btn = (Button)findViewById(R.id.login);
		mLogin_btn.setOnClickListener(mClickListener);
		
		String username = mPref.getString(SystemConfig.USERNAME, null);
		String password = mPref.getString(SystemConfig.PASSWORD, null);
		Log.d(TAG, TAG + "--username--" + username + "--password---" + password); 
		if(username != null){
			mUsername.setText(username);
			mRemName.setChecked(true);
		}
		
		if(password != null){
			mPassword.setText(password);
			mRemPass.setChecked(true);
		}
	}
	
	private OnClickListener mRegisterListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Login.this,Register.class);
			startActivity(intent);
			finish();
		}
	};
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mUsername.getText().toString().trim() == null || mUsername.getText().toString().trim().equals("")){
				new AlertDialog.Builder(Login.this).setTitle("提示").setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("请输入帐号").setPositiveButton("确定", null).show();
				return;
			}
			
			if(mPassword.getText().toString().trim() == null || mPassword.getText().toString().trim().equals("")){
				new AlertDialog.Builder(Login.this).setTitle("提示").setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("请输入密码").setPositiveButton("确定", null).show();
				return;
			}
			
			if(mRemName.isChecked()){
				mPref.edit().putString(SystemConfig.USERNAME, mUsername.getText().toString().trim()).commit();
			}else{
				mPref.edit().putString(SystemConfig.USERNAME, null).commit();
			}
			if(mRemPass.isChecked()){
				mPref.edit().putString(SystemConfig.PASSWORD, mPassword.getText().toString().trim()).commit();
			}else{
				mPref.edit().putString(SystemConfig.PASSWORD, null).commit();
			}
			
			mDialog.show();
			dologin(mUsername.getText().toString().trim(), mPassword.getText().toString().trim());
		}
	};
	
	
	private void dologin(String username,String password){
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.USERNAME, username);
		params.addBodyParameter(Params.PASSWORD, password);
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.POST, SystemConfig.URL_LOGIN, params ,new loginTask());
		Log.d(TAG, TAG + "url--->" + SystemConfig.URL_LOGIN);
	}
	private class loginTask extends RequestCallBack<String>{

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.e(TAG, TAG + "----" + msg);
			mDialog.dismiss();
			MobileSysDialog.show(Login.this, "错误提示", "网络连接失败...");
		}

		@Override
		public void onSuccess(ResponseInfo<String> ResponseInfo) {
			Log.d(TAG, TAG + "---" + ResponseInfo.result);
			try {
				JSONObject Obj = new JSONObject(ResponseInfo.result);
				String type = Obj.optString(Params.TYPE);
				if(type.equalsIgnoreCase(Params.SUCCESS)){
					mDialog.dismiss();
					Intent intent = new Intent(Login.this,Mobile.class);
					startActivity(intent);
					finish();
				}else if(type.equalsIgnoreCase(Params.FAIL)){
					mDialog.dismiss();
					MobileSysDialog.show(Login.this, "提示", "账号或密码错误...");
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
			new AlertDialog.Builder(Login.this)
					.setMessage("确认退出吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Login.this.finish();
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
	
	
	
}
