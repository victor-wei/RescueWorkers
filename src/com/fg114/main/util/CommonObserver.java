package com.fg114.main.util;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;

/**
 * 所有自定义监听者的定义都在这里
 * 
 * @author xujianjun,2012-09-18
 * 
 */
public class CommonObserver {

	public void update(CommonObservable commonObservable, Object arg) {

	}


	/****************************************************************************************
	 * 例子：监听处理器——Webview 评论提交后关闭页面并调用js
	 ****************************************************************************************/
	public static class CommentAddSuccessObserver extends CommonObserver {

		private WeakReference<Runnable> reference;

		/**
		 * 传入需要接受通知的WebView控件
		 * 
		 * @param webview
		 */
		public CommentAddSuccessObserver(Runnable runAfterCommentSuccess) {
			this.reference = new WeakReference<Runnable>(runAfterCommentSuccess);
		}

		/**
		 * 登录完成时，将执行此update方法通知WebView来执行一个js方法。 执行完成后，自己将自己从监听列表里清除
		 */
		public void update(CommonObservable commonObservable, Object arg) {
			if (reference == null) {
				commonObservable.deleteObserver(this);
				return;
			}
			// ---
			Runnable runAfterCommentSuccess = reference.get();
			if (runAfterCommentSuccess == null) {
				commonObservable.deleteObserver(this);
				return;
			}
			//WebUtils.jsCommentAddSuccess(webView, new JavaScriptInterface(""));
			runAfterCommentSuccess.run();
			commonObservable.deleteObserver(this);
		}
	}

}
