package com.rescueworkers.dto;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;


/**
 * task
 */
public class Task implements Serializable{
	public String tableName = "task";
	public String _id=""; //任务的id
	public String id=""; //任务的id
	public String no = "";
	public String customer_name=""; //客户姓名
	public String customer_phone=""; //客户手机
	public String address;  //地址
	public String car_vin;
	public String car_model;
	public String car_color;
	public String type;
	public int uploadFlag;//是否已经上传
	/**
	 * 0:新任务
	 * 1:已出发
	 * 2:已到达
	 * 3:已完成
	 * 4:已完成资料上传
	 */
	public String status;
	public String state;
	public String confirmMemo;	//确认备注，最大250字符
	
	public void fromCusor(Cursor cursor) {
		_id = cursor.getString(cursor.getColumnIndex("_id"));
        id = cursor.getString(cursor.getColumnIndex("id"));
        no = cursor.getString(cursor.getColumnIndex("no"));
        customer_name = cursor.getString(cursor.getColumnIndex("customerName"));
        customer_phone = cursor.getString(cursor.getColumnIndex("customerPhone"));
        address = cursor.getString(cursor.getColumnIndex("address"));
        car_vin = cursor.getString(cursor.getColumnIndex("carVin"));
        car_model = cursor.getString(cursor.getColumnIndex("carModel"));
        car_color = cursor.getString(cursor.getColumnIndex("carColor"));
        type = cursor.getString(cursor.getColumnIndex("type"));
        uploadFlag = cursor.getInt(cursor.getColumnIndex("uploadFlag"));
        status = cursor.getString(cursor.getColumnIndex("status"));
        state = cursor.getString(cursor.getColumnIndex("state"));
	}
	
	public ContentValues fromData() {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("no", no);
        values.put("customerName", customer_name);
        values.put("customerPhone", customer_phone);
        values.put("address", address);
        values.put("carVin", car_vin);
        values.put("carModel", car_model);
        values.put("carColor", car_color);
        values.put("type", type);
        values.put("uploadFlag", uploadFlag);
        values.put("status", status);
        values.put("state", state);
        return values;
	}
	
	
}
