package com.rescueworkers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.fg114.main.service.MyLocation;

public class MapActivity extends Activity {
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	TextView gps=null;
	Thread thread=null;
	MapController mMapController;
	boolean isLocated=false;
	boolean isRunning=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBMapMan=new BMapManager(getApplication());
        mBMapMan.init("FB6E7D2A06F28684A1F26F7C19788241D1AFE39C", null); 
        
        setContentView(R.layout.map_activity);
        
        mMapView=(MapView)findViewById(R.id.mapview);
        mMapView.setBuiltInZoomControls(true);
        mMapController=mMapView.getController();
        mMapController.setZoom(19);
        gps=(TextView)findViewById(R.id.gps);
    }

    @Override
    protected void onDestroy(){
            mMapView.destroy();
            if(mBMapMan!=null){
                    mBMapMan.destroy();
                    mBMapMan=null;
            }
            super.onDestroy();
    }
    @Override
    protected void onPause(){
    		isRunning=false;
            mMapView.onPause();
            if(mBMapMan!=null){
                    mBMapMan.stop();
            }
            super.onPause();
    }

	@Override
	protected void onResume() {
		isRunning=true;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isRunning) {
					SystemClock.sleep(1000);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try{
							MyLocation loc = MyLocation.getInstance();
							gps.setText(loc.toString());
							if (loc.isValid()) {
								mMapView.getOverlays().clear();
								GeoPoint point = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
								OverlayItem item = new OverlayItem(point,"item3","item3");
								OverlayTest overlay=new OverlayTest(getResources().getDrawable(R.drawable.icon_location),mMapView);
								mMapView.getOverlays().add(overlay);
								overlay.addItem(item);
								mMapView.refresh();
								if(!isLocated){
									mMapController.setCenter(point);
									isLocated=true;									
								}
							}
							}catch(Exception e){
								e.printStackTrace();								
							}
						}
					});
					SystemClock.sleep(6000);
				}
			}
		});
		// ---
		thread.start();
		mMapView.onResume();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
		super.onResume();
	}
	class OverlayTest extends ItemizedOverlay<OverlayItem> {
	    //用MapView构造ItemizedOverlay
	    public OverlayTest(Drawable marker,MapView mapView){
	            super(marker,mapView);
	    }
	    protected boolean onTap(int index) {
	        return true;
	    }
	        public boolean onTap(GeoPoint pt, MapView mapView){
	                //在此处理MapView的点击事件，当返回 true时
	                super.onTap(pt,mapView);
	                return false;
	        }
	        // 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口
	        /*
	        @Override
	        protected OverlayItem createItem(int i) {
	                return mGeoList.get(i);
	        }
	       
	        @Override
	        public int size() {
	                return mGeoList.size();
	        }
	        */

	}        
}
