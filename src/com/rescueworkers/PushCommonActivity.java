package com.rescueworkers;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CheckUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.SharedprefUtil;
import com.fg114.main.util.URLExecutor;
import com.fg114.main.util.ViewUtils;

/**
 * 消息推送
 * 
 */
public class PushCommonActivity extends Activity {

	// 传入参数获得
	Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pushcommonlayout);

		bundle = getIntent().getExtras();
		ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);

		if (tasksInfo.get(0).numActivities > 1) {
			//已在软件中，跳登录页
			PushCommonActivity.this.finish();
			ActivityUtil.jumpNotForResult(PushCommonActivity.this,UserLoginActivity.class, bundle, true);
		} else {
			//新进软件，跳闪屏页
			PushCommonActivity.this.finish();
			ActivityUtil.jumpNotForResult(PushCommonActivity.this,SplashActivity.class, bundle, false);
		}
//		//---
//		ActivityManager activityManager = (ActivityManager) this
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		final List<RunningTaskInfo> tasksInfo = activityManager
//				.getRunningTasks(1);
//
//		if (tasksInfo.get(0).numActivities > 1) {
//			DialogUtil.showComfire(this, null, msgData.getTitle(), msgData.getOkButtonName(),
//					new Runnable() {
//
//						@Override
//						public void run() {							
//							handlePushMessage(PushCommonActivity.this, msgData);
//							PushCommonActivity.this.finish();
//						}
//					}, 
//					msgData.getCancelButtonName(), 
//					new Runnable() {
//
//						@Override
//						public void run() {
//							PushCommonActivity.this.finish();
//						}
//					});
//		} else {
//			PushCommonActivity.this.finish();
//			ActivityUtil.jumpNotForResult(PushCommonActivity.this,
//					IndexActivity.class, bundle, false);
//		}


	}
//	
//	//处理推送消息
//	public static void handlePushMessage(Activity context, PushMsg2DTO msgData){
//		//类别  1:广告链接  2：本地连接  3:普通链接（需要跳系统浏览器）
//		//普通链接跳转到webview页面, 本地链接使用url处理器
//		if (msgData.getTypeTag() == 1) {
//			// 广告链接，使用内嵌的WebView打开
//			Bundle bd = new Bundle();
//			bd.putInt(Settings.BUNDLE_KEY_FROM_PAGE, Settings.USER_CENTER_ACTIVITY);
//			bd.putString(Settings.BUNDLE_KEY_WEB_URL, msgData.getAdvUrl());
//			bd.putString(Settings.BUNDLE_KEY_WEB_TITLE, msgData.getTitle());
//			ActivityUtil.jump(context, SimpleWebViewActivity.class, Settings.USER_CENTER_ACTIVITY, bd, false);
//
//		} else if (msgData.getTypeTag() == 2) {
//			// 本地链接，跳转本地界面
//			URLExecutor.execute(msgData.getAdvUrl(), context, Settings.INDEX_ACTIVITY);
//		} else if (msgData.getTypeTag() == 3) {
//			// 普通链接，使用系统浏览器打开
//			ActivityUtil.jumbToWeb(context, msgData.getAdvUrl());
//		}
//	}

}
