package com.zjedu.mobilepolicesys;

import com.zjedu.mobilepolicesys.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.RadioGroup.OnCheckedChangeListener;

@SuppressWarnings("deprecation")
public class Help extends TabActivity{
	private TabHost mTabHost = null;
	private TabWidget mTabWidget = null; 
	private RadioGroup mRadioGroup = null;
	private RadioButton mRadioButton0 = null;
	private RadioButton mRadioButton1 = null;
	private static final String TAG = "Help";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		initUI();
		setTitle("请求支援");
	}
	
	
	private void initUI(){
		mRadioButton0 = (RadioButton)findViewById(R.id.help_radio_button0);
		mRadioButton1 = (RadioButton)findViewById(R.id.help_radio_button1);
		mTabWidget = (TabWidget)findViewById(android.R.id.tabs);
		mRadioGroup = (RadioGroup)findViewById(R.id.help_main_radio);
		mTabHost = getTabHost();
		mRadioButton1.setChecked(true);
		
		mTabHost.addTab(mTabHost.newTabSpec("请求支援").setIndicator("请求支援").
				setContent(new Intent(Help.this, ForHelp.class)));
		mTabHost.addTab(mTabHost.newTabSpec("请求支援信息").
        		setIndicator("请求支援信息").setContent(new Intent(Help.this, ForHelpMsg.class)));
		
		mTabHost.setCurrentTab(1);
		mRadioGroup.setOnCheckedChangeListener(mOnCheckedListenter);
		mTabWidget.setStripEnabled(false);
	}
	
	
	private OnCheckedChangeListener mOnCheckedListenter = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.help_radio_button0:
				mRadioButton0.setChecked(true);
				mTabHost.setCurrentTab(0);
				Log.d(TAG, TAG + "---checked 0");
				break;
			case R.id.help_radio_button1:
				mRadioButton1.setChecked(true);
				mTabHost.setCurrentTab(1);
				Log.d(TAG, TAG + "---checked 1");
				break;
			}
		}
		
	};
}
