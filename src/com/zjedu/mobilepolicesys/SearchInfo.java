package com.zjedu.mobilepolicesys;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zjedu.mobilepolicesys.R;
import com.zjedu.dao.EventDao;
import com.zjedu.entity.Event;
import com.zjedu.widget.MobileSysDialog;

public class SearchInfo extends Activity{

	private SharedPreferences mPref = null;
	private EditText mSearchEdit = null;
	private Button mSearchButton = null;
	private ListView mListView = null;
	private RadioButton mLocationButton = null;
	private RadioButton mTitleButton = null;
	private Adapter adapter = null;
	private List<Event> findevents = new ArrayList<Event>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		setContentView(R.layout.searchinfo);
		initUI();
	}
	
	private void initUI(){
		mSearchEdit = (EditText)findViewById(R.id.search_edit);
		mSearchEdit.setOnFocusChangeListener(mOnFocusChangeListener);
		mSearchButton = (Button)findViewById(R.id.btn_search);
		mSearchButton.setOnClickListener(mOnClickListener);
		mLocationButton = (RadioButton)findViewById(R.id.radioBtnLocation);
		mTitleButton = (RadioButton)findViewById(R.id.radioBtnTitle);
		mListView = (ListView)findViewById(R.id.list);
		mListView.setOnItemClickListener(mOnItemClickListener);
		
		String username = mPref.getString(SystemConfig.USERNAME, null);
		if(username != null)
			setTitle(username + "/ 信息查询");
	}
	
	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				mSearchEdit.setHint("");
			}
		}
	};
	
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String flag = mSearchEdit.getText().toString().trim();
			boolean done = false;
			if(mLocationButton.isChecked()){
				if(flag != null)
					done = geteventbyLocation(flag);
			}else if(mTitleButton.isChecked()){
				if(flag != null)
					done = geteventbyTitle(flag);
			}
			
			if(!done){
				MobileSysDialog.show(SearchInfo.this, "提示", "未找到相关事件.");
			}
			
		}
	};
	
	
	private boolean geteventbyLocation(String location){
		if(!findevents.isEmpty())
			findevents.clear();
		boolean success = false;
		EventDao  dao = new EventDao(SearchInfo.this);
		List<Event> events = dao.findEvents();
		if(events.size() > 0){
			for(Event event :events){
				if(event.getLocation().contains(location))
					findevents.add(event);
			}
			adapter = new Adapter(findevents);
			mListView.setAdapter(adapter);
			if(findevents.size() > 0)
				success = true;
		}
		return success;
	}
	
	
	private boolean geteventbyTitle(String title){
		if(!findevents.isEmpty())
			findevents.clear();
		boolean success = false;
		EventDao  dao = new EventDao(SearchInfo.this);
		List<Event> events = dao.findEvents();
		if(events.size() > 0){
			for(Event event :events){
				if(event.getTitle().contains(title))
					findevents.add(event);
			}
			adapter = new Adapter(findevents);
			mListView.setAdapter(adapter);
			if(findevents.size() > 0)
				success = true;
		}
		return success;
	}
	
	
	private class Adapter extends BaseAdapter{

		List<Event> events = null;
		public Adapter(List<Event> events){
			this.events = events;
		}
		@Override
		public int getCount() {
			return events.size();
		}

		@Override
		public Object getItem(int location) {
			return events.get(location);
		}

		@Override
		public long getItemId(int location) {
			return location;
		}

		@Override
		public View getView(int location, View v, ViewGroup arg2) {
			LinearLayout linear = (LinearLayout)LayoutInflater.from(SearchInfo.this).inflate(R.layout.item, null);
			TextView title = (TextView)linear.findViewById(R.id.item_text);
			TextView loca = (TextView)linear.findViewById(R.id.item_location);
			title.setText(events.get(location).getTitle());
			loca.setText(events.get(location).getLocation());
			return linear;
		}
		
	}
	
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int location,
				long arg3) {
			Intent intent = new Intent(SearchInfo.this,NewsDetail.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.EVENTID, findevents.get(location).getId());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};
	
}
