package com.rescueworkers.dto;

import com.fg114.main.service.dto.BaseDTO;

/**
 *
 */
public class MainPageInfoDTO extends BaseDTO {
    //今日任务数量
	public int taskNum;
	//新版本逻辑
	public boolean hasNewVersion; //是否有新版本
	public String newVersionCode; //新版本号
	public String newVersionUrl; //新版本升级url地址
	
	
}
