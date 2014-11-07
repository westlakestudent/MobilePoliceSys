package com.zjedu.mobilepolicesys;

import com.zjedu.mobilepolicesys.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

@SuppressWarnings("deprecation")
public class MeEvent extends TabActivity{

	private TabHost mTabHost = null;
	private TabWidget mTabWidget = null; 
	private RadioGroup mRadioGroup = null;
	private RadioButton mRadioButton0 = null;
	private RadioButton mRadioButton1 = null;
	private static final String TAG = "MeEvent";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		initUI();
		setTitle("我的事件");
	}
	
	
	private void initUI(){
		mRadioButton0 = (RadioButton)findViewById(R.id.e_radio_button0);
		mRadioButton1 = (RadioButton)findViewById(R.id.e_radio_button1);
		mTabWidget = (TabWidget)findViewById(android.R.id.tabs);
		mRadioGroup = (RadioGroup)findViewById(R.id.e_main_radio);
		mTabHost = getTabHost();
		mRadioButton1.setChecked(true);
		
		mTabHost.addTab(mTabHost.newTabSpec("查看事件").setIndicator("查看事件").
				setContent(new Intent(MeEvent.this, CheckEvent.class)));
		mTabHost.addTab(mTabHost.newTabSpec("发布事件").
        		setIndicator("发布事件").setContent(new Intent(MeEvent.this, ReleaseEvent.class)));
		
		mTabHost.setCurrentTab(1);
		mRadioGroup.setOnCheckedChangeListener(mOnCheckedListenter);
		mTabWidget.setStripEnabled(false);
	}
	
	private OnCheckedChangeListener mOnCheckedListenter = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.e_radio_button0:
				mRadioButton0.setChecked(true);
				mTabHost.setCurrentTab(0);
				Log.d(TAG, TAG + "---checked 0");
				break;
			case R.id.e_radio_button1:
				mRadioButton1.setChecked(true);
				mTabHost.setCurrentTab(1);
				Log.d(TAG, TAG + "---checked 1");
				break;
			}
		}
		
	};
}
