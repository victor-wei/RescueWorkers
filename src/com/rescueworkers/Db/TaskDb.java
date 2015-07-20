package com.rescueworkers.Db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rescueworkers.Settings;
import com.rescueworkers.dto.Task;

/**
 * @author Administrator
 *
 */
public class TaskDb {

	/**
	 * 只进行新数据插入操作
	 */
	public static boolean saveNotFinishTask(Task task) {
		boolean success = false;
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		if (task == null) {
			return false;
		}
		if (getTaskById(task.id) != null) {
			// 如果本地存在此task的相关数据，以本地数据为准
			success = true;
		} else {
			try {
				task.uploadFlag = 1;
				db.insertOrThrow("taskInfo", null, task.fromData());
				success = true;
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			}
		}
		return success;
	}

	/**
	 * 根据id获取task
	 * 
	 * @param id
	 * @return
	 */
	private static Task getTaskById(String id) {
		Task task = null;
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("taskInfo", null, "id = ?",
				new String[] { id }, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToNext()) {
					task = new Task();
					task.fromCusor(cursor);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return task;
	}
	
	public static List<Task> getAllTaskList(String uploadFlag) {
		
		List<Task> taskList = new ArrayList<Task>();
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("taskInfo", null, "uploadFlag = ?",
				new String[] { uploadFlag }, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Task task = new Task();
					task.fromCusor(cursor);
					taskList.add(task);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return taskList;
		
	}
	public static List<Task> getTaskList(String status ,int uploadFlag) {

		List<Task> taskList = new ArrayList<Task>();
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("taskInfo", null, "status = ?",
				new String[] { status }, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Task task = new Task();
					task.fromCusor(cursor);
					taskList.add(task);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return taskList;
	
	}
	
	/**获取未完成或未上传成功的task
	 * @param status
	 * @param cancelStatus
	 * @param uploadFlag
	 * @return
	 */
	public static List<Task> getNotFinishOrNotUploadTaskList(String status, String cancelStatus,String uploadFlag) {

		List<Task> taskList = new ArrayList<Task>();
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("taskInfo", null, "(status != ? and status != ?) or uploadFlag = ?",
				new String[] { status ,cancelStatus , uploadFlag}, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Task task = new Task();
					task.fromCusor(cursor);
					taskList.add(task);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return taskList;
	
	}
	
	/**更新taskInfo表
	 * @param taskId
	 * @param status
	 */
	public static void updateTaskInfo(String taskId,ContentValues values) {
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		db.update("taskInfo", values, " id = ?", new String[]{taskId});
	}
	public static void updateTaskInfo(String taskId,String status,String uploadFlag) {
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("status", status);
		values.put("uploadFlag", uploadFlag);
		db.update("taskInfo", values, " id = ?", new String[]{taskId});
	}
	public static void updateTaskUploadInfo(String taskId,String uuid,String uploadFlag) {
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("uploadFlag", uploadFlag);
		db.update("taskInfo", values, " id = ? and _id = ?", new String[]{taskId,uuid});
	}

	/**加入状态更新同步表中
	 * @param task
	 * @return
	 */
	public static boolean addIntoTaskUploadInfo(Task task) {

		boolean success = false;
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		if (task == null) {
			return false;
		}
			try {
				task.uploadFlag = 0;
				db.insertOrThrow("taskUploadInfo", null, task.fromData());
				success = true;
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			}
		return success;
	
	}
	
	public static List<Task> getUnUploadTaskList(String uploadFlag) {

		List<Task> taskList = new ArrayList<Task>();
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("taskUploadInfo", null, "uploadFlag = ?",
				new String[] {  uploadFlag}, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToNext()) {
					Task task = new Task();
					task.fromCusor(cursor);
					taskList.add(task);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return taskList;
	
	}
	public static String getUnUploadTask(String uploadFlag) {
		
		Task task = null;
		SQLiteDatabase db = Settings.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("taskUploadInfo", null, "uploadFlag = ?",
				new String[] {  uploadFlag}, null, null, null);
		JSONArray jsonArray = new JSONArray();
		try {
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					task = new Task();
					task.fromCusor(cursor);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", cursor.getString(0));
					jsonObject.put("status", cursor.getString(1));
					jsonObject.put("operateTime", cursor.getString(2));
					jsonObject.put("uploadFlag", cursor.getInt(3));
					jsonArray.put(jsonObject);
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
