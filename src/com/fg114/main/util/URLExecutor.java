package com.fg114.main.util;

import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;

/*
 * 处理url匹配的工具类
 * 内部链接导航的地址  
 * xms://promotions   (优惠页面) 
 * xms://restaurant/<rest's UUID>  (餐厅详情) 
 * xms://search   (全部内容搜索)
 * xms://order/{订单id} (跳转到订单)
 * xms://download/{下载链接} (转到软件下载)
 * 
 * xms://custom/pagename?aa=1&bb=2  (可配置的自定义url)
 * 自定义url的Key对应的参数类型约定，以前缀为标识:i_xxx-Integer, s_xxx-String, b_xxx-Boolean, d_xxx-Double
 * 
 * xms://restaurant/jump/<rest's UUID>/<下一子页面> (转到餐厅详情下的某个子页面)
 * 地图  map
 * 餐厅图片列表 picture
 * 餐馆描述 describe
 * 优惠信息 discount
 * 菜单列表 food
 * 评论列表 comment
 * 
 */
public class URLExecutor{
	
	public static final String NEXT_PAGE_MAP = "map";
	public static final String NEXT_PAGE_PICTURE = "picture";
	public static final String NEXT_PAGE_DESCRIBE = "describe";
	public static final String NEXT_PAGE_DISCOUNT = "discount";
	public static final String NEXT_PAGE_FOOD = "food";
	public static final String NEXT_PAGE_COMMENT = "comment";
	
	private static List<UrlMatcher> list=new ArrayList<UrlMatcher>();
	static {
//		//使用自带浏览器打开页面
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if(url.startsWith("link://")){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				if (context instanceof Activity) {
//					ActivityUtil.jumbToWeb((Activity) context, url.replace("link://", "http://"));
//				}
//			}
//		});
//		//转到餐厅详情下的某个子页面
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if(!TextUtils.isEmpty(url) && url.startsWith("xms://restaurant/jump/")){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				try {
//					int start = url.indexOf("xms://restaurant/jump/") + "xms://restaurant/jump/".length();
//					int end = url.lastIndexOf("/");
//					String restId = ConvertUtil.subString(url, start, end);
//					String page = ConvertUtil.subString(url, end + 1);
//					Bundle bundle = new Bundle();
//					bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);
//					bundle.putString(Settings.BUNDLE_KEY_ID, restId);
//					bundle.putString(Settings.BUNDLE_RES_DETAIL_NEXT_PAGE, page);
//					ActivityUtil.jump(context, RestaurantDetailActivity.class, frompage, bundle);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		//执行随手拍功能
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if("xms://takephoto".equals(url)){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				FunctionPopWindow.jumpToPage(context, Settings.STATUTE_PAGE_CAPTURE, frompage, null);
//			}
//		});
//		//优惠页面
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if("xms://promotions".equals(url)){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				Bundle bundle = new Bundle();
//				bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);				
//				ActivityUtil.jump(context, MealComboListActivity.class, frompage, bundle);
//				
//			}
//		});
//		//全部内容搜索，跳转到搜索列表页
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if("xms://search".equals(url)){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				Bundle bundle = new Bundle();
//				bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);	
//				bundle.putString(Settings.BUNDLE_KEY_LEFT_BUTTON,"返回");
//				ActivityUtil.jump(context, ResAndFoodListActivity.class, frompage, bundle);
//				
//			}
//		});
//		//餐厅详情
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if(url!=null&&url.startsWith("xms://restaurant/")){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				int start=17;
//				Bundle bundle = new Bundle();
//				bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);				
//				bundle.putString(Settings.BUNDLE_KEY_ID, url.substring(start));					
//				ActivityUtil.jump(context, RestaurantDetailActivity.class, frompage, bundle); 
//				
//			}
//		});
//		//跳转到订单详情
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if(url!=null&&url.startsWith("xms://order/")){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				int start=12;
//				Bundle bundle = new Bundle();
//				bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);		
//				bundle.putString(Settings.BUNDLE_KEY_ID, url.substring(start));				
//				ActivityUtil.jump(context, MyOrderDetailActivity.class, frompage, bundle);
//				
//			}
//		});
//		//跳转到现金券详情页
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if(url!=null&&url.startsWith("xms://cashcoupon/")){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				int start=17;
//				Bundle bundle = new Bundle();
//				bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);
//				//格式：restid/couponid
//				String temp=url.substring(start);
//				if(CheckUtil.isEmpty(temp)){
//					return;
//				}
//				String[] data=temp.split("/");		
//				if(data.length!=2){
//					return;
//				}
//				bundle.putString(Settings.BUNDLE_REST_ID, data[0]);				
//				bundle.putString(Settings.UUID, data[1]);				
//				ActivityUtil.jump(context, MealComboDetailActivity.class, frompage, bundle);
//				
//			}
//		});
//		//跳转到下载软件
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if(url!=null&&url.startsWith("xms://download/")){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				int start=15;
//				Bundle bundle = new Bundle();
////				bundle.putString(Settings.BUNDLE_KEY_ID, url.substring(start));		
////				if(Settings.gVersionChkDTO==null||!Settings.gVersionChkDTO.isHaveNewVersionTag()){
////					return;
////				}
////				bundle.putString(Settings.BUNDLE_KEY_CONTENT, Settings.gVersionChkDTO.getDownloadUrl());
//				bundle.putString(Settings.BUNDLE_KEY_CONTENT, url.substring(start));
//				bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);
//				ActivityUtil.jump(context, AutoUpdateActivity.class, frompage, bundle);
//				
//			}
//		});
//		//跳转到自定义页面
//		registerUrlMatcher(new UrlMatcher() {
//			
//			@Override
//			public boolean isMatched(String url,Context context) {
//				if(url!=null&&url.startsWith("xms://custom/")){
//					return true;
//				}
//				return false;
//			}
//			
//			@Override
//			public void doAction(String url,Context context,int frompage) {
//				try {
//					url = ConvertUtil.subString(url, url.lastIndexOf("/") + 1);
//					int start = url.indexOf("/") + 1;
//					int end = url.indexOf("?");
//					String pageName = ConvertUtil.subString(url, start, end);
//					Class<?> cls = Class.forName(pageName);
//					String actionUrl = ConvertUtil.subString(url, start);
//					Bundle bundle = getBundleFromUrl(actionUrl);
//					if (!bundle.containsKey(Settings.BUNDLE_KEY_FROM_PAGE)) {
//						bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE, frompage);
//					}
//					ActivityUtil.jump(context, cls, frompage, bundle);
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}
	private static void registerUrlMatcher(UrlMatcher matcher){
		list.add(matcher);
	}
	/**
	 * 执行一个url，如果有已注册的url处理器，则会执行处理，并且返回true，否则返回false
	 * @param url
	 * @param context
	 * @param frompage
	 * @return 返回true表示有匹配的url处理器
	 */
	public static boolean execute(String url,Context context,int frompage) {
		for(int i=0;i<list.size();i++){
			if(list.get(i).isMatched(url,context)){
				list.get(i).doAction(url,context,frompage);
				return true;
			}
		}
		return false;
	}
	private abstract static class UrlMatcher{
		public abstract boolean isMatched(String url,Context context);
		public abstract void doAction(String url,Context context,int frompage);
	}
	
	/**
	 * 根据url获得自定义跳转的Bundle
	 * @param url
	 * @return
	 */
	private static Bundle getBundleFromUrl(String url) {
		Bundle bundle = new Bundle();
		String params = ConvertUtil.subString(url, url.indexOf("?") + 1);
		String[] paramsArray = params.split("&");
		if (paramsArray == null || paramsArray.length == 0) {
			return bundle;
		}
		for (String param : paramsArray) {
			if (TextUtils.isEmpty(param) || param.indexOf("=") < 1) {
				continue;
			}
			String[] result = param.split("=");
			if (result == null || result.length != 2 || TextUtils.isEmpty(result[1])) {
				continue;
			}
			if(result[0].startsWith("s_")) {
				bundle.putString(ConvertUtil.subString(result[0], result[0].indexOf("s_") + 2), result[1]);
			} 
			else if (result[0].startsWith("i_")) {
				bundle.putInt(ConvertUtil.subString(result[0], result[0].indexOf("i_") + 2), Integer.parseInt(result[1]));
			} 
			else if (result[0].startsWith("b_")) {
				bundle.putBoolean(ConvertUtil.subString(result[0], result[0].indexOf("b_") + 2), Boolean.parseBoolean(result[1]));
			}
			else if (result[0].startsWith("d_")) {
				bundle.putDouble(ConvertUtil.subString(result[0], result[0].indexOf("d_") + 2), Double.parseDouble(result[1]));
			}
			else {
				bundle.putString(result[0], result[1]);
			}
		}
		return bundle;
	}
}