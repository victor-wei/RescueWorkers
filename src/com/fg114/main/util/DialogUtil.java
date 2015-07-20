package com.fg114.main.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fg114.main.app.view.MyImageView;
import com.fg114.main.service.dto.CommonTypeDTO;
import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.rescueworkers.R;
import com.rescueworkers.Settings;

/**
 * 系统提示对话框
 * 
 * @author zhangyifan
 * 
 */
public class DialogUtil
{

	private static final String TAG = DialogUtil.class.getName();

	private static final int TOAST_INTERVAL = 2000;
	private static long lastTime = 0;

	private static final String TAG_TYPE_NEARBY = "nearby";
	private static final int DEFAULT_DISTANCE = 5000;
	private static final String[] DAT_OF_WEEK = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	private static int mYear = 0;
	private static int mMonth = 0;
	private static int mDay = 0;
	private static int mHour = 0;
	private static int mMinute = 0;
	private static int mDayOfWeek = Calendar.SUNDAY;
	private static String strDayName;
	/**
	 * 提示
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showAlert(Context ctx, boolean isTwoButton, String msg, OnClickListener... listerner)
	{
		try {
			// 创建提示框
			Builder builder = new Builder(ctx);
			builder.setCancelable(false);
			builder.setMessage(msg);
			builder.setPositiveButton("确定", listerner[0]);
			if (isTwoButton) {
				builder.setNegativeButton("取消", listerner[1]);
			}
			builder.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 自定义提示
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showAlert(Context ctx, boolean isTwoButton, String title, String msg, String firstBtnName, String SecoundBtnName, OnClickListener... listerner)
	{
		try {
			// 创建提示框
			Builder builder = new Builder(ctx);
			builder.setCancelable(false);
			builder.setMessage(msg);
			builder.setTitle(title);
			// builder.setIcon(0);//去除标题图片
			builder.setPositiveButton(firstBtnName, listerner[0]);
			if (isTwoButton) {
				builder.setNegativeButton(SecoundBtnName, listerner[1]);
			}
			builder.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 自定义提示
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showAlert(Context ctx, String title, String msg, String firstBtnName, String secoundBtnName, String thirdBtnName, OnClickListener... listener)
	{
		try {
			// 创建提示框
			Builder builder = new Builder(ctx);
			builder.setCancelable(false);
			builder.setMessage(msg);
			builder.setTitle(title);
			// builder.setIcon(0);//去除标题图片
			builder.setPositiveButton(firstBtnName, listener != null && listener.length > 0 ? listener[0] : null);
			builder.setNeutralButton(secoundBtnName, listener != null && listener.length > 1 ? listener[1] : null);
			builder.setNegativeButton(thirdBtnName, listener != null && listener.length > 2 ? listener[2] : null);
			builder.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 简单alert提示
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showAlert(Context ctx, String title, String msg)
	{
		showAlert(ctx, title, msg, null);
	}
	/**
	 * 简单alert提示
	 * 
	 * @param ctx
	 * @param msg
	 * @param runAfterAlert
	 *            对话框关闭后，需要执行的任务
	 */
	public static void showAlert(Context ctx, String title, String msg, final Runnable runAfterAlert)
	{
		try {
			// 创建提示框
			Builder builder = new Builder(ctx);
			builder.setCancelable(false);
			builder.setTitle(title);
			builder.setMessage(msg);
			builder.setNegativeButton("确定", new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (runAfterAlert != null) {
						runAfterAlert.run();
					}
				}
			});
			builder.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 版本更新提示框
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showVerComfire(final Context ctx, boolean isForceUpdate, final String version, String msg, OnClickListener... listerner)
	{
		// 创建提示框
		Builder builder = new Builder(ctx);
		builder.setCancelable(false);
		// 初始化显示组件
		LinearLayout dialogLayout = new LinearLayout(ctx);
		dialogLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		dialogLayout.setOrientation(LinearLayout.VERTICAL);
		dialogLayout.setGravity(Gravity.LEFT);
		dialogLayout.setPadding(10, 10, 10, 10);
		// 对话框显示信息
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 5, 0, 0);
		TextView tvMsg = new TextView(ctx);
		tvMsg.setLayoutParams(layoutParams);
		tvMsg.setTextColor(ctx.getResources().getColor(R.color.text_color_white));
		tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		tvMsg.setText(msg);
		dialogLayout.addView(tvMsg);
		if (!isForceUpdate) {
			// 获得组件状态
			boolean isShow = SharedprefUtil.getBoolean(ctx, Settings.IS_AUTO_SHOW_UPDATE_DIALOG, true);

			CheckBox cbNotShow = new CheckBox(ctx);
			cbNotShow.setLayoutParams(layoutParams);
			cbNotShow.setTextColor(ctx.getResources().getColor(R.color.text_color_white));
			cbNotShow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			cbNotShow.setText("下次不再提醒");
			cbNotShow.setChecked(!isShow);
			dialogLayout.addView(cbNotShow);

			cbNotShow.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					SharedprefUtil.saveBoolean(ctx, Settings.IS_AUTO_SHOW_UPDATE_DIALOG, !isChecked);
					SharedprefUtil.save(ctx, Settings.UPDATE_VERSION, version);
				}
			});
		}
		builder.setIcon(null);
		builder.setView(dialogLayout);
		builder.setPositiveButton("赶紧更新", listerner[0]);
		if (!isForceUpdate) {
			builder.setNegativeButton("后悔吧你", listerner[1]);
		} else {
			builder.setNegativeButton("不更新并退出", new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					ActivityUtil.exitApp((Activity) ctx);
				}
			});
		}
		builder.show();
	}

	/**
	 * 提示确认对话框
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showComfire(final Context ctx, String title, String msg, String button1, final Runnable button1ClickListerner, String button2, final Runnable button2ClickListerner)
	{
		// 创建提示框
		Builder builder = new Builder(ctx);
		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMessage(msg);
		builder.setPositiveButton(button1, new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				((Activity) ctx).runOnUiThread(button1ClickListerner);
			}
		});
		builder.setNegativeButton(button2, new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				((Activity) ctx).runOnUiThread(button2ClickListerner);
			}
		});
		builder.show();
	}

	/**
	 * 提示确认对话框
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showComfire(final Context ctx, String title, String msg, String[] button, final Runnable... buttonClickListerner)
	{
		// 创建提示框
		Builder builder = new Builder(ctx);
		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMessage(msg);
		if (button.length > 0) {
			builder.setPositiveButton(button[0], new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					((Activity) ctx).runOnUiThread(buttonClickListerner[0]);
				}
			});
		}
		if (button.length > 1) {
			builder.setNegativeButton(button[1], new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					((Activity) ctx).runOnUiThread(buttonClickListerner[1]);
				}
			});
		}
		if (button.length > 2) {
			builder.setNeutralButton(button[2], new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					((Activity) ctx).runOnUiThread(buttonClickListerner[2]);
				}
			});
		}

		builder.show();
	}

	/**
	 * 显示提示框，判断时间 防止
	 */
	public static void showToast(Context ctx, String msg)
	{
		if (TextUtils.isEmpty(msg)) {
			return;
		}
		// if (lastTime == 0) {
		// makeToast(ctx, msg);
		// lastTime = System.currentTimeMillis();
		// }
		// long thisTime = System.currentTimeMillis();
		// if (thisTime - lastTime > TOAST_INTERVAL) {
		makeToast(ctx, msg);
		// lastTime = thisTime;
		// }
	}

	/**
	 * 制作Toast
	 * 
	 * @param ctx
	 * @param msg
	 * @return
	 */
	private static void makeToast(Context ctx, String msg)
	{
		// 自定义Toast内容
		TextView toastText = new TextView(ctx);
		toastText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		toastText.setBackgroundResource(R.drawable.bg_alert);
		toastText.setPadding(20, 20, 20, 20);
		toastText.setText(msg);
		toastText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		toastText.setTextColor(ctx.getResources().getColor(R.color.text_color_black));
		// 自定义Toast设置
		Toast toast = new Toast(ctx);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastText);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}






	/**
	 * 补全带参数的message
	 * 
	 * @param msg
	 * @param args
	 * @return
	 */
	public static String fullMsg(String msg, String... args)
	{
		String fullMsg = msg;
		for (int i = 0; i < args.length; i++) {
			fullMsg = fullMsg.replace("{" + i + "}", args[i]);
		}
		return fullMsg;
	}

	public static ProgressDialog showProgressDialog(Context context, String msg, boolean isIndeterminate, int max, DialogInterface.OnCancelListener listerner)
	{
		ProgressDialog pdlg = new ProgressDialog(context);
		pdlg.setTitle("");
		pdlg.setMessage(msg);
		pdlg.setIndeterminate(isIndeterminate);
		if (isIndeterminate) {
			pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		} else {
			pdlg.setMax(max);
			pdlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		pdlg.setOnCancelListener(listerner);
		try {
			pdlg.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return pdlg;
	}

	public static void dismissProgressDialog(ProgressDialog pdlg)
	{
		try {
			if (pdlg != null && pdlg.isShowing()) {
				pdlg.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static ProgressDialog getProgressDialog(Context context, String msg, boolean isIndeterminate, int max, DialogInterface.OnCancelListener listerner)
	{
		ProgressDialog pdlg = new ProgressDialog(context);
		pdlg.setTitle("");
		pdlg.setMessage(msg);
		pdlg.setIndeterminate(isIndeterminate);
		if (isIndeterminate) {
			pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		} else {
			pdlg.setMax(max);
			pdlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		pdlg.setOnCancelListener(listerner);
		return pdlg;
	}




	/**
	 * 显示全屏朦皮
	 * 
	 * @param context
	 * @param drawableResourceId
	 *            要显示的朦皮的resourceId
	 */
	public static void showVeilPicture(final Activity context, int drawableResourceId)
	{

		try {
			LinearLayout content = new LinearLayout(context);
			final PopupWindow window = new PopupWindow(context);
			window.setOutsideTouchable(true);
			window.setFocusable(true);
			window.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
			window.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
			window.setContentView(content);
			window.setBackgroundDrawable(context.getResources().getDrawable(drawableResourceId));
			window.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			/*
			 * //校准顶部位置 Window parentWindow=context.getWindow(); Rect r = new
			 * Rect(); parentWindow.getDecorView().getRootView().
			 * getWindowVisibleDisplayFrame(r); int contentViewTop=r.top;
			 * content.setPadding(0, contentViewTop, 0, 0); View v=new
			 * View(context); v.setBackgroundColor(0xFF0000FF);
			 * v.setLayoutParams(new
			 * android.view.ViewGroup.LayoutParams(126,126));
			 * content.addView(v);
			 */
			// ----
			window.setTouchInterceptor(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					window.dismiss();
					return true;
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 只会显示一次的全屏朦皮，朦皮以键值keyname来区分
	 * 
	 * @param context
	 * @param drawableResourceId
	 *            要显示的朦皮的resourceId
	 * @param keyname
	 *            用来标识朦皮的唯一名称
	 */
	public static void showVeilPictureOnce(final Activity context, final int drawableResourceId, final String keyname)
	{

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try {
					if (SharedprefUtil.getBoolean(context, keyname, true)) {
						SystemClock.sleep(500);

						context.runOnUiThread(new Runnable()
						{

							@Override
							public void run()
							{
								try {

									DialogUtil.showVeilPicture(context, drawableResourceId);
									SharedprefUtil.saveBoolean(context, keyname, false);
								} catch (Exception ex) {
									Log.e("showVeilPictureOnce " + keyname, ex.getMessage(), ex);
								}
							}
						});
					}
				} catch (Exception ex) {
					Log.e("showVeilPictureOnce " + keyname, ex.getMessage(), ex);
				}

			}

		}).start();
	}
	/*
	 * 适用于通用对话框的初始化事件和销毁事件监听接口
	 */
	public interface DialogEventListener
	{
		/**
		 * @param contentView
		 *            对话框的内容区
		 * @param dialog
		 *            对话框
		 */
		public void onInit(View contentView, PopupWindow dialog);
	}

	/**
	 * 通用对话框，具有固定的半通明全屏背景，使用一个自定义的layout资源作为对话框的外观。 内部通过popwindow实现。
	 * 
	 * @param layoutResourceId
	 *            对话框要显示的内容。
	 * @param listener
	 *            一个监听对话框生命期的监听器。
	 */
	public static void showDialog(Activity activity, final int layoutResourceId, final DialogEventListener listener)
	{

		Context context = activity;
		Window parentWindow = activity.getWindow();

		// 对话框内容
		View content = View.inflate(context, layoutResourceId, null);
		final PopupWindow window = new PopupWindow(context, null, 0);
		window.setOutsideTouchable(true);
		window.setFocusable(true);
		window.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
		window.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
		ColorDrawable color = new ColorDrawable(0x99000000);
		window.setBackgroundDrawable(color);
		window.setContentView(content);
		window.showAtLocation(parentWindow.getDecorView(), Gravity.CENTER, 0, 0);

		// 校正内容区域的位置
		int contentViewTop = parentWindow.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		Rect r = new Rect();
		parentWindow.getDecorView().getRootView().getWindowVisibleDisplayFrame(r);
		contentViewTop = r.top;

		window.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{

			}
		});
		content.setPadding(0, contentViewTop, 0, 0);
		// ---
		if (listener != null) {
			listener.onInit(content, window);
		}
	}



	/**
	 * 一个通用的callback，使用者双方可以根据约定来使用不同功能的回调方法
	 */
	public static abstract class GeneralCallback
	{
		public void run()
		{
		}
		//
		public void run(Bundle data)
		{
		}
	}


	/**
	 * 显示一个简单的列表对话框，可以点击其中一个项。只有取消按钮
	 */
	public static void showListDialog(Activity context, String title, String[] items, android.content.DialogInterface.OnClickListener listener)
	{
		// -------对话框------
		Builder bd = new Builder(context);

		// 设置title
		bd.setTitle(title);
		bd.setNegativeButton("取消", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		bd.setItems(items, listener);
		bd.show();
	}

	/**
	 * 格式化时间
	 */
	private static String getAddZeroTime(int time)
	{
		String strTime;
		if (time < 10) {
			strTime = "0" + time;
		} else {
			strTime = String.valueOf(time);
		}
		return strTime;
	}

}
