package com.fg114.main.service;

import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CommonObservable;
import com.fg114.main.util.CommonObserver;
import com.fg114.main.util.ContextUtil;

public class LocationUtil {
	
	
	private ArrayList<LocationProvider> locationUtils=new ArrayList<LocationProvider>();
	private static LocationUtil instance=new LocationUtil();
	private volatile boolean isRunning=false;
	private boolean inited=false;
	private Thread worker = new Thread() {
		@Override
		public void run() {
			while (true) {
				try {
					if (locationUtils != null) {
						for (final LocationProvider util : locationUtils) {
							if (isRunning && ActivityUtil.isOnForeground(ContextUtil.getContext())) {
								//Log.d("requestLocate","LocationProvider="+util.getClass());
								util.requestLocate();
								
							} else {
								//Log.d("stopLocate","LocationProvider="+util.getClass());
								//主线程里跑
								util.stopLocate();
							}
						}
					}
					SystemClock.sleep(5000);
				} catch (Throwable e) {
					e.printStackTrace();
					SystemClock.sleep(1000);
				}
			}
		}
	};
	//---
	private LocationUtil(){
	}

	public synchronized void init() {
		if(inited){
			return;
		}
		inited=true;
		setSavingPowerOn();
		addLocationProvider(BaiduLocationProvider.getInstance());
		worker.start();
	}
	public static LocationUtil getInstance(){
		return instance;
	}
	private void setSavingPowerOn(){
		//锁屏控制，当锁屏时就不再进行GPS定位
		Context context=ContextUtil.getContext();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				if (Intent.ACTION_SCREEN_ON.equals(action)) {
					start();
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					stop();
				}
			}
		}, filter);
	}
	public synchronized void addLocationProvider(LocationProvider provider){
		if(!locationUtils.contains(provider)){
			locationUtils.add(provider); 
		}
	}
	public synchronized void start(){
		if(!inited){
			throw new RuntimeException("LocationUtil has not inited!");
		}
		isRunning=true;
	}
	public synchronized void stop(){
		isRunning=false;
	}
	
	public interface LocationProvider {
		void requestLocate();
		void stopLocate();
	}
	public static void notifyGpsUpdated(){
		CommonObservable.getInstance().notifyObservers(LocationUtil.class);
	}
	/****************************************************************************************
	 * 监听处理器-----当start()之后，gps位置定位成功后会触发
	 ****************************************************************************************/
	public static class GpsUpdatedObserver extends CommonObserver {

		private Runnable runAfterGpsUpdated;

		/**
		 * 传入回调
		 * @param Runnable
		 */
		public GpsUpdatedObserver(Runnable runAfterGpsUpdated) {
			this.runAfterGpsUpdated = runAfterGpsUpdated;
		}

		/**
		 * 登录完成时，执行完成后，自己将自己从监听列表里清除
		 */
		public void update(CommonObservable commonObservable, Object arg) {
			
			// ---
			runAfterGpsUpdated.run();
			
		}
	}

	
}
