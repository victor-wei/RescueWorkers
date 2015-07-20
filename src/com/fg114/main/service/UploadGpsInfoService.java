package com.fg114.main.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.fg114.main.util.CommonObservable;
import com.fg114.main.util.CommonObserver;
import com.fg114.main.util.ContextUtil;
import com.fg114.main.util.SessionManager;
import com.rescueworkers.IndexActivity;
import com.rescueworkers.PushCommonActivity;
import com.rescueworkers.R;
import com.rescueworkers.dto.MainPageInfoDTO;
import com.rescueworkers.task.GetMainPageInfoTask;
import com.rescueworkers.task.UploadGpsInfoTask;

public class UploadGpsInfoService extends Service {

	static {
		CommonObservable.getInstance().addObserver(new MainPageinfoObserver());
	}
	private static volatile boolean isRunning=false;
	private static Thread worker = new Thread("MainPageInfo worker") {

		@Override
		public void run() {
			while (true) {
				try {
					SystemClock.sleep(1000 * 60);
					if(isRunning){
						continue;
					}
					MyLocation myLocation = MyLocation.getInstance();
					String latitude = "";
					String longitude = "";
					String locationTime = "";
					if (myLocation != null) {
						latitude = myLocation.getLatitude() + "";
						longitude = myLocation.getLongitude() + "";
						locationTime = myLocation.getLocationTime();
					}
					//Log.i("KeepAliveService","working");
					isRunning=true;
					UploadGpsInfoTask task = new UploadGpsInfoTask(null, ContextUtil.getContext(),null);
//					GetMainPageInfoTask task = new GetMainPageInfoTask(null, ContextUtil.getContext(),new Runnable() {
//						@Override
//						public void run() {
//							CommonObservable.getInstance().notifyObservers(MainPageinfoObserver.class);
//							CommonObservable.getInstance().notifyObservers(IndexActivity.MainPageinfoObserver.class);
//						}
//					},latitude,longitude,locationTime);
					//---
					task.execute(new Runnable() {
						public void run() {
							isRunning=false;
						}
					},new Runnable() {
						public void run() {
							isRunning=false;
						}
					});
				} catch (Throwable e) {
					if(e instanceof InterruptedException){
						return;//结束服务
					}
				}
			}

		}
	};

	@Override
	public void onCreate() {
		try {
			super.onCreate();
			Log.d("---KeepAliveService","onCreate");
			worker.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onDestroy() {
		try {
			super.onDestroy();
			Log.d("---KeepAliveService","onDestroy");
			Intent localIntent = new Intent();
			localIntent.setClass(this, UploadGpsInfoService.class); //销毁时重新启动Service
			if(worker!=null){
				worker.interrupt();
			}
			this.startService(localIntent);
		}
		catch (Exception e) {
			e.printStackTrace();	
		}
	}


	@Override
	public void onStart(Intent intent, int startId) {
		try {
			super.onStart(intent, startId);
			Log.d("---KeepAliveService","onDestroy");
		}
		catch (Exception e) {
			e.printStackTrace();	
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("---KeepAliveService","onBind");
		return null;
	}
	
//	private void startKeepAlives(long time) {
//		try {
//			time = time > 0 ? time : KEEP_ALIVE_INTERVAL;// 如果下次循环时间为0，则用默认时间 
//			Intent i = new Intent();
//			i.setClass(this, KeepAliveService.class);
//			i.setAction(ACTION_KEEPALIVE);
//			PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
//			AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
//			alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pi);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	/****************************************************************************************
	 * 监听处理器
	 ****************************************************************************************/
	public static class MainPageinfoObserver extends CommonObserver {

		//private Runnable runAfter;
		
		/**
		 * 传入回调
		 * @param Runnable
		 */
		public MainPageinfoObserver() {
			//this.runAfter = runAfter;
		}
		public void update(CommonObservable commonObservable, Object arg) {
			// 发送消息
			//runAfter.run();
			MainPageInfoDTO dto=SessionManager.getInstance().getMainPageInfo();
			if(dto!=null&&dto.isNeedUpdateTag()&&dto.taskNum>0){
				showNotification(dto);
			}
		}
		
		private void showNotification(MainPageInfoDTO dto) {
			try {
				Context context=ContextUtil.getContext();
				NotificationManager mNotifMan=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification();
				notification.defaults = Notification.DEFAULT_ALL;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.tickerText = "有新的今日任务";
				notification.icon = R.drawable.icon;
				notification.when = System.currentTimeMillis();

				Intent intent = new Intent(context, PushCommonActivity.class);
				PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				notification.setLatestEventInfo(context, "有新的今日任务", "共"+dto.taskNum+"条", pi);

				mNotifMan.notify("spotcheck",1, notification);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
