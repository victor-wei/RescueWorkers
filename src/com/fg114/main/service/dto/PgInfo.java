package com.fg114.main.service.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 分页信息
 * @author qianjiefeng
 *
 */
public class PgInfo {
	//总记录数量
    private int totalNum = 0;
    //页面大小
    private int pageSize = 0;
    //当前页
    private int pageNo = 0;
    //总计多少页
    private int sumPage = 0;
    //是否是第一页
    private boolean firstTag = false;
    //是否是最后一页
    private boolean lastTag = false;

    
	//get,set-------------------------------------------------------------------
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getSumPage() {
		return sumPage;
	}
	public void setSumPage(int sumPage) {
		this.sumPage = sumPage;
	}
	public boolean isFirstTag() {
		return firstTag;
	}
	public void setFirstTag(boolean firstTag) {
		this.firstTag = firstTag;
	}
	public boolean isLastTag() {
		return lastTag;
	}
	public void setLastTag(boolean lastTag) {
		this.lastTag = lastTag;
	} 
	
	/**
	 * json to bean
	 * @param jObj
	 * @return
	 */
	public static PgInfo toBean(JSONObject jObj) {
		
		PgInfo dto = new PgInfo();

		try {

			if (jObj.has("totalNum")) {
				dto.setTotalNum(jObj.getInt("totalNum"));
			}
			if (jObj.has("pageSize")) {
				dto.setPageSize(jObj.getInt("pageSize"));
			}
			if (jObj.has("pageNo")) {
				dto.setPageNo(jObj.getInt("pageNo"));
			}
			if (jObj.has("sumPage")) {
				dto.setSumPage(jObj.getInt("sumPage"));
			}
			if (jObj.has("firstTag")) {
				dto.setFirstTag(jObj.getBoolean("firstTag"));
			}
			if (jObj.has("lastTag")) {
				dto.setLastTag(jObj.getBoolean("lastTag"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return dto;
	}
}
