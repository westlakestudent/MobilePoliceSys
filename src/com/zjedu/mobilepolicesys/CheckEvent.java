package com.zjedu.mobilepolicesys;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zjedu.dao.EventDao;
import com.zjedu.entity.Event;

public class CheckEvent extends Activity{
	private ListView mListView = null;
	private Button mRefreshButton = null;
	private List<Event> mEvents = new ArrayList<Event>();
	private EventDao dao = null;
	private Adapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkevent);
		init();
	}
	
	private void init(){
		mListView = (ListView)findViewById(R.id.mlist);
		mListView.setOnItemClickListener(mOnItemClickListener);
		mRefreshButton = (Button)findViewById(R.id.refresh);
		mRefreshButton.setOnClickListener(mOnClickListener);
		dao = new EventDao(CheckEvent.this);
		mEvents = dao.findEvents();
		if(mEvents.size() > 0){
			adapter = new Adapter(mEvents);
			mListView.setAdapter(adapter);
			setTitle("我的事件");
		}else{
			setTitle("未找到我的事件");
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mEvents = dao.findEvents();
			if(mEvents.size() > 0){
				adapter = new Adapter(mEvents);
				mListView.setAdapter(adapter);
				setTitle("我的事件");
			}else{
				setTitle("未找到我的事件");
			}
		}
	};
	
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
			LinearLayout linear = (LinearLayout)LayoutInflater.from(CheckEvent.this).inflate(R.layout.item, null);
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
			Intent intent = new Intent(CheckEvent.this,NewsDetail.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.EVENTID, mEvents.get(location).getId());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};
	
}
