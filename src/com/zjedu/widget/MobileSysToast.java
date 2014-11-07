package com.zjedu.widget;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author westlakeboy
 *
 */
public class MobileSysToast {

	public static void toast(Context context,String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
