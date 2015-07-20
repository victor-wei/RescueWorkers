package com.rescueworkers;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fg114.main.service.KeepAliveService;
import com.fg114.main.service.LocationUtil;
import com.fg114.main.service.http.AbstractHttpApi;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.ContextUtil;
import com.fg114.main.util.CrashHandler;
import com.rescueworkers.Db.DbHelper;

/**
 * Application
 */
public class XApplication extends Application {

	private static final String TAG = "---XApplication";
	private static final boolean DEBUG = true;
	public static CrashHandler crashHandler;

	// 获取位置管理服务
	public static DefaultHttpClient mHttpClient;
	
//	Handler locationMainThreadHander=new Handler(){
//        @Override 
//        public void handleMessage(Message msg) { 
//            try {
//				super.handleMessage(msg);
//				Runnable runner=msg.getCallback();
//				if(runner!=null){
//					runner.run();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//        }
//	};

	@Override
	public void onCreate() {
		super.onCreate();
		
		// 初始化上下文工具
		ContextUtil.init(this);
		Log.d(TAG,"onCreate");
		// 建立http client
		mHttpClient = AbstractHttpApi.createHttpClient();
		// 初始化崩溃捕获处理
		crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		try{
			// 发送崩溃日志
    		crashHandler.sendPreviousReportsToServer();
    	} catch(Throwable e){
    		e.printStackTrace();
    	}
		try {
			 DbHelper dbHelper = new DbHelper(getApplicationContext(), "rescueWorker.db", null, 1);
			 Settings.dbHelper = dbHelper;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 初始化全局变量
		Settings.ASS_PATH = getPackageResourcePath();
		Settings.DEV_ID = ActivityUtil.getDeviceId(this);
		Settings.VERSION_NAME = ActivityUtil.getVersionName(this);

		// 重置配置信息
		resetConfig();
		
		
		
		// 推送服务
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Intent intentService=new Intent(ContextUtil.getContext(),KeepAliveService.class);
				ContextUtil.getContext().startService(intentService);
			}
		}.sendEmptyMessageDelayed(0, 1000);
		//定位
		LocationUtil.getInstance().init();
		LocationUtil.getInstance().start();
		//Log.e("ActivityUtil.getRealDeviceId",ActivityUtil.getRealDeviceId(this));
	}


	/**
	 * 重置配置文件信息，可能一些版本的配置文件结构有修改， 为了避免错误需要修改该方法中的处理
	 */
	private void resetConfig() {
//		// 获得当前版本号
//		int ver = ActivityUtil.getVersionCode(getBaseContext());
//
//		// 获得配置中的版本号
//		int localVer = SharedprefUtil.getInt(getBaseContext(), Settings.LOCAL_VERSION, ver);
//
//		// 检查版本
//		if (localVer < ver) {
//			// 配置中的版本是老版本的场合，重置指定的配置（内容可以随版本变化变更）
//			SharedprefUtil.resetByKey(getBaseContext(), Settings.IS_LOGIN_KEY);
//			SharedprefUtil.resetByKey(getBaseContext(), Settings.LOGIN_USER_INFO_KEY);
//
//			// SharedprefUtil.save(getBaseContext(), Settings.CITY_LIST2, "{}");
//			// SharedprefUtil.saveBoolean(getBaseContext(),
//			// Settings.IS_SHOW_NEW_FEATURE, true);
//		}
//		SharedprefUtil.saveInt(getBaseContext(), Settings.LOCAL_VERSION, ver);
	}

}
