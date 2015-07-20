package com.fg114.main.service;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
import com.fg114.main.service.LocationUtil.LocationProvider;
import com.fg114.main.service.MyLocation.LocationType;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CommonObservable;
import com.fg114.main.util.ContextUtil;
import com.fg114.main.util.SessionManager;
import com.rescueworkers.Db.DbHelper;
import com.rescueworkers.Db.GpsDb;
import com.rescueworkers.dto.LocationInfo;

public class BaiduLocationProvider implements LocationProvider {

	private LocationClient locationClient;
	private BDLocationListener myListener;
	private LocationClientOption option;
	// 授权Key
	String mStrKey = "B88FA5B7055C3B7D8C6C074AA4E79FB31EBCA790";//这是地图的，这里定位SDK没有用到
		
	//
	private static BaiduLocationProvider instance = new BaiduLocationProvider();

	private BaiduLocationProvider() {
	        
		locationClient = new LocationClient(ContextUtil.getContext()); // 声明LocationClient类
		myListener = new MyLocationListener();
		locationClient.registerLocationListener(myListener); // 注册监听函数
		option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms，这里不设置，使用手工发起定位
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		locationClient.setLocOption(option);
		
	}

	public static BaiduLocationProvider getInstance() {
		return instance;
	}

	@Override
	public void requestLocate() {
		
		if (!locationClient.isStarted()) {
			locationClient.start();
		}
		if (locationClient != null && locationClient.isStarted()){
			locationClient.requestLocation();
		}
	}

	@Override
	public void stopLocate() {
		locationClient.stop();

	}

	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;
			}
			//--------------------------------------------
			MyLocation loc=MyLocation.getInstance();
			boolean isValid=false;
			if(location.getLocType() == BDLocation.TypeGpsLocation||location.getLocType() == BDLocation.TypeNetWorkLocation){
				String address=location.hasAddr()
						&&location.getCity()!=null
						&&location.getDistrict()!=null
						&&location.getStreet()!=null
						?location.getCity()+location.getDistrict()+location.getStreet():"";
				//location.getAddrStr();
				isValid=loc.setGpsLocation(location.getLatitude(), location.getLongitude(), address,location.getTime());
				if(isValid){
					// 保存经纬度信息
					try {
						GpsDb.saveGps(location.getLatitude()+"", location.getLongitude()+"",location.getTime());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					loc.setLocationType(location.getLocType() == BDLocation.TypeGpsLocation?LocationType.LOCATION_TYPE_GPS:LocationType.LOCATION_TYPE_NETWORK);
					loc.setRadius(location.getRadius());
					LocationUtil.notifyGpsUpdated();
				}
			}			
			
			//--------------------------------------------
//			StringBuffer sb = new StringBuffer(256);
//			sb.append("time : ");
//			sb.append(location.getTime());
//			sb.append("\nerror code : ");
//			sb.append(location.getLocType());
//			sb.append("\nlatitude : ");
//			sb.append(location.getLatitude());
//			sb.append("\nlontitude : ");
//			sb.append(location.getLongitude());
//			sb.append("\nradius : ");
//			sb.append(location.getRadius());
//			if (location.getLocType() == BDLocation.TypeGpsLocation) {
//				sb.append("\nspeed : ");
//				sb.append(location.getSpeed());
//				sb.append("\nsatellite : ");
//				sb.append(location.getSatelliteNumber());
//			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//				sb.append("\naddr : ");
//				sb.append(location.getAddrStr());
//			}
//
//			//Log.d("BaiduLocationProvider",sb.toString());
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			
		}
	}
}
