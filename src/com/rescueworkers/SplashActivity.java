package com.rescueworkers;

import com.fg114.main.util.ActivityUtil;

import android.app.*;
import android.os.*;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;

/**
 * 闪屏
 * 
 * @author wufucheng
 * 
 */
public class SplashActivity extends Activity {

	// private static final String TAG = SplashActivity.class.getSimpleName();

	private static final int mSleepTime = 1000; // 闪屏停留的时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	//executeCheckVersionTask();


//		boolean isShowNew = SharedprefUtil.getBoolean(SplashActivity.this, Settings.IS_SHOW_NEW_FEATURE, true);
//		Bundle bundle = new Bundle();
//		if (isShowNew) {
//			ActivityUtil.jump(SplashActivity.this, NewFeatureActivity.class, Settings.SPLASH_ACTIVITY, bundle);
//			finish();
//		} else {
//			ActivityUtil.jump(SplashActivity.this, IndexActivity.class, Settings.SPLASH_ACTIVITY, bundle);
//			finish();
//		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(1000);
				ActivityUtil.jump(SplashActivity.this, UserLoginActivity.class, 1);
				finish();				
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityUtil.exitApp(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
//
//	private Handler splashHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			boolean isShowNew = SharedprefUtil.getBoolean(SplashActivity.this, Settings.IS_SHOW_NEW_FEATURE, true);
//
//			Bundle bundle = new Bundle();
//			if (isShowNew) {
//				ActivityUtil.jump(SplashActivity.this, NewFeatureActivity.class, Settings.SPLASH_ACTIVITY, bundle);
//				finish();
//			} else {
//				ActivityUtil.jump(SplashActivity.this, IndexActivity.class, Settings.SPLASH_ACTIVITY, bundle);
//				finish();
//			}
//			super.handleMessage(msg);
//		}
//	};
	
//	/**
//	 * 版本管理
//	 */
//	private void executeCheckVersionTask() {
//		try {
//
//			/* 检查是否从旧版升级而来 */
//			// 获得当前版本号
//			int ver = ActivityUtil.getVersionCode(getBaseContext());
//			// 获得配置中的版本号
//			int localVer = SharedprefUtil.getInt(getBaseContext(), Settings.LOCAL_VERSION, ver);
//			// int localVer = SharedprefUtil.getLocalVersion(ver);
//			// 检查版本
//			if (localVer < ver) {
//				// /*3.1.34 修改为升级时不重置用户登录信息*/
//				/*
//				 * // 配置中的版本是老版本的场合，重置指定的配置（内容可以随版本变化变更）
//				 * SharedprefUtil.resetByKey(getBaseContext(),
//				 * Settings.IS_LOGIN_KEY);
//				 * SharedprefUtil.resetByKey(getBaseContext(),
//				 * Settings.LOGIN_USER_INFO_KEY);
//				 */
//				// 版本升级时firstopen为true
//				SharedprefUtil.saveBoolean(getBaseContext(), Settings.IS_FRIST_WITH_NET, true);
//
//				// SharedprefUtil.saveBoolean(getBaseContext(),
//				// Settings.IS_SHOW_NEW_FEATURE, true);
//			}
//
//
//			Thread checkVersionThread = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					// 检查版本
//					try {
//
//						SharedprefUtil.saveInt(getBaseContext(), Settings.LOCAL_VERSION, ActivityUtil.getVersionCode(getBaseContext()));
//						// SharedprefUtil.setLocalVersion(localVer);
//
//						// 调用91跟踪代码
//						TrackTool.track91();
//
//						boolean isFirstOpenWithNet = SharedprefUtil.getBoolean(SplashActivity.this, Settings.IS_FRIST_WITH_NET, true);
//						// Log.e("Fg114Application", "isFirstOpenWithNet=" +
//						// isFirstOpenWithNet);
//						JsonPack result = A57HttpApiV3.getInstance().chkVersion(ActivityUtil.getVersionName(SplashActivity.this), isFirstOpenWithNet,
//								Settings.CLIENT_TYPE, Settings.SELL_CHANNEL_NUM, ActivityUtil.getDeviceId(SplashActivity.this));
//						if (result.getRe() == 200) {
//							VersionChkDTO dto = JsonUtils.fromJson(result.getObj().toString(), VersionChkDTO.class);
//							if (dto != null) {
//								if (isFirstOpenWithNet) {
//									SharedprefUtil.saveBoolean(SplashActivity.this, Settings.IS_FRIST_WITH_NET, false);
//								}
//
//								// 设置讯飞语音参数
//								if (!CheckUtil.isEmpty(dto.getXfUrl()) && !CheckUtil.isEmpty(dto.getXfEngineName())) {
//									Settings.XF_Params = dto.getXfUrl();
//									Settings.XF_ENGINE_NAME = dto.getXfEngineName();
//								}
//
//								// 当前还未退出软件时处理
//								if (ActivityUtil.isOnForeground(ContextUtil.getContext())) {
//									Settings.gVersionChkDTO = dto;
//									// 通知版本更新
//									UpdateObserver.getInstance().sendBroadcast();
//								}
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					} catch (OutOfMemoryError e) {
//						ActivityUtil.saveOutOfMemoryError(e);
//					}
//
//					// 获取联通手机号
//					try {
//						String phone = A57HttpApiV3.getInstance().getUnicomPhoneNum();
////						 phone = "13200000000";
//						// Log.e(TAG, "unicom mNum=" + phone);
//						if (!CheckUtil.isEmpty(phone)) {
//							// 取到号码时固定调用接口，得到手机号
//							JsonPack result = A57HttpApiV3.getInstance().userLoginByPhone2(phone, SessionManager.getInstance().getUserInfo(ContextUtil.getContext()).getToken());
//							if (result.getRe() == 200) {
//								if (!SessionManager.getInstance().isUserLogin(SplashActivity.this)) {
//									// 没有登录的情况下，自动登录
//									UserInfo2DTO dto = UserInfo2DTO.toBean(result.getObj());
//									// 获得用户信息
//									SessionManager.getInstance().setUserInfo(ContextUtil.getContext(), dto);
//									// 设置登录状态
//									SessionManager.getInstance().setIsUserLogin(ContextUtil.getContext(), true);
//									// 是否需要同步微博
//									XApplication.isNeedRunWebbo = true;
//									
//									if (TextUtils.isEmpty(SharedprefUtil.get(ContextUtil.getContext(), Settings.ANONYMOUS_TEL, ""))) {
//										SharedprefUtil.save(ContextUtil.getContext(), Settings.ANONYMOUS_TEL, phone);
//									}
//								}
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					try {
//						// 同步用户信息(微博状态)
//						if (SessionManager.getInstance().isUserLogin(SplashActivity.this)) {
//							WeiboUtil.syncUserInfo(SessionManager.getInstance().getUserInfo(SplashActivity.this).getToken());
////							runOnUiThread(new Runnable() {								
////								@Override
////								public void run() {
////									SyncUserInfoTask task = new SyncUserInfoTask(null, SplashActivity.this,true);
////									task.execute();									
////								}
////							});
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//					// 调用更新城市和报错列表
//					try {
////						Log.e("Fg114Application","getSoftwareCommonData");
//						JsonPack result = A57HttpApiV3.getInstance().getSoftwareCommonData2(
//								SessionManager.getInstance().getCityList2DTO(SplashActivity.this).getTimestamp(),
//								SessionManager.getInstance().getListManager().getErrorReportTypeListPack(SplashActivity.this).getTimestamp(),
//								SessionManager.getInstance().getShRegionListDTO().getTimestamp());
//						if (result.getRe() == 200) {
//							SoftwareCommonData2 data = SoftwareCommonData2.toBean(result.getObj());
//							if (data.getCityDto() != null && data.getCityDto().isNeedUpdateTag()) {
//								Log.e("Fg114Application","getCityDto isNeedUpdateTag");
//								SessionManager.getInstance().setCityList2DTO(SplashActivity.this, data.getCityDto(), JsonUtils.toJson(data.getCityDto()));
//							}
//							if (data.getErrorReportTypeListDto() != null && data.getErrorReportTypeListDto().isNeedUpdateTag()) {
//								Log.e("Fg114Application","getErrorReportTypeListDto isNeedUpdateTag");
//								SessionManager.getInstance().getListManager().setErrorReportTypeListPack(SplashActivity.this, data.getErrorReportTypeListDto());
//							}
//							if (data.getPointsHintForShareSoftware() != null) {
//								SessionManager.getInstance().setPointsHintForShareSoftware(data.getPointsHintForShareSoftware());
//							}
//							if (data.getShRegionDto() != null && data.getShRegionDto().isNeedUpdateTag()) {
//								Log.e("Fg114Application","getShRegionDto isNeedUpdateTag");
//								SessionManager.getInstance().setShRegionListDTO(data.getShRegionDto());
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}catch (OutOfMemoryError e) {
//						ActivityUtil.saveOutOfMemoryError(e);
//					}
//
//
//				}
//			});
//			checkVersionThread.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
