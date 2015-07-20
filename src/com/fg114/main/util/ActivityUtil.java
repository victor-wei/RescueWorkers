package com.fg114.main.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.rescueworkers.R;
import com.rescueworkers.Settings;
import com.rescueworkers.XApplication;


/**
 * 窗体操作
 * @author zhangyifan
 * @email zhangyifan@95171.cn
 * @date 2011.4.13
 */
public class ActivityUtil {

	private static final String TAG = "ActivityUtil";
	private static final boolean DEBUG = Settings.DEBUG;

	private static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	private static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
	private static final String MIME_TYPE_TEXT = "text/plain";
	private static final String MIME_TYPE_EMAIL = "message/rfc822";
	
	/** Network type is unknown */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B*/
    public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0*/
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A*/
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT*/
    public static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    public static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    public static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B*/
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    public static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    public static final int NETWORK_TYPE_HSPAP = 15;
    
    /** No phone radio. */
    public static final int PHONE_TYPE_NONE = 0;
    /** Phone radio is GSM. */
    public static final int PHONE_TYPE_GSM = 1;
    /** Phone radio is CDMA. */
    public static final int PHONE_TYPE_CDMA = 2;
    /** Phone is via SIP. */
    public static final int PHONE_TYPE_SIP = 3;
	
	private static boolean mCancelThread;

	/**
	 * 窗体跳转
	 * @param old
	 * @param cls
	 */
	public static void jump(Context old, Class<?> cls, int requestCode, Bundle mBundle) {
		jump(old, cls, requestCode, mBundle, false);
	}

	/**
	 * 窗体跳转
	 * @param old
	 * @param cls
	 */
	public static void jump(Context old, Class<?> cls, int requestCode, Bundle mBundle, boolean clearTop) {
		Intent intent = new Intent();
		intent.setClass(old, cls);
		if (mBundle != null) {
			intent.putExtras(mBundle);
		}

		Activity activity = (Activity) old;
		if (clearTop) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		activity.startActivityForResult(intent, requestCode);
// activity.overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
		ActivityUtil.overridePendingTransition(activity, R.anim.right_slide_in, R.anim.right_slide_out);
	}

	public static void jump(Context old, Class<?> cls, int requestCode, Bundle mBundle, boolean clearTop, int enterAnim, int exitAnim) {
		Intent intent = new Intent();
		intent.setClass(old, cls);
		if (mBundle != null) {
			intent.putExtras(mBundle);
		}

		Activity activity = (Activity) old;
		if (clearTop) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		activity.startActivityForResult(intent, requestCode);
// activity.overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
		ActivityUtil.overridePendingTransition(activity, enterAnim, exitAnim);
	}

	public static void jump(Context old, Class<?> cls, int requestCode) {
		jump(old, cls, requestCode, null);
	}

	public static void jumpNotForResult(Context old, Class<?> cls, Bundle mBundle, boolean clearTop) {
		Intent intent = new Intent();
		intent.setClass(old, cls);
		if (mBundle != null) {
			intent.putExtras(mBundle);
		}

		Activity activity = (Activity) old;
		if (clearTop) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		activity.startActivity(intent);
// activity.overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
		ActivityUtil.overridePendingTransition(activity, R.anim.right_slide_in, R.anim.right_slide_out);
	}

	/**
	 * 返回
	 * @param old
	 * @param intent
	 */
	public static void back(Context old, Intent intent) {
		Activity activity = (Activity) old;
		activity.setResult(Activity.RESULT_OK, intent);
		activity.finish();
	}

	/**
	 * 添加控件(会删除之前layout所有控件)
	 * @param layout
	 * @param view
	 */
	public static void addViewOnly(ViewGroup layout, View view) {
		try {
			if (layout.getChildCount() > 0) {
				layout.removeAllViews();
			}
			layout.addView(view);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void runInUIThread(Context context, final Toast toast) {
		final Activity activity = (Activity) context;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				toast.show();
			}
		});
	}

	public static Display getWindowDisplay(Context context) {
		return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	/**
	 * 获得设备ID
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String id = telephonyManager.getDeviceId();
		String uuid = SessionManager.getInstance().getUUID(context);
		if (CheckUtil.isEmpty(id)) {
			id = uuid;
		}
		return uuid + "|" + id;
// return id;
	}

	public static String getRealDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String id = telephonyManager.getDeviceId();
		if (CheckUtil.isEmpty(id)) {
			id = SessionManager.getInstance().getUUID(context);
		}
		return id;
	}

	/**
	 * 获得手机型号
	 */
	public static String getDeviceType() {
		return Build.MODEL;
	}

	/**
	 * 获得版本号
	 * @param ctx
	 * @return
	 */
	public static int getVersionCode(Context ctx) {
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			int code = info.versionCode; // 版本号
// Log.d(TAG, "versionCode="+code+", pkg="+info.packageName);
			return code;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
			return 0;
		}
	}

	/**
	 * 获得版本名称
	 * @param ctx
	 * @return
	 */
	public static String getVersionName(Context ctx) {
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
			return "";
		}
	}

	public static float getPX(Context context, int dipValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
	}

	/**
	 * 检测是否连接了网络
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (connectivity != null) {
				NetworkInfo[] infoArray = connectivity.getAllNetworkInfo();
				if (infoArray != null) {
					for (NetworkInfo info : infoArray) {
						if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	//是否开通了gps定位或者网络定位
	public static boolean isGpsAvailable() {
		try {
			LocationManager lm = (LocationManager) ContextUtil.getContext().getSystemService(Context.LOCATION_SERVICE);
			if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) 
					|| lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 系统顶部状态栏消息提示
	 * @param mContext
	 * @return
	 */
	public static NotificationManager getNotificationManager(Context mContext) {
		return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@SuppressWarnings("rawtypes")
	public static Notification buildNotification(Context mContext, String title, String info, Class jumpClass) {
		Notification notification = new Notification(R.drawable.icon, title, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，
		notification.flags |= Notification.FLAG_AUTO_CANCEL; // 表明在点击后，此通知自动清除，
		Intent intent = new Intent(mContext, jumpClass);
		intent.putExtra("info", info);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(mContext, title, info, contentIntent);
		return notification;
	}

	/**
	 * 拨打电话
	 */
	public static void callSuper57(Context context, String phoneNo) {
		// 已经绑定电话的场合
		String number = "tel:" + phoneNo;
		try {
// Intent callIntent = new Intent(Intent.ACTION_CALL);
			Intent callIntent = new Intent(Intent.ACTION_DIAL);
			callIntent.setData(Uri.parse(number));
			context.startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			if (DEBUG)
				Log.e(TAG, "Call failed", e);
		}
	}

	/**
	 * 去系统设置界面
	 */
	public static void gotoSysSetting(Context context) {
		try {
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(settingsIntent);
		} catch (ActivityNotFoundException e) {
			if (DEBUG)
				Log.e(TAG, "Settings failed", e);
		}
	}
	
	/**
	 * 去系统无线设置界面
	 */
	public static void gotoWirelessSettings(Context context) {
		try {
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			context.startActivity(settingsIntent);
		} catch (Exception e) {
			if (DEBUG)
				Log.e(TAG, "Settings failed", e);
		}
	}

	/**
	 * 获得手机Ip
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			for (; en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				for (; enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			if (DEBUG)
				Log.e(TAG, "getLocalIpAddress failed", e);
		}
		return "";
	}



	public static boolean existSDcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 通过uri获得文件名
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromURI(Activity activity, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 获得手机分辨率
	 */
	public static DisplayMetrics getWindowsPixels(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获得手机的宽带和高度像素单位为px
		return dm;
	}

// /**
// * 查看内存使用状况
// * @param clazz
// */
// public static void logHeap(Class clazz) {
// Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
// Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
// Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
// DecimalFormat df = new DecimalFormat();
// df.setMaximumFractionDigits(2);
// df.setMinimumFractionDigits(2);
//
// Log.d("test", "debug. =================================");
// Log.d("test", "debug.heap native: allocated " + df.format(allocated) + "MB of " +
// df.format(available) + "MB (" + df.format(free) + "MB free) in [" +
// clazz.getName().replaceAll("com.myapp.android.","") + "]");
// Log.d("test", "debug.memory: allocated: " + df.format(new
// Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new
// Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new
// Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
// System.gc();
// System.gc();
// }

	/**
	 * 判断是否是联通wcdma
	 */
	public static boolean isWcdma(Activity activity) {
		// 获得手机SIMType
		TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		int nType = tm.getNetworkType();
		int pType = tm.getPhoneType();
		String nOperator = tm.getNetworkOperator();

		if (nOperator.equals("46001")) {
			// 联通的场合
			if (pType == TelephonyManager.PHONE_TYPE_GSM) {
				// gsm的场合
				if (nType == TelephonyManager.NETWORK_TYPE_HSDPA || nType == TelephonyManager.NETWORK_TYPE_HSUPA || nType == TelephonyManager.NETWORK_TYPE_HSPA) {
					// WCDMA的场合
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断WIFI是否打开
	 */
	public static boolean isWifiOpen(Activity activity) {
		// 获得手机SIMType
		WifiManager wm = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		return wm.isWifiEnabled();
	}

// /**
// * 判断应用是否已有快捷方式
// */
// private static boolean isInstallShortcut(Context context) {
// boolean flag = false;
// ContentResolver cr = context.getContentResolver();
// Uri uri;
// if(android.os.Build.VERSION.SDK_INT < 8){
// uri = Uri.parse("content://com.android.launcher.settings/favorites?notify=true");
// }else{
// //2.2以上版本
// uri = Uri.parse("content://com.android.launcher2.settings/favorites?notify=true");
// }
// Cursor c = cr.query(uri, new String[] {"title", "iconResource"},
// "title=?",
// new String[] {context.getString(R.string.app_name)},
// null);
// cr.delete(uri, "title=?", new String[] {context.getString(R.string.app_name)});
// if (c != null) {
// if (c.getCount() > 0) {
// flag = true;
// }
// c.close();
// }
//
// return flag;
//
// }
//
	/**
	 * 添加首页快捷方式
	 */
	public static void setShortCut(Context context) {

		boolean isHasDesktopLink = SharedprefUtil.getBoolean(context, Settings.IS_HAS_DESKTOP_LINK, false);
		if (isHasDesktopLink) {
			return;
		}

		String appName = context.getString(R.string.app_name);
		// 获得所有已安装应用信息
		boolean flag = false;
		int app_id = -1;
		PackageManager p = context.getPackageManager();
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> res = p.queryIntentActivities(i, 0);
		// 获得本应用信息
		for (int k = 0; k < res.size(); k++) {
			if (res.get(k).activityInfo.loadLabel(p).toString().equals(appName)) {
				flag = true;
				app_id = k;
				break;
			}
		}

		if (flag) {
			ActivityInfo ai = res.get(app_id).activityInfo;
			// 快捷方式启动对象
			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
			shortcutIntent.setClassName(ai.packageName, ai.name);
			shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			Intent addShortcut = new Intent(ACTION_ADD_SHORTCUT);
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			// 快捷方式显示名
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
			// 快捷方式icon
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.icon));
			// 不允许重复创建
			addShortcut.putExtra(EXTRA_SHORTCUT_DUPLICATE, false);
			context.sendBroadcast(addShortcut);
			SharedprefUtil.saveBoolean(context, Settings.IS_HAS_DESKTOP_LINK, true);
		}
	}



	/**
	 * 兼容1.6的画面迁移动画方法
	 * @param activity
	 * @param animId1
	 * @param animId2
	 */
	public static void overridePendingTransition(Activity activity, int animId1, int animId2) {
		try {
			Method m = activity.getClass().getSuperclass().getMethod("overridePendingTransition", int.class, int.class);
			m.invoke(activity, animId1, animId2);
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * 判断应用是否已安装
	 * @param context
	 * @param uri
	 * @return
	 */
	public static boolean isAppInstalled(Context context, String uri) {
		PackageManager manager = context.getPackageManager();
		try {
			manager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 调用分享程序
	 * @param activity
	 * @param subject
	 * @param message
	 * @param chooserDialogTitle
	 */
	public static void callShare(Activity activity, String subject, String message, String chooserDialogTitle) throws Exception {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		shareIntent.putExtra(Intent.EXTRA_TEXT, message);
		shareIntent.setType(MIME_TYPE_TEXT);
		Intent intent = Intent.createChooser(shareIntent, chooserDialogTitle);
		activity.startActivity(intent);
	}


	/**
	 * 从SD卡读入
	 * @param context
	 * @param strFileName
	 * @return
	 * @throws Exception
	 */
	public static String readFileFromSD(Context context, String strFileName) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return "";
		}

		String strPath = Environment.getExternalStorageDirectory() + "/" + Settings.IMAGE_CACHE_DIRECTORY + "/";
		File fPath = new File(strPath);
		File fFile = new File(strPath + strFileName);
		if (!fPath.exists() || !fFile.exists()) {
			return "";
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fFile);
			byte[] bData = new byte[fis.available()];
			fis.read(bData);
			return new String(bData, "UTF-8");
		} catch (Exception e) {
			return "";
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 写入SD卡
	 * @param context
	 * @param strContent
	 * @param strFileName
	 * @return
	 * @throws Exception
	 */
	public static boolean writeFileToSD(Context context, String strContent, String strFileName) throws Exception {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		FileOutputStream fos = null;
		try {
			String strPath = Environment.getExternalStorageDirectory() + "/" + Settings.IMAGE_CACHE_DIRECTORY + "/";
			File fPath = new File(strPath);
			File fFile = new File(strPath + strFileName);
			if (!fPath.exists()) {
				fPath.mkdir();
			}
			if (!fFile.exists()) {
				fFile.createNewFile();
			}
			fos = new FileOutputStream(fFile);
			byte[] bytes = strContent.getBytes("UTF-8");
			fos.write(bytes);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 判断是否模拟器
	 * @param context
	 * @return
	 */
	public static boolean isEmulator(Context context) {
		try {
			return Build.MODEL.toLowerCase().indexOf("sdk") > -1 || Build.PRODUCT.toLowerCase().indexOf("sdk") > -1 || Build.BRAND.toLowerCase().indexOf("generic") > -1 || "1".equals(SystemPropertiesProxy.get(context, "ro.kernel.qemu"));
		} catch (Exception e) {
			return false;
		}
	}

	public static String getDevString(Context context) {
		try {
			return "MODEL:" + Build.MODEL.toLowerCase() + "|PRODUCT:" + Build.PRODUCT.toLowerCase() + "|BRAND:" + Build.BRAND.toLowerCase() + "|qemu:" + SystemPropertiesProxy.get(context, "ro.kernel.qemu");
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isTestDev(Context context) {
		try {
			boolean isTest = false;
			String devId = ActivityUtil.getRealDeviceId(context);
//			if(){
//				PackageManager pm=ContextUtil.getContext().getPackageManager();
//				PackageInfo pi=pm.getPackageInfo(ContextUtil.getContext().getPackageName(), 0);
//				pi.applicationInfo.
//			}
			for (String s : Settings.TEST_ID) {
				if (s.equalsIgnoreCase(devId)) {
					isTest = true;
					break;
				}
			}
			//isTest=true;
			return isTest;
		} catch (Exception e) {
			return false;
		}
	}
	public static boolean isDebug(){
		// 测试版
		if (A57HttpApiV3.getInstance().mApiBaseUrl.toLowerCase().startsWith("http://t")||A57HttpApiV3.getInstance().mApiBaseUrl.toLowerCase().startsWith("https://t")) {
			return true;
		}else{
			return false;
		}
	}
//	public static void jumpToDishList(Context context, int requestCode, Bundle bundle) {
//		DishOrderDTO dishOrder = SessionManager.getInstance().getDishOrder(context, resId);
//		if (dishOrder.getTimeStamp() + Settings.DISH_EXPIRED_TIME < System.currentTimeMillis()) {
//			dishOrder.reset();
//			SessionManager.getInstance().setDishOrder(context, dishOrder);
//		}
//// bundle.putInt(Settings.BUNDLE_DISH_SRC_PAGE, Settings.CAPTURE_ACTIVITY);
//		ActivityUtil.jump(context, DishListActivity.class, requestCode, bundle);
//	}

	/**
	 * 从本地选取图片，应处理onActivityResult，示例： protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { //获得图片的真实地址 String path = getPathByUri(this, data.getData()); }
	 * @param activity
	 * @param requestCode
	 */
	public static void pickImage(Activity activity, int requestCode) throws Exception {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
// intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 调用拍照程序拍摄图片，返回图片对应的Uri，应处理onActivityResult
	 * ContentResolver的insert方法会默认创建一张空图片，如取消了拍摄，应根据方法返回的Uri删除图片
	 * @param activity
	 * @param requestCode
	 * @param fileName
	 * @return
	 */
	public static Uri captureImage(Activity activity, int requestCode, String fileName, String desc) throws Exception {
		// 设置文件参数
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION, desc);
		// 获得uri
		Uri imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		activity.startActivityForResult(intent, requestCode);
		return imageUri;
	}

	/**
	 * 通过地址跳转到网页
	 * @param activity
	 * @param url
	 */
	public static void jumbToWeb(Activity activity, String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			activity.startActivity(intent);
			ActivityUtil.overridePendingTransition(activity, R.anim.right_slide_in, R.anim.right_slide_out);
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtil.showToast(activity, "抱歉，无法打开链接");
		}
	}

	/**
	 * 获得应用是否在前台
	 */
	public static boolean isOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public static void saveException(Throwable ex) {
		try {
			if (XApplication.crashHandler != null) {
				XApplication.crashHandler.saveException(ex);
			}
		} catch (Exception e) {

		}
	}

	public static void saveException(Throwable ex, String msg) {
		try {
			if (XApplication.crashHandler != null) {
				XApplication.crashHandler.saveException(ex, msg);
			}
		} catch (Exception e) {

		}
	}

	public static void saveOutOfMemoryError(OutOfMemoryError e) {
		saveException(e, "Collect OutOfMemoryError");
	}

	public static String getChannelId(Context context) {
		Settings.SELL_CHANNEL_NUM ="1";
		return Settings.SELL_CHANNEL_NUM;
		// 3.1.32 改为固定取包里的渠道号文件ch，不再取缓存，使得更新时渠道号为新包内的渠道号
//		// 读取渠道号
//		BufferedReader br = null;
//		try {
//			String ch = "1";
//// String ch = SharedprefUtil.get(context, Settings.KEY_CHANNEL_NUM, "");
//// if (ch.equals("")) {
//			br = new BufferedReader(new InputStreamReader(context.getAssets().open("ch")));
//			ch = br.readLine();
//			SharedprefUtil.save(context, Settings.KEY_CHANNEL_NUM, ch);
//			Settings.SELL_CHANNEL_NUM = ch;
//// }
//			return ch;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return "";
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}


	public interface OnRecognizedFinishListener {
		public void onRecognizedFinish(String text);
	}


	
	public static String getDnsInfo() {
		String result = "unknown";
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method method = SystemProperties.getMethod("get", new Class[] { String.class });
			ArrayList<String> servers = new ArrayList<String>();
			for (String name : new String[] { "net.dns1", "net.dns2", "net.dns3", "net.dns4", }) {
			    String value = (String) method.invoke(null, name);
			    if (value != null && !"".equals(value) && !servers.contains(value))
			        servers.add(value);
			}
			StringBuffer sb = new StringBuffer();
			for (String s : servers) {
				sb.append(s).append(";");
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getNetworkInfo() {
		String result = "unknown";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) ContextUtil.getContext().getSystemService(Context.TELEPHONY_SERVICE);
			StringBuffer sb = new StringBuffer();
			sb.append("NetworkCountryIso=").append(telephonyManager.getNetworkCountryIso()).append(";");
			sb.append("NetworkOperator=").append(telephonyManager.getNetworkOperator()).append(";");
			sb.append("NetworkOperatorName=").append(telephonyManager.getNetworkOperatorName()).append(";");
			sb.append("NetworkType=").append(getNetworkTypeName(telephonyManager.getNetworkType())).append(";");
			sb.append("PhoneType=").append(getPhoneTypeName(telephonyManager.getPhoneType())).append(";");
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static String getNetworkTypeName(int type) {
		switch (type) {
		case NETWORK_TYPE_GPRS:
			return "GPRS";
		case NETWORK_TYPE_EDGE:
			return "EDGE";
		case NETWORK_TYPE_UMTS:
			return "UMTS";
		case NETWORK_TYPE_HSDPA:
			return "HSDPA";
		case NETWORK_TYPE_HSUPA:
			return "HSUPA";
		case NETWORK_TYPE_HSPA:
			return "HSPA";
		case NETWORK_TYPE_CDMA:
			return "CDMA";
		case NETWORK_TYPE_EVDO_0:
			return "CDMA - EvDo rev. 0";
		case NETWORK_TYPE_EVDO_A:
			return "CDMA - EvDo rev. A";
		case NETWORK_TYPE_EVDO_B:
			return "CDMA - EvDo rev. B";
		case NETWORK_TYPE_1xRTT:
			return "CDMA - 1xRTT";
		case NETWORK_TYPE_LTE:
			return "LTE";
		case NETWORK_TYPE_EHRPD:
			return "CDMA - eHRPD";
		case NETWORK_TYPE_IDEN:
			return "iDEN";
		case NETWORK_TYPE_HSPAP:
			return "HSPA+";
		default:
			return "UNKNOWN";
		}
	}

	private static String getPhoneTypeName(int type) {
		switch (type) {
		case PHONE_TYPE_NONE:
			return "NONE";
		case PHONE_TYPE_GSM:
			return "GSM";
		case PHONE_TYPE_CDMA:
			return "CDMA";
		case PHONE_TYPE_SIP:
			return "SIP";
		default:
			return "UNKNOWN";
		}
	}
	
	/**
	 * 调用发送电子邮件程序
	 * @param activity
	 * @param address
	 * @param subject
	 * @param body
	 */
	public static void callEmail(Activity activity, String address, String subject, String body) throws Exception {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.setType(MIME_TYPE_EMAIL);
		activity.startActivity(intent);
	}
	
	/**
	 * 发送短信
	 * @param context
	 * @param phone 电话号码
	 * @param content 短信内容
	 */
	public static void sendSMS(Context context, String phone, String content) throws Exception {
		phone = "smsto:" + phone;
		Uri uri = Uri.parse(phone);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", content);
		context.startActivity(intent);
	}
	
	public static String getCurrentNetWork() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) ContextUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] infoArray = connectivity.getAllNetworkInfo();
				if (infoArray != null) {
					for (NetworkInfo info : infoArray) {
						if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
							StringBuffer sbInfo = new StringBuffer();
							if (!TextUtils.isEmpty(info.getTypeName())) {
								sbInfo.append(info.getTypeName());
							}
							if (!TextUtils.isEmpty(info.getSubtypeName())) {
								sbInfo.append(" ").append(info.getSubtypeName());
							}
							if (!TextUtils.isEmpty(info.getExtraInfo())) {
								sbInfo.append(" ").append(info.getExtraInfo());
							}
							if (TextUtils.isEmpty(sbInfo.toString())) {
								sbInfo.append("unknown");
							}
							return sbInfo.toString();
						}
					}
				}
			}
			return "unknown";
		} catch (Exception e) {
			return "unknown";
		}
	}
	
	/**
	 * 使设备震动
	 */
	public static void vibrate(long milliseconds) {
		try {
			Vibrator vibrator = (Vibrator) ContextUtil.getContext().getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(milliseconds);
		} catch (Exception e) {
			LogUtils.logE(TAG, e);
		}
	}
	/**
	 * 退出应用
	 */
	public static void exitApp(Activity activity) {
		XApplication app = (XApplication) activity.getApplication();
		// app.onTerminate();
		// System.exit(0);
		activity.finish();
	}
}
