package com.zjedu.mobilepolicesys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.zjedu.mobilepolicesys.R;
import com.zjedu.dao.HelpDao;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ForHelpMsg extends Activity{
	private Button mButton = null;
	private ListView mListView = null;
	private static final String TAG = "ForHelpMsg";
	
	private Adapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forhelpmsg);
		initUI();
		doQueryHmsg();
	}

	
	private void initUI(){
		mButton = (Button)findViewById(R.id.help_msg_refresh);
		mButton.setOnClickListener(mOnClickListener);
		mListView = (ListView)findViewById(R.id.help_msg_list);
		
	}
	
	
	private OnClickListener mOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doQueryHmsg();
			}
		};
		
		private void doQueryHmsg(){
			List<com.zjedu.entity.Help> helps = null;
			HelpDao dao = new HelpDao(ForHelpMsg.this);
			helps = dao.findHelps();
			Log.d(TAG, "helps size--->" + helps.size());
			adapter = new Adapter(helps);
			mListView.setAdapter(adapter);
		}
		
		
		
		
		private class Adapter extends BaseAdapter{

			private List<com.zjedu.entity.Help> messages = null;
			public Adapter(List<com.zjedu.entity.Help> messages){
				this.messages = messages;
			}
			@Override
			public int getCount() {
				return messages.size();
			}

			@Override
			public Object getItem(int location) {
				return messages.get(location);
			}

			@Override
			public long getItemId(int location) {
				return location;
			}

			@Override
			public View getView(int location, View v, ViewGroup arg2) {
				SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				View layout = LayoutInflater.from(ForHelpMsg.this).inflate(R.layout.help_item, null);
				TextView nametext = (TextView)layout.findViewById(R.id.help_msg_name);
				TextView datetext = (TextView)layout.findViewById(R.id.help_msg_date);
				TextView contentext = (TextView)layout.findViewById(R.id.help_msg_content);
				TextView addrtext = (TextView)layout.findViewById(R.id.help_msg_addr);
				
				String name = messages.get(location).getUsername();
				String date = messages.get(location).getTime();
				String content = messages.get(location).getContent();
				String addr = messages.get(location).getAddr();
				Date d = new Date(Long.valueOf(date));
				String time = sfd.format(d);
				nametext.setText(name );
				datetext.setText(time);
				contentext.setText(content);
				addrtext.setText(addr);
				
				return layout;
			}
			
		}
}
