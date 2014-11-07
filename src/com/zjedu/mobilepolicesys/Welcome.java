package com.zjedu.mobilepolicesys;

import com.zjedu.mobilepolicesys.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

/**
 * 
 * @author westlakeboy
 *
 */
public class Welcome extends Activity{

	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weclome);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(Welcome.this,Login.class);
				startActivity(intent);
				finish();
			}
		}, 2 * 1000);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
			new AlertDialog.Builder(Welcome.this)
					.setMessage("确认退出吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Welcome.this.finish();
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
