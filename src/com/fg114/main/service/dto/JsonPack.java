package com.fg114.main.service.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * json包装对象
 * @author qianjiefeng
 *
 */
public class JsonPack {
	//请求是否成功     约定  200：成功  其他：异常
	private int error = 0;
	//异常信息
	private String msg = "";
	//对象 可以为null
	private JSONObject obj = null;
	//对象 可以为null
	private Object resultObj = null;
	public Object getResultObj() {
		return resultObj;
	}
	public void setResultObj(Object resultObj) {
		this.resultObj = resultObj;
	}
	//回调函数
	private Runnable callBack;
	// 调用的url
	private String url = "";
	
	//get,set-------------------------------------------------------------------
	public int getRe() {
		return error;
	}
	public void setRe(int re) {
		this.error = re;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public JSONObject getObj() {
		return obj;
	}
	public void setObj(JSONObject obj) {
		this.obj = obj;
	}
	public Runnable getCallBack() {
		return callBack;
	}
	public void setCallBack(Runnable callBack) {
		this.callBack = callBack;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public static JsonPack toBean(String jsonStr) {
		JsonPack jp = null;
		try {
			JSONObject jObj = new JSONObject(jsonStr);
			jp = new JsonPack();
			if (jObj.has("re")) {
				jp.setRe(jObj.getInt("re"));
			}
			if (jObj.has("msg")) {
				jp.setMsg(jObj.getString("msg"));
			}
			if (jObj.has("obj")) {
				Object obj = jObj.get("obj");
				if (obj instanceof JSONObject) {
        			JSONObject successResultObject = jObj.getJSONObject("obj");  
        			jp.setObj(successResultObject);
        		}
			}
			if (jObj.has("url")) {
				jp.setUrl(jObj.getString("url"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jp;
	}


}
