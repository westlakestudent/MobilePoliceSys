package com.zjedu.mobilepolicesys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zjedu.mobilepolicesys.R;
import com.zjedu.dao.EventDao;
import com.zjedu.widget.MobileSysToast;

public class More extends Activity{

	private static final String TAG ="More";
	private SharedPreferences mPref = null;
	private ListView mListView = null;
	private String[] names = {"支援通知","清除事件","退出帐号"};
	private String[] choises = { "接收", "不接收" };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		setContentView(R.layout.more);
		initUI();
	}
	
	private void initUI(){
		mListView = (ListView)findViewById(R.id.more_list);
		Adapter adapter = new Adapter();
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}
	
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, final View v, int location,
				long arg3) {
			Log.d(TAG, "--->" + names[location]);
			switch (location) {
			case 0:
				new AlertDialog.Builder(More.this).setTitle("是否接收").setIcon(android.R.drawable.ic_dialog_info).setSingleChoiceItems(
						choises, mPref.getInt(SystemConfig.WHICH, 0),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mPref.edit().putInt(SystemConfig.WHICH, which).commit();
							mPref.edit().putBoolean(SystemConfig.ACCEPTED, true).commit();
							TextView t = (TextView)v.findViewById(R.id.more_item);
							t.setText(names[0] + " :" + choises[which]);
							}
						}).setNegativeButton("取消", null).show();

				break;
			case 1:
				EventDao dao = new EventDao(More.this);
				dao.deleteEvent();
				MobileSysToast.toast(More.this,"清除成功");
				break;
			case 2:
				Intent intent = new Intent(More.this, Login.class);
				More.this.startActivity(intent);
				More.this.finish();
				break;
			}
		}
	};

	private class Adapter extends BaseAdapter{

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int location) {
			return names[location];
		}

		@Override
		public long getItemId(int location) {
			return location;
		}

		@Override
		public View getView(int location, View arg1, ViewGroup arg2) {
			View layout = LayoutInflater.from(More.this).inflate(R.layout.more_item, null);
			TextView nameText = (TextView)layout.findViewById(R.id.more_item);
			if(location == 0){
				boolean done = mPref.getBoolean(SystemConfig.ACCEPTED, false);
				int index = mPref.getInt(SystemConfig.WHICH, -1);
				if(done){
					nameText.setText(names[0] + " :" + choises[index]);
					return layout;
				}
			}
			
			nameText.setText(names[location]);
			return layout;
		}
		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
			Intent intent = new Intent(More.this,Mobile.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
