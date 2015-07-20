package com.fg114.main.service.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通用类别DTO
 * @author qianjiefeng
 *
 */
public class CommonTypeDTO  {
	//类别ID 
	public String uuid = "";
	//类别名称
	public String name = "";
	
	public String toString(){
		return name;
	}
}
