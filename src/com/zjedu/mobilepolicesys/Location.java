package com.zjedu.mobilepolicesys;



import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Location extends Activity{
	private enum E_BUTTON_TYPE {
		LOC,
		COMPASS,
		FOLLOW
	}
	private SharedPreferences mPref = null;
	private E_BUTTON_TYPE mCurBtnType = null;
	private LocationClient mLocClient = null;
	private LocationData locData = null;
	private MyLocationOverlay mLocationOverlay = null;
	private MapView mMapView = null;	
	private MapController mMapController = null;
    private BMapManager mBMapManager = null;
	private Button requestLocButton = null;
	boolean isRequest = false;
	private String TAG = "Location";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBMapManager = new BMapManager(this.getApplicationContext());
        mBMapManager.init(mMGListener);
        setContentView(R.layout.location);
        setTitle("定位功能");
        requestLocButton = (Button)findViewById(R.id.button1);
        mPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        mCurBtnType = E_BUTTON_TYPE.LOC;
	    requestLocButton.setOnClickListener(btnClickListener);
	    
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapController.setZoom(13);
        mMapController.enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener(mBDBdLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll"); 
        mLocClient.setLocOption(option);
        mLocClient.start();
       
        int lat = mPref.getInt(SystemConfig.LAT, -1);
        int lon = mPref.getInt(SystemConfig.LON, -1);
        locData.latitude = lat;
        locData.longitude = lon;
		mLocationOverlay = new MyLocationOverlay(mMapView);
	    mLocationOverlay.setData(locData);
	    mMapController.animateTo(new GeoPoint(lat,lon));
		mMapView.getOverlays().add(mLocationOverlay);
		mLocationOverlay.enableCompass();
//		mMapView.setSatellite(true);
		mMapView.refresh();
		
    }
    
    public void requestLocClick(){
    	isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(Location.this, "正在定位……", Toast.LENGTH_SHORT).show();
    }
    
    
    private  OnClickListener btnClickListener = new OnClickListener() {
    	public void onClick(View v) {
			switch (mCurBtnType) {
			case LOC:
				requestLocClick();
				break;
			case COMPASS:
				mLocationOverlay.setLocationMode(LocationMode.NORMAL);
				requestLocButton.setText(R.string.dingwei);
				mCurBtnType = E_BUTTON_TYPE.LOC;
				break;
			case FOLLOW:
				mLocationOverlay.setLocationMode(LocationMode.COMPASS);
				requestLocButton.setText(R.string.luopan);
				mCurBtnType = E_BUTTON_TYPE.COMPASS;
				break;
			}
		}
	};
    
    private BDLocationListener mBDBdLocationListener = new BDLocationListener() {
    	@Override
    	public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            Log.d(TAG, "location----->onReceiveLocation" + "lat" + location.getLatitude() + "lon" + location.getLongitude());
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            mLocationOverlay.setData(locData);
            mMapView.refresh();
           
            if (isRequest){
            	Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                isRequest = false;
                mLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				requestLocButton.setText(R.string.follow);
                mCurBtnType = E_BUTTON_TYPE.FOLLOW;
            }
        }

		@Override
		public void onReceivePoi(BDLocation arg0) {
			
		}
	};
	
  private MKGeneralListener mMGListener = new MKGeneralListener() {
	
	@Override
	public void onGetPermissionState(int iError) {
		 if (iError != 0) {
             Log.d(TAG, TAG + "检查网络后再连。。。key认证失败");
         }
         else{
        	 Log.d(TAG, TAG + "key验证成功。。。");
         }
	}
	
	@Override
	public void onGetNetworkState(int iError) {
		if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
            Log.d(TAG, TAG + "网络错误。。。");
        }
        else if (iError == MKEvent.ERROR_NETWORK_DATA) {
        	Log.d(TAG, TAG + "检索输入错误。。。");
        }
	}
};
    

    @Override
    protected void onPause() {
    	if(mMapView != null)
    		mMapView.onPause();
    	if(mBMapManager != null)
    		mBMapManager.stop();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	if(mMapView != null)
    		mMapView.onResume();
    	if(mBMapManager != null)
    		mBMapManager.start();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
//        if (mLocClient != null)
//            mLocClient.stop();
        if(mMapView != null)
        	mMapView.destroy();
        if(mBMapManager != null)
        	mBMapManager.destroy();
        super.onDestroy();
        Log.d(TAG, TAG + "--->onDestroy");
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ){
			new AlertDialog.Builder(Location.this)
			.setMessage("确认退出定位吗?")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Location.this.finish();
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

