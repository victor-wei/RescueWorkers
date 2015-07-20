package com.rescueworkers;

import java.io.File;

import com.rescueworkers.Db.DbHelper;

import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * 系统常量
 * */
public class Settings {
	public static final String[] TEST_ID = {"863061026709913"};
	public static boolean DEBUG = true;
	public static final StringBuffer requestLog = new StringBuffer(2048);
	public static final String KK = "6AA89CD986019BC62B81700CD8346906";
	// ************缓存设置。added by xu jianjun,
	// 2011-11-21。***************************************************
	public static final long CACHE_MEMORY_FILE_TOTAL_SIZE = 1024 * 300; // 控制内存缓存中文件的总字节数上限，不能太大，这里是经过测试后的极限值
	public static final long CACHE_MEMORY_VALUE_TOTAL_SIZE = 1024 * 1024; // 控制内存缓存中文字的总字节数上限
	public static final int CACHE_FILESYSTEM_FILE_MAX_NUMBER = 4000; // 在本地文件系统中可缓存的最大文件数

	public static final String CACHE_DIR_ADVERTISEMENT = "CACHE_DIR_ADVERTISEMENT"; // 存储广告对象
	public static final String CACHE_DIR_ADVERTISEMENT_CLOSED = "CACHE_DIR_ADVERTISEMENT_CLOSED"; // 存储广告对象是否关闭的状态
	public static final String KEY_LOGIN_USERNAME = "login_username";
	public static final String LOGIN_USER_INFO_KEY = "LOGIN_USER_INFO_KEY";
	public static final String MAIN_PAGE_INFO_KEY = "MAIN_PAGE_INFO_KEY";
	public static final String IS_LOGIN_KEY = "IS_LOGIN_KEY";
	public static final String UUID = "UUID";
	public static final String IMAGE_CACHE_DIRECTORY = "spot_check";
	public static final String LOCATION_SET = "location_set";//保存定位信息
	public static String VERSION_NAME = "";
	public static String REST_EC_NAME = "Restecname";
	//
	public static String SELL_CHANNEL_NUM = "1";
	public static String DEV_ID = "";
	public static final String CONFIG_FILE = "Config57"; // 配置信息句柄
	public static final String IS_HAS_DESKTOP_LINK = "IS_HAS_DESKTOP_LINK";
	public static final String IS_AUTO_SHOW_UPDATE_DIALOG = "IS_AUTO_SHOW_UPDATE_DIALOG";
	public static final String UPDATE_VERSION = "UPDATE_VERSION";
	public static final String BUNDLE_KEY_TASK = "BUNDLE_KEY_TASK";
	public static final int CAMERAIMAGE = 1314;

	public static String CURRENT_PAGE;
	public static String ASS_PATH;
    
	public static int workerJobState = 0;// 救援司机任务状态  新任务 0- 已出发 1 - 已到达 2 - 已完成 3 - 已完成资料上传 4
	public static int saveLocationTime = 60 * 1000;//60s保存经纬度信息
	
	public static DbHelper dbHelper ;//数据库
}
