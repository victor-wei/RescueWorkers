package com.fg114.main.service;

import android.os.SystemClock;

/**
 * GPS缓存类，该类初始时是无效数据。当设置其值时，无效gps将不会被接受。
 * @author xujianjun,2013-04-27
 *
 */
public class MyLocation {
	
	private static final double INVALID_GPS_POINT=-999;
	private static final String INVALID_GPS_ADDRESS="无法确定您的位置";
	private static final int TIME_EXPIRATION=2*60*1000; //缓存超时时间
	//---定位类型
	public static enum LocationType{
		LOCATION_TYPE_GPS("GPS定位"),
		LOCATION_TYPE_NETWORK("网络定位"),//是笼统的说法，一般包括wifi和基站
		LOCATION_TYPE_WIFI("WIFI定位"),
		LOCATION_TYPE_BASE_STATION("基站定位"),
		LOCATION_TYPE_NONE("未知");
		//---
		String name;
		private LocationType(String name){
			this.name=name;
		}
		public String getName() {
			return this.name;
		}		
	}
	//缓存的gps
	private double latitude;
	private double longitude;
	private String address;
	private double radius; //精度
	private String locationTime; //定位时间
	private LocationType locationType=LocationType.LOCATION_TYPE_NONE;
	//gps时间戳
	private volatile long timestamp;
	//当获取前一个定位结果时，不用判断超时逻辑，只判断gps位置的有效性
	private boolean willNotExpire;
	
	//singleton
	private static MyLocation instance=new MyLocation(INVALID_GPS_POINT,INVALID_GPS_POINT,INVALID_GPS_ADDRESS,SystemClock.elapsedRealtime(),false,String.valueOf(SystemClock.elapsedRealtime()));
	
	private MyLocation(double latitude,double longitude,String address,long timestamp,boolean willNotExpire,String locationTime){
		this.latitude=latitude;
		this.longitude=longitude;
		this.address=address;	
		this.timestamp=timestamp;	
		this.willNotExpire=willNotExpire;
	}
	public static MyLocation getInstance(){
		return instance;
	}
	/**
	 * 设置成功返回true
	 * @param latitude
	 * @param longitude
	 * @param address
	 * @return
	 */
	public boolean setGpsLocation(double latitude,double longitude,String address,String locationTime){
		//是否是有效gps，如果不是有效的，则不更新内部缓存的数据（好让该缓存的gps超时）
		if(isValidLatitude(latitude) && isValidLongitude(longitude)){
			
			this.latitude=latitude;
			this.longitude=longitude;
			this.address=address;
			this.locationTime = locationTime;
			timestamp=SystemClock.elapsedRealtime();
			//Log.d("定位成功"+Thread.currentThread().getId(),toString());
			return true;
		}
		return false;
	}
	private boolean isValidLatitude(double latitude){
		return latitude>=-90&&latitude<=90;
	}
	private boolean isValidLongitude(double longitude){
		return longitude>=-180&&longitude<=180;
	}
	public String getAddress(){
		String lalo="[定位成功] 纬: "+latitude+", 经: "+longitude;
		return (address==null||address.trim().equals(""))&&isValid()?lalo:lalo+"\n地址信息: "+address;
	}
	public double getLatitude(){
		return latitude;		
	}
	public double getLongitude(){
		return longitude;		
	}
	public LocationType getLocationType() {
		return locationType;
	}
	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}
	public double getRadius() {
		
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public String getLocationTime(){
		return locationTime;
	}
	public void setLocationTime(String locationTime){
		this.locationTime = locationTime;
	}

	/**
	 * 超过5分钟的gps或者无效的gps都视为无效
	 * @return
	 */
	public boolean isValid(){
		return isValidLatitude(latitude) 
				&& isValidLongitude(longitude)
				&&((SystemClock.elapsedRealtime()-timestamp)<=TIME_EXPIRATION || willNotExpire);
	}
	
	/**
	 * 获得最近一次缓存的经纬度
	 * @return
	 */
	public MyLocation getPreviousLocation(){
		MyLocation loc=new MyLocation(this.latitude, this.longitude, this.address,0,true,this.locationTime);
		return loc;
	}
	public String toString(){
		return "["+locationType.getName()+"]["+minutesFromLong(SystemClock.elapsedRealtime()-timestamp)+"]"+"lat="+latitude+",lon="+longitude+"|"+address;
	}
	public String minutesFromLong(long millisecond){
		return String.format(String.format("%d sec", millisecond/1000));
		
	}

	
}
