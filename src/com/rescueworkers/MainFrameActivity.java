package com.rescueworkers;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fg114.main.service.LocationUtil;
import com.fg114.main.service.MyLocation;
import com.fg114.main.service.LocationUtil.GpsUpdatedObserver;
import com.fg114.main.service.MyLocation.LocationType;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CommonObservable;
import com.fg114.main.util.ContextUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.ViewUtils;
import com.fg114.main.util.DialogUtil.DialogEventListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


/**
 * 框架
 * 
 */
public class MainFrameActivity extends Activity 
{

	// 功能组件
	protected Button btnGoBack;
	protected TextView tvTitle;
	protected Button btnOption;
	protected LinearLayout mainLayout;
	protected TextView locAddress;
	protected ImageButton locRefreshButton;
	protected ViewGroup locInfoLayout;
	private ProgressDialog progressDialog;
	MyLocation location=MyLocation.getInstance();
	
	protected boolean isFirstIn=true;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_frame);
		
		// 组件初始
		initComponent();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Settings.CURRENT_PAGE = this.getClass().getSimpleName();
		// 判断是否已登录
		boolean hasLogined = SessionManager.getInstance().isUserLogin(this);
		//没有登录，跳登录
		if (!hasLogined && !( this instanceof UserLoginActivity)) {
			ActivityUtil.jump(this, UserLoginActivity.class, 1,null,true);
			finish();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		System.gc();
	}

	@Override
	public void onConfigurationChanged(Configuration config)
	{
		super.onConfigurationChanged(config);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
//
//		if ((requestCode == Settings.CAMERAIMAGE || requestCode == Settings.LOCALIMAGE)) {
//			String path = null;
//			if (data != null && data.getData() != null) {
//				path = parseImgPath(data.getData());
//			} else if (takePhotoUri != null) {
//				path = parseImgPath(takePhotoUri);
//			}
//
//			try {
//				if (CheckUtil.isEmpty(path)) {
//					DialogUtil.showToast(this, "没有选择任何图片");
//					return;
//				}
//				// 如果未拍照或选择了空图片
//				if (new File(path).length() == 0) {
//					getContentResolver().delete(takePhotoUri, null, null);
//					return;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			Bundle bundle = new Bundle();
//			Settings.uploadPictureUri = path;
//			Settings.uploadPictureOrignalActivityId = Settings.INDEX_ACTIVITY;
//			if (mOnShowUploadImageListener != null) {
//				mOnShowUploadImageListener.onGetPic(bundle);
//			}
//
//			takePhotoUri = null;
//		} else if (requestCode != resultCode) {
//			this.setResult(resultCode, data);
//			this.finish();
//
//		} else {
//			super.onActivityResult(requestCode, resultCode, data);
//		}
	}


	@Override
	public void finish()
	{
		closeProgressDialog();
		Settings.CURRENT_PAGE = "";
		super.finish();

	}

	private void initComponent()
	{
		
		btnGoBack = (Button) findViewById(R.id.main_frame_btnGoBack);
		tvTitle = (TextView) findViewById(R.id.main_frame_tvTitle);
		btnOption = (Button) findViewById(R.id.main_frame_btnOption);
		mainLayout = (LinearLayout) findViewById(R.id.main_frame_layout);
		//--
		locAddress = (TextView) findViewById(R.id.main_frame_location_address);
        locRefreshButton=(ImageButton)findViewById(R.id.main_frame_location_refresh);
		locInfoLayout = (ViewGroup) findViewById(R.id.main_frame_locationLayout);
		btnOption.setVisibility(View.INVISIBLE);
		
		
		tvTitle.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(ActivityUtil.isTestDev(MainFrameActivity.this)){
					//测试位置，跳百度地图
					ActivityUtil.jump(MainFrameActivity.this, MapActivity.class, 1,null,true);
				}
				return false;
			}
		});
		
		locAddress.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(ActivityUtil.isTestDev(MainFrameActivity.this)){
					//Settings.DEBUG=!Settings.DEBUG;
					//DialogUtil.showToast(MainFrameActivity.this, "调试模式"+(Settings.DEBUG?"开启":"关闭"));
					if(A57HttpApiV3.getInstance().mApiBaseUrl==A57HttpApiV3.SERVICE_URL){
						A57HttpApiV3.getInstance().mApiBaseUrl=A57HttpApiV3.TEST_SERVICE_URL;
						DialogUtil.showToast(MainFrameActivity.this, "切换为测试版");
					}
					else{
						A57HttpApiV3.getInstance().mApiBaseUrl=A57HttpApiV3.SERVICE_URL;
						DialogUtil.showToast(MainFrameActivity.this, "切换为正式版");
					}
					
				}
				return false;
			}
		});
		// 返回按钮------------------------------------------------
		btnGoBack.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		locRefreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 2500);
				startLocate(2000);
			}
		});
		startLocate(4000);
	}
	private void startLocate(final int delayMilliseconds){
		locAddress.setText("正在定位...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(delayMilliseconds);
				setLocationView();
			}
		},"startLocate").start();
	}
	//使用位置信息设置locationView
	private void setLocationView(){
		runOnUiThread(new Runnable() { 
			@Override
			public void run() {
				if(location.isValid()){
					locAddress.setText(location.getAddress());
				}else{
					//如果是第一次进入页面，没有定到位时，这里再重试一次
					if(isFirstIn){
						startLocate(4000);
						isFirstIn=false;
						return;
					}
					locAddress.setText("无法获取您的位置");
				}
			}
		});
		
	}
	/**
	 * 打开进度提示
	 */
	public void showProgressDialog(String msg)
	{
		try {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setTitle("");
				progressDialog.setMessage(msg);
				progressDialog.setIndeterminate(true);
			}
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭进度提示
	 */
	public void closeProgressDialog()
	{
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			progressDialog = null;
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	//----------------------------//gps位置监听------------------------------
//	public static GpsUpdatedObserver GpsUpdatedObserver=new GpsUpdatedObserver(new GpsCallBack());
//	static {
//		CommonObservable.getInstance().addObserver(GpsUpdatedObserver);
//	}
//	private static class GpsCallBack implements Runnable{
//
//		@Override
//		public void run() {
//			try{
//				
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
	/**
	 * 创建Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if(ActivityUtil.isTestDev(this)){
			DialogUtil.showDialog(this, R.layout.dialog_request_log, new DialogEventListener() {
				
				@Override
				public void onInit(View contentView, PopupWindow dialog) {
					TextView text=(TextView)contentView.findViewById(R.id.log);
					text.setText(Settings.requestLog.toString());
				}
			});
		}
		return false;
	}

}
