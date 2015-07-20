package com.rescueworkers;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.JsonUtils;
import com.fg114.main.util.ViewUtils;
import com.rescueworkers.Db.TaskDb;
import com.rescueworkers.adapter.TaskAdapter;
import com.rescueworkers.dto.Task;
import com.rescueworkers.dto.TaskListDTO;

public class TodayTaskActivity extends MainFrameActivity {

	private View contextView;
	private TextView no_info;
	private ListView listview;
	private TaskAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化界面
		initComponent();

	}

	/**
	 * 初始化
	 */
	private void initComponent() {

		// 设置标题栏
		tvTitle.setText("今日任务");
		btnGoBack.setVisibility(View.VISIBLE);
		btnOption.setVisibility(View.VISIBLE);
		btnOption.setText("刷新");

		// 内容部分
		contextView = View.inflate(this, R.layout.today_task, null);
		listview = (ListView) contextView.findViewById(R.id.listview);
		no_info = (TextView) contextView.findViewById(R.id.no_info);
		no_info.setVisibility(View.GONE);

		btnOption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				excuteTask();
			}
		});
		mainLayout.addView(contextView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		// ---
		adapter = new TaskAdapter(this, new Runnable() {
			@Override
			public void run() {
				excuteTask();
			}
		});
		listview.setAdapter(adapter);
	}

	private void excuteTask() {
		final WorkNotFinishTask task = new WorkNotFinishTask("正在读取未完成任务...",
				this);
		task.execute(new Runnable() {

			@Override
			public void run() {
				if (task.dto == null || task.dto.worker_jobs == null
						|| task.dto.worker_jobs.size() == 0) {
					no_info.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					return;
				}
				no_info.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				adapter.setList(task.dto.worker_jobs);
			}
		}, new Runnable() {

			@Override
			public void run() {

			}
		});

	}

	// --------------------------------------------------------------------------------------------
	class WorkNotFinishTask extends BaseTask {

		public TaskListDTO dto;

		public WorkNotFinishTask(String preDialogMessage, Context context) {
			super(preDialogMessage, context);
		}

		@Override
		public JsonPack getData() throws Exception {
			JsonPack result = new JsonPack();
			result.setResultObj(TaskDb.getNotFinishOrNotUploadTaskList("已完成", "已取消", "0"));
			return result;
//			if (!Settings.DEBUG) {
//				String token = SessionManager.getInstance()
//						.getUserInfo(context).token;
//				return A57HttpApiV3.getInstance().getNotFinishWorkList(token,
//						latitude, longitude, locationTime);
//			} else {
//				return getTestData();
//			}
		}

		@Override
		public void onPreStart() {

		}

		@SuppressWarnings("unchecked")
		@Override
		public void onStateFinish(JsonPack result) {
			closeProgressDialog();
			if (result.getObj() != null) {
				dto = JsonUtils.fromJson(result.getObj().toString(),
						TaskListDTO.class);
			}
			if(result.getResultObj() != null){
				dto = new TaskListDTO();
				dto.worker_jobs = (List<Task>) result.getResultObj();
			}
		}

		@Override
		public void onStateError(JsonPack result) {
			closeProgressDialog();
			DialogUtil.showToast(context, result.getMsg());
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
			// -----------------------------------
			return result;
		}
	}

	// ----------------------------------------

	@Override
	protected void onResume() {
		super.onResume();

		excuteTask();

	}
}
