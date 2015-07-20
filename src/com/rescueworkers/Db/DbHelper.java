package com.rescueworkers.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		upgradeDataBaseToV1(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private void upgradeDataBaseToV1(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table if not exists  gpsInfo(")
		.append(" _id integer PRIMARY KEY,")
		.append(" uuid text,latitude text,longitude text,acquisition_at integer ,uploadFlag integer)");
		db.execSQL(sql.toString());
		//任务信息表
		sql = new StringBuffer();
		sql.append("create table if not exists  taskInfo(")
		.append(" _id integer PRIMARY KEY,")
		.append(" id text,no text,customerName text,customerPhone text,address text,carVin text,carModel text,carColor text,type text,status text,state text,uploadFlag integer)");
		db.execSQL(sql.toString());
		//任务信息状态表
		sql = new StringBuffer();
		sql.append("create table if not exists  taskUploadInfo(")
		.append(" _id integer PRIMARY KEY,")
		.append(" uuid text,id text,no text,customerName text,customerPhone text,address text,carVin text,")
		.append("carModel text,carColor text,type text,status text,state text,")
		.append("createdTime integer,latitude text,longitude text,uploadFlag integer)");
		db.execSQL(sql.toString());
		//任务照片或录音状态表
		sql = new StringBuffer();
		sql.append("create table if not exists  taskImageOrVoiceInfo(")
		.append(" _id integer PRIMARY KEY,")
		.append(" id text,no text,uploadFlag integer)");
		db.execSQL(sql.toString());
	}
}
