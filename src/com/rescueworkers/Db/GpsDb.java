package com.rescueworkers.Db;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rescueworkers.Settings;
import com.rescueworkers.dto.LocationInfo;

public class GpsDb {

	static int maxNums = 10;// 单次最大上传gps信息条数

	// 保存gps信息
	public static void saveGps(String latitude, String longitude,
			String locationTime) {
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("uuid", UUID.randomUUID().toString());
		values.put("latitude", latitude);
		values.put("longitude", longitude);
		values.put("acquisition_at", locationTime);
		values.put("uploadFlag", 0);
		db.insert("gpsInfo", null, values);
	}

	// 获取未上传的gps信息
	public static String getGpsForUpload() {
		JSONArray jsonArray = new JSONArray();
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		StringBuffer sql = new StringBuffer();
		sql.append(" uploadFlag =  ? ");
		sql.append(" order by acquisition_at asc limit ? offset ?;");
		Cursor cursor = db.query("gpsInfo", null, sql.toString(), new String[] {
				"0", "0", "10" }, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("latitude", cursor.getString(1));
					jsonObj.put("longtitude", cursor.getString(2));
					jsonObj.put("acquisition_at", cursor.getString(3));
					jsonArray.put(jsonObj);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return jsonArray.toString();
	}
}
