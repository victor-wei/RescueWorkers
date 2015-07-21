package com.rescueworkers.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;

import com.fg114.main.service.MyLocation;
import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.JsonUtils;
import com.fg114.main.util.SessionManager;
import com.rescueworkers.Settings;
import com.rescueworkers.Db.TaskDb;
import com.rescueworkers.dto.TaskListDTO;

/**
 *
 */
public class GetNotFinishWorkTask extends BaseTask {

	public TaskListDTO dto;
	private Runnable onMainPageInfoChanged;

	public GetNotFinishWorkTask(String preDialogMessage, Context context,Runnable onMainPageInfoChanged) {
		super(preDialogMessage, context);
		this.onMainPageInfoChanged=onMainPageInfoChanged;
	}

	@Override
	public JsonPack getData() throws Exception {
		MyLocation myLocation = MyLocation.getInstance();
		String latitude = "";
		String longitude = "";
		String locationTime = "";
		if (myLocation != null) {
			latitude = myLocation.getLatitude() + "";
			longitude = myLocation.getLongitude() + "";
			locationTime = myLocation.getLocationTime();
		}
		if (Settings.DEBUG) {
			String token = SessionManager.getInstance().getUserInfo(context).token;
			JsonPack result = A57HttpApiV3.getInstance().getNotFinishWorkList(token,
					latitude, longitude, locationTime);
			//将未完成任务数据存入数据库中，如果数据库中已经有了此条数据，则不更新
			if (result.getObj() != null) {
				TaskListDTO taskList = JsonUtils.fromJson(result.getObj().toString(),
						TaskListDTO.class);
				for(int i=0;i<taskList.worker_jobs.size();i++){
					taskList.worker_jobs.get(i).uploadFlag = 1;
					TaskDb.saveNotFinishTask(taskList.worker_jobs.get(i));
				}
			}
			return result;
		} else {
			return getTestData();
		}
	}

	@Override
	public void onPreStart() {

	}

	@Override
	public void onStateFinish(JsonPack result) {
		closeProgressDialog();
		if (result.getObj() != null) {
			dto = JsonUtils.fromJson(result.getObj().toString(),
					TaskListDTO.class);
			if(dto!=null){
				//保护“未到原因”不为空//保护一下，免得被刷成空列表
//				SessionManager.getInstance().setMainPageInfo(dto);
				onMainPageInfoChanged.run();		
			}
		}
	}

	@Override
	public void onStateError(JsonPack result) {
		closeProgressDialog();
		DialogUtil.showToast(context, result.getMsg());
		if(result != null && result.getRe() == Settings.CODE_TOKEN_INVALIDE){
			//退出app重新登陆
			
		}
	}

	private JsonPack getTestData() throws JSONException {
		// ---------------------------测试数据
		SystemClock.sleep(500);
		String data = "{\"worker_jobs\":[{\"id\":1454,\"no\":\"CS150120081446836\",\"customer_name\":\"朱军超\","
				+ "\"address\":\"北京市-西城区\",\"car_vin\":\"\",\"car_model\":\"雪佛兰SGM7121MT轿车\",\"car_color\":\"\""
				+ ",\"type\":\"接电服务\",\"status\":\"新任务\",\"state\":\"1\"}"
				+ ",{\"id\":1470,\"no\":\"CS150119203746220\",\"customer_name\":\"万毅\",\"address\":\"北京市-海淀区军事博物馆往东茂林居\",\"car_vin\":\"\",\"car_model\":\"福克斯CAF7180M轿车\",\"car_color\":\"\",\"type\":\"接电服务\",\"status\":\"已出发\",\"state\":\"启用\"},{\"id\":1471,\"no\":\"CS150120081446837\",\"customer_name\":\"朱军超\",\"address\":\"北京市-西城区\",\"car_vin\":\"\",\"car_model\":\"雪佛兰SGM7121MT轿车\",\"car_color\":\"\",\"type\":\"接电服务\",\"status\":\"已到达\",\"state\":\"启动\"},{\"id\":1472,\"no\":\"CS150120081446838\",\"customer_name\":\"雷子\",\"address\":\"北京市-西城区\",\"car_vin\":\"\",\"car_model\":\"雪佛兰SGM7121MT轿车\",\"car_color\":\"\",\"type\":\"接电服务\",\"status\":\"已完成\",\"state\":\"1\"},{\"id\":1474,\"no\":\"CS150120081446839\",\"customer_name\":\"马化腾\",\"address\":\"北京市-西城区\",\"car_vin\":\"\",\"car_model\":\"雪佛兰SGM7121MT轿车\",\"car_color\":\"\",\"type\":\"接电服务\",\"status\":\"已完成照片信息上传\",\"state\":\"1\"},{\"id\":1480,\"no\":\"CS150120081446834\",\"customer_name\":\"马云\",\"address\":\"北京市-西城区\",\"car_vin\":\"\",\"car_model\":\"雪佛兰SGM7121MT轿车\",\"car_color\":\"\",\"type\":\"接电服务\",\"status\":\"已取消\",\"state\":\"1\"}]}";
		JsonPack result = new JsonPack();
		result.setObj(new JSONObject(data));
		if (result.getObj() != null) {
			TaskListDTO taskList = JsonUtils.fromJson(result.getObj().toString(),
					TaskListDTO.class);
			for(int i=0;i<taskList.worker_jobs.size();i++){
				taskList.worker_jobs.get(i).uploadFlag = 1;
				TaskDb.saveNotFinishTask(taskList.worker_jobs.get(i));
			}
		}
		// -----------------------------------
		return result;
	}
}
