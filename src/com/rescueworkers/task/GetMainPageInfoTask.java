package com.rescueworkers.task;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.JsonUtils;
import com.fg114.main.util.SessionManager;
import com.rescueworkers.Settings;
import com.rescueworkers.dto.MainPageInfoDTO;

/**
 *
 */
public class GetMainPageInfoTask extends BaseTask {

	public MainPageInfoDTO dto;
	private Runnable onMainPageInfoChanged;
	private String latitude;
	private String longitude;
	private String acquisition_at;
	
	public GetMainPageInfoTask(
					String preDialogMessage, 
					Context context,
					Runnable onMainPageInfoChanged,String latitude,String longitude,String acquisition_at) {
		super(preDialogMessage, context);
		this.onMainPageInfoChanged=onMainPageInfoChanged;
		this.latitude = latitude;
		this.longitude = longitude;
		this.acquisition_at = acquisition_at;
	}

	@Override
	public JsonPack getData() throws Exception {
		
		if(!Settings.DEBUG){
			String token=SessionManager.getInstance().getUserInfo(context).token;
			MainPageInfoDTO dto=SessionManager.getInstance().getMainPageInfo();
			return A57HttpApiV3.getInstance().getMainPageInfo(token,dto.getTimestamp(), latitude, longitude, acquisition_at);
		}else{
			return getTestData();
		}
	}

	@Override
	public void onPreStart() {
		
	}
	
	@Override
	protected void onPostExecute(JsonPack result) {
		super.onPostExecute(result);
		closeProgressDialog();
	}

	@Override
	public void onStateFinish(JsonPack result) {
		if (result.getObj() != null) {
			dto = JsonUtils.fromJson(result.getObj().toString(), MainPageInfoDTO.class);
			if(dto!=null){
				//保护“未到原因”不为空//保护一下，免得被刷成空列表
				SessionManager.getInstance().setMainPageInfo(dto);
				onMainPageInfoChanged.run();		
			}
		}
	}

	private JsonPack getTestData() throws JSONException{
		//---------------------------测试数据
		SystemClock.sleep(2000);
		String data="{\"timestamp\":\"3421134234\",\"needUpdateTag\":\"true\",\"taskNum\":\""+new Random().nextInt(20)+"\",\"notArrivalReasonList\":[{\"uuid\":\"111\",\"name\":\"生病未到\"},{\"uuid\":\"222\",\"name\":\"没赶上飞机\"}]}";
		JsonPack result=new JsonPack();
		result.setObj(new JSONObject(data));
		//-----------------------------------
		return result;
	}

	@Override
	public void onStateError(JsonPack result) {
		
	}
}
