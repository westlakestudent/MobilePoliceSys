package com.zjedu.mobilepolicesys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ReleaseEvent extends Activity{

	private ProgressDialog mDialog = null;
	private Button mCameraBtn = null;
	private Button mReleaseBtn = null;
	private EditText mTitleEdit = null;
	private EditText mContentEdit = null;
	private EditText mFilePathEdit = null;
	private static final int CAMERA_RESULT_CODE = 1;
	private static final int PICTURE_RESULT_CODE = 2;
	private static final String TAG = "ReleaseEvent";
	private SharedPreferences mPref = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.CHINA);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		setContentView(R.layout.releasevent);
		initUI();
	}

	private void initUI(){
		mCameraBtn = (Button)findViewById(R.id.camera);
		mReleaseBtn = (Button)findViewById(R.id.event_release_btn);
		mTitleEdit = (EditText)findViewById(R.id.event_release_title);
		mContentEdit = (EditText)findViewById(R.id.event_release_content);
		mFilePathEdit = (EditText)findViewById(R.id.event_release_filepath);
		
		mCameraBtn.setOnClickListener(mOnClickListener);
		mReleaseBtn.setOnClickListener(mOnClickListener);
		mFilePathEdit.setOnClickListener(mOnClickListener);
		
		mDialog = new ProgressDialog(ReleaseEvent.this);
		mDialog.setMessage("发布ing，请稍候...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String state = Environment.getExternalStorageState();
			switch (v.getId()) {
			case R.id.camera:
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
					startActivityForResult(intent, CAMERA_RESULT_CODE);
					}
				break;
			case R.id.event_release_btn:
				String path = mFilePathEdit.getText().toString().trim();
				doRelease(path);
				break;
			case R.id.event_release_filepath:
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, PICTURE_RESULT_CODE);
					}
				break;
			}
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CAMERA_RESULT_CODE){
			if(resultCode == RESULT_OK){
				String name = sdf.format(new Date()) + ".jpg"; 
				Bundle bundle = data.getExtras();  
	            Bitmap bitmap = (Bitmap) bundle.get("data");
	            FileOutputStream b = null;  
                File file = new File("/mnt/sdcard/mobilepolicesys/upload/");  
                if(!file.exists())
                	file.mkdirs();
	            String fileName = "/mnt/sdcard/mobilepolicesys/upload/"+name;  
	            mFilePathEdit.setText(fileName);
	             try {  
	                 b = new FileOutputStream(fileName);  
	                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
	             } catch (FileNotFoundException e) {  
	                 e.printStackTrace();  
	             } finally {  
	                 try {  
	                     b.flush();  
	                     b.close();  
	                 } catch (IOException e) {  
	                     e.printStackTrace();  
	                 }  
	             }  
	            Log.d(TAG, "bitmap--" + bitmap);
			}
		}else if(requestCode == PICTURE_RESULT_CODE){
			if(resultCode == RESULT_OK){
				Uri uri = data.getData();  
				String path = uri.getPath();
	            Log.e(TAG, "uri" + uri.toString() + "path--" + path);
	            mFilePathEdit.setText(path);
			}
		}
			
	}
	
	private void doRelease(String path){
		long time = new Date().getTime();
		String username = mPref.getString(SystemConfig.USERNAME, "michal");
		String addr = mPref.getString(SystemConfig.ADDRESS, "杭州市西湖区留和路228号");
		String title = mTitleEdit.getText().toString().trim();
		String content = mContentEdit.getText().toString().trim();
		RequestParams params = new RequestParams();
		params.addBodyParameter(Params.FILE, new File(path));
		params.addBodyParameter(Params.USERNAME, username);
		params.addBodyParameter(Params.STATUS, "N");
		params.addBodyParameter(Params.TITLE, title);
		params.addBodyParameter(Params.CONTENT, content);
		params.addBodyParameter(Params.ADDRESS, addr);
		params.addBodyParameter(Params.TIME, String.valueOf(time));
		
		mDialog.show();
		HttpUtils http = new HttpUtils(100 * 1000);
		http.send(HttpMethod.POST, SystemConfig.URL_EVENT_RELEASE, params, new releaseTask());
	}
	
	
	private class releaseTask extends RequestCallBack<String>{

		
		
		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			super.onLoading(total, current, isUploading);
			
		}

		@Override
		public void onFailure(HttpException e, String msg) {
			Log.d(TAG, e + "----" + msg);
			mDialog.dismiss();
			MobileSysDialog.show(ReleaseEvent.this, "错误", "服务器连接失败");
		}

		@Override
		public void onSuccess(ResponseInfo<String> info) {
			Log.d(TAG, info.result);
			mDialog.dismiss();
			try {
				JSONObject obj = new JSONObject(info.result);
				String type = obj.optString(Params.TYPE);
				if(type.equalsIgnoreCase(Params.SUCCESS)){
					MobileSysDialog.showandfinish(ReleaseEvent.this, "提示", "发布成功");
					//finish();
				}else{
					MobileSysDialog.show(ReleaseEvent.this, "错误", "发布失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				mDialog.dismiss();
				MobileSysDialog.show(ReleaseEvent.this, "错误", "发布失败");
			}
			
		}
		
	}
	
}
