package com.rescueworkers.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.SessionManager;
import com.rescueworkers.Settings;
import com.rescueworkers.Db.GpsDb;
import com.rescueworkers.Db.TaskDb;
import com.rescueworkers.dto.Task;

public class UploadGpsInfoTask extends BaseTask{
	private String token;
	private Context context;
	private Task task;
	public UploadGpsInfoTask(String preDialogMessage, Context context,Task task) {
		super(preDialogMessage, context);
		this.token = SessionManager.getInstance().getUserInfo(context).token;
		this.context = context;
		this.task = task;
	}

	@Override
	public JsonPack getData() throws Exception {
		if (!Settings.DEBUG) {
			String gps = GpsDb.getGpsForUpload();
            // 从任务状态切换列表中，获取最多10条信息，组装成一条数据上传
			return A57HttpApiV3.getInstance().postGpsInfo(token, gps);

		} else {
			return getTestData();
		}
	}

	@Override
	public void onStateFinish(JsonPack result) {
		//上传成功后，更新两张表中的上传状态
	}

	@Override
	public void onStateError(JsonPack result) {
		
	}

	@Override
	public void onPreStart() {
		
	}
	
	private JsonPack getTestData() throws JSONException{
		//---------------------------测试数据
		SystemClock.sleep(500);
		String data="{}";
		JsonPack result=new JsonPack();
		result.setObj(new JSONObject(data));
		//-----------------------------------
		return result;
	}

}
