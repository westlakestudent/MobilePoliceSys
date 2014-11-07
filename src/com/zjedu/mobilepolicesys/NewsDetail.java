package com.zjedu.mobilepolicesys;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjedu.dao.EventDao;
import com.zjedu.entity.Event;

/**
 * 
 * @author westlakeboy
 *
 */
public class NewsDetail extends Activity{

	private static final String TAG = "NewsDetail";
	private TextView mNews = null;
	private TextView mTitle = null;
	private ImageView mImage = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newslayout);
		setTitle("详细信息");
		init();
	}

	private void init(){
		mNews = (TextView)findViewById(R.id.news);
		mTitle = (TextView)findViewById(R.id.title);
		mImage = (ImageView)findViewById(R.id.news_image);
	    
		
		Bundle bundle = getIntent().getExtras();
		int eventid = bundle.getInt(SystemConfig.EVENTID,-1);
		EventDao dao = new EventDao(NewsDetail.this);
		if(eventid != -1){
			Event event = dao.findEvent(eventid);
			if(event != null){
				mTitle.setText(event.getTitle());
				mNews.setText(event.getContent());
				String path = event.getPath();
				Log.d(TAG, "path:" + path);
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				mImage.setImageBitmap(bitmap);
			}
		}
	}
	
	
}
