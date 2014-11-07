package com.zjedu.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * 
 * @author westlakeboy
 *
 */
public class MobileSysDialog {
	
	public static void show(Context context,String title,String msg){
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton("确定", null)
		.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
	
	public static void showandfinish(Context context,String title,String msg){
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
}
