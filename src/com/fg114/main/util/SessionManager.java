package com.fg114.main.util;

import java.util.Set;
import java.util.UUID;





import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.google.xiaomishujson.Gson;
import com.rescueworkers.Settings;
import com.rescueworkers.dto.LocationInfo;
import com.rescueworkers.dto.MainPageInfoDTO;
import com.rescueworkers.dto.UserInfoDTO;

/**
 * Session管理
 * 
 * @author zhangyifan
 * 
 */
public class SessionManager {
	
	
	private static SessionManager instance;
	// 用户登录信息
	private UserInfoDTO userInfo;
	private MainPageInfoDTO mainPageInfo;
	private LocationInfo locationSet;
	
	public LocationInfo getLocationSet() {
		return locationSet;
	}
	public String getLocationInfo(){
		return  SharedprefUtil.get(ContextUtil.getContext(), Settings.LOCATION_SET, "{}");
	}
	

	public void setLocationSet(LocationInfo locationSet) {
		if(locationSet == null){
			SharedprefUtil.resetByKey(ContextUtil.getContext(), Settings.LOCATION_SET);
		}
		this.locationSet = locationSet;
		String json = new Gson().toJson(locationSet);
		String oldLocation = SharedprefUtil.get(ContextUtil.getContext(), Settings.LOCATION_SET, "");
		JSONArray array;
		try {
			JSONObject jsonObject = new JSONObject(json);
			if(CheckUtil.isEmpty(oldLocation)){
				array = new JSONArray();
			}else{
				array = new JSONArray(oldLocation);
			}
			array.put(jsonObject);
			SharedprefUtil.save(ContextUtil.getContext(), Settings.LOCATION_SET, array.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private SessionManager() {

	}

	public static synchronized SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}



	/**
	 * 从session中获得用户信息
	 */

	public UserInfoDTO getUserInfo(Context ctx) {
		try {
			if (!SessionManager.getInstance().isUserLogin(ctx)) {
				return new UserInfoDTO();
			}
			if (userInfo == null) {
				userInfo = new UserInfoDTO();
				String jsonUserInfo = SharedprefUtil.get(ctx, Settings.LOGIN_USER_INFO_KEY, "{}");
				userInfo = JsonUtils.fromJson(jsonUserInfo, UserInfoDTO.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}
	/**
	 * 将用户信息存入session中
	 * 
	 * @param ctx
	 * @param userInfo
	 */
	public void setUserInfo(Context ctx, UserInfoDTO userInfo) {

		this.userInfo = userInfo;
		String jsonUserInfo = new Gson().toJson(userInfo);
		SharedprefUtil.save(ctx, com.rescueworkers.Settings.LOGIN_USER_INFO_KEY, jsonUserInfo);
	}

	/**
	 * 从session中获得MainPageInfoDTO
	 */
	
	public MainPageInfoDTO getMainPageInfo() {
		try {
			if (mainPageInfo == null) {
				mainPageInfo = new MainPageInfoDTO();
				String jsonUserInfo = SharedprefUtil.get(ContextUtil.getContext(), Settings.MAIN_PAGE_INFO_KEY, "{}");
				mainPageInfo = JsonUtils.fromJson(jsonUserInfo, MainPageInfoDTO.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPageInfo;
	}
	
	/**
	 * 将MainPageInfoDTO信息存入session中
	 * 
	 * @param ctx
	 * @param userInfo
	 */
	public void setMainPageInfo(MainPageInfoDTO mainPageInfo) {
		
		this.mainPageInfo = mainPageInfo;
		String json = new Gson().toJson(mainPageInfo);
		SharedprefUtil.save(ContextUtil.getContext(), Settings.MAIN_PAGE_INFO_KEY, json);
	}
	
	/**
	 * 从session中获得用户登录状态
	 */
	public boolean isUserLogin(Context ctx) {
		return SharedprefUtil.getBoolean(ctx, Settings.IS_LOGIN_KEY, false);
	}

	/**
	 * 设置用户登录状态存入session中
	 * 
	 * @param ctx
	 * @param userInfo
	 */
	public void setIsUserLogin(Context ctx, boolean isLogin) {
		SharedprefUtil.saveBoolean(ctx, Settings.IS_LOGIN_KEY, isLogin);
	}


	/**
	 * 获得UUID
	 * 
	 * @param context
	 * @return
	 */
	public String getUUID(Context context) {
		String uuid = "";
		try {
			if (ActivityUtil.existSDcard()) {
				// SD卡可用时操作SD卡
				
				// 检查SD卡中是否有UUID
				uuid = ActivityUtil.readFileFromSD(context, Settings.UUID);
				if (CheckUtil.isEmpty(uuid)) {
					// SD卡中没有时检查缓存中是否有UUID，缓存中存在的情况下使用缓存中的UUID
					uuid = SharedprefUtil.get(context, Settings.UUID, "");
					// 同时写入SD卡备用，下次获取uuid时使用SD卡中的uuid
					ActivityUtil.writeFileToSD(context, uuid, Settings.UUID);
					
					if (CheckUtil.isEmpty(uuid)) {
						// 缓存中也不存在的情况下创建新的UUID
						uuid = UUID.randomUUID().toString();
						ActivityUtil.writeFileToSD(context, uuid, Settings.UUID);
						// 同时写入缓存备用，下次SD卡拔出时，如应用还未卸载，则可使用此uuid
						SharedprefUtil.save(context, Settings.UUID, uuid);
					}
				} else {
					// SD卡存在UUID时，检查缓存中是否存有UUID
					String uuidInPref = SharedprefUtil.get(context, Settings.UUID, "");
					if (TextUtils.isEmpty(uuidInPref) || !uuidInPref.equals(uuid)) {
						// 如果缓存中不存在UUID，或缓存中的UUID不等于SD卡中的UUID(曾经清除过缓存或卸载过应用，但SD卡还保留了之前的唯一id)
						// 将SD卡中UUID写入缓存，下次SD卡拔出时，如应用还未卸载，则可使用此uuid
						SharedprefUtil.save(context, Settings.UUID, uuid);
					}
				}
			} else {
				// SD卡不可用时操作缓存
				uuid = SharedprefUtil.get(context, Settings.UUID, "");
				if (CheckUtil.isEmpty(uuid)) {
					uuid = UUID.randomUUID().toString();
					SharedprefUtil.save(context, Settings.UUID, uuid);
				}
			}
		} catch (Exception e) {
			// 有异常时使用缓存
			uuid = SharedprefUtil.get(context, Settings.UUID, "");
			if (CheckUtil.isEmpty(uuid)) {
				uuid = UUID.randomUUID().toString();
				SharedprefUtil.save(context, Settings.UUID, uuid);
			}
		}
		return uuid;
	}
}
