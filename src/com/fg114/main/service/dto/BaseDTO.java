package com.fg114.main.service.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * DTO基类  
 * @author qianjiefeng
 *
 */
public class BaseDTO {
	//是否需要更新
	private boolean needUpdateTag = true;
	//时间戳
	private long timestamp;
	//页面信息  可以为null
	private PgInfo pgInfo = new PgInfo();


	//get,set-------------------------------------------------------------------
	public boolean isNeedUpdateTag() {
		return needUpdateTag;
	}
	public void setNeedUpdateTag(boolean needUpdateTag) {
		this.needUpdateTag = needUpdateTag;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public PgInfo getPgInfo() {
		return pgInfo;
	}
	public void setPgInfo(PgInfo pgInfo) {
		this.pgInfo = pgInfo;
	}
	public void initBase(JSONObject jObj){
		BaseDTO base=toBean(jObj);
		this.needUpdateTag=base.needUpdateTag;
		this.timestamp=base.timestamp;
		this.pgInfo=base.pgInfo;
	}
	/**
	 * json to bean
	 * @param jObj
	 * @return
	 */
	public static BaseDTO toBean(JSONObject jObj) {
		
		BaseDTO dto = new BaseDTO();

		try {

			if (jObj.has("needUpdateTag")) {
				dto.setNeedUpdateTag(jObj.getBoolean("needUpdateTag"));
			}
			if (jObj.has("timestamp")) {
				dto.setTimestamp(jObj.getLong("timestamp"));
			}
			if (jObj.has("pgInfo")) {
				dto.setPgInfo(PgInfo.toBean(jObj.getJSONObject("pgInfo")));
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
			
		}
		return dto;
	}
}
