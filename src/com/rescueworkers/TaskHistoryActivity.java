package com.rescueworkers;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.fg114.main.service.MyLocation;
import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.JsonUtils;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.ViewUtils;
import com.rescueworkers.Db.TaskDb;
import com.rescueworkers.adapter.TaskAdapter;
import com.rescueworkers.dto.Task;
import com.rescueworkers.dto.TaskListDTO;

public class TaskHistoryActivity extends MainFrameActivity {

	private View contextView;
	private TextView start_date;
	private TextView end_date;
	private Button search_button;
	private long start;
	private long end;
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
		tvTitle.setText("任务查询");
		btnGoBack.setVisibility(View.VISIBLE);

		// 内容部分
		contextView = View.inflate(this, R.layout.task_history, null);
		start_date = (TextView) contextView.findViewById(R.id.start_date);
		end_date = (TextView) contextView.findViewById(R.id.end_date);
		search_button = (Button) contextView.findViewById(R.id.search_button);
		listview = (ListView) contextView.findViewById(R.id.listview);
		no_info = (TextView) contextView.findViewById(R.id.no_info);
		no_info.setVisibility(View.GONE);
		start_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				showDateDialog(v);
			}
		});
		end_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				showDateDialog(v);
			}
		});
		search_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				excuteTask();
			}
		});
		// --
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		setDate(end_date, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
		date.add(Calendar.HOUR, -7*24);
		setDate(start_date, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
		mainLayout.addView(contextView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//---
		adapter=new TaskAdapter(this,new Runnable() {
			@Override
			public void run() {
				excuteTask();
			}
		});
		listview.setAdapter(adapter);
	}

	private void excuteTask() {
		int[] start = (int[]) start_date.getTag();
		int[] end = (int[]) end_date.getTag();
		Calendar date1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		Calendar date2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		date1.clear();
		date2.clear();
		date1.set(start[0], start[1] - 1, start[2], 0, 0, 0);
		date2.set(end[0], end[1] - 1, end[2], 23, 59, 59);
		if(!checkInput(date1,date2)){
			return;
		}
		MyLocation myLocation = MyLocation.getInstance();
		String latitude = "";
		String longitude = "";
		String locationTime = "";
		if (myLocation != null) {
			latitude = myLocation.getLatitude() + "";
			longitude = myLocation.getLongitude() + "";
			locationTime = myLocation.getLocationTime();
		}
		final TaskListTask task = new TaskListTask("正在查询...", this, date1.getTimeInMillis(), date2.getTimeInMillis());
		task.execute(new Runnable() {

			@Override
			public void run() {
				if (task.dto == null || task.dto.worker_jobs == null || task.dto.worker_jobs.size() == 0) {
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

	private void showDateDialog(final View v) {
		int[] data = (int[]) v.getTag();
		DatePickerDialog d = new DatePickerDialog(this, new MyDateSetListener((TextView) v), data[0], data[1] - 1, data[2]);
		d.show();
	}

	private class MyDateSetListener implements OnDateSetListener {
		TextView v;

		public MyDateSetListener(TextView v) {
			this.v = v;
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			setDate(v, year, monthOfYear, dayOfMonth);
		}
	}

	private void setDate(TextView v, int year, int monthOfYear, int dayOfMonth) {
		v.setText(year + "-" + getAddZeroTime(monthOfYear + 1) + "-" + getAddZeroTime(dayOfMonth));
		v.setTag(new int[] { year, monthOfYear + 1, dayOfMonth });
	}

	private static String getAddZeroTime(int time) {
		String strTime;
		if (time < 10) {
			strTime = "0" + time;
		} else {
			strTime = String.valueOf(time);
		}
		return strTime;
	}

	// --------------------------------------------------------------------------------------------
	class TaskListTask extends BaseTask {

		public TaskListDTO dto;
		private long start_date;
		private long end_date;

		public TaskListTask(String preDialogMessage, Context context, long start_date, long end_date) {
			super(preDialogMessage, context);
			this.start_date = start_date;
			this.end_date = end_date;
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
			if(!Settings.DEBUG){
				String token = SessionManager.getInstance().getUserInfo(context).token;
				JsonPack result = A57HttpApiV3.getInstance().getTaskList(token, start_date, end_date, latitude, longitude, locationTime);
				//将未完成任务数据存入数据库中，如果数据库中已经有了此条数据，则不更新
				if (result.getObj() != null) {
					TaskListDTO taskList = JsonUtils.fromJson(result.getObj().toString(),
							TaskListDTO.class);
					for(int i=0;i<taskList.worker_jobs.size();i++){
						taskList.worker_jobs.get(i).uploadFlag = 1;
						TaskDb.saveNotFinishTask(taskList.worker_jobs.get(i));
					}
				}
				result.setResultObj(TaskDb.getAllTaskList("1"));
				return result;
			}else{
				return getTestData();
			}
		}

		@Override
		public void onPreStart() {

		}

		@Override
		public void onStateFinish(JsonPack result) {
			closeProgressDialog();
			if (result.getResultObj() != null) {
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
			String data = "{\"worker_jobs\":[{\"id\":1000,\"no\":\"CS150120081446836\",\"customer_name\":\"朱军超\",\"address\":\"北京市-西城区\",\"car_vin\":\"\",\"car_model\":\"雪佛兰SGM7121MT轿车\",\"car_color\":\"\",\"type\":\"接电服务\",\"status\":\"已完成\",\"state\":\"1\"},{\"id\":1470,\"no\":\"CS150119203746220\",\"customer_name\":\"万毅\",\"address\":\"北京市-海淀区军事博物馆往东茂林居\",\"car_vin\":\"\",\"car_model\":\"福克斯CAF7180M轿车\",\"car_color\":\"\",\"type\":\"接电服务\",\"status\":\"已完成\",\"state\":\"1\"}]}";
			JsonPack result = new JsonPack();
			result.setObj(new JSONObject(data));
			// -----------------------------------
			if (result.getObj() != null) {
				TaskListDTO taskList = JsonUtils.fromJson(result.getObj().toString(),
						TaskListDTO.class);
				for(int i=0;i<taskList.worker_jobs.size();i++){
					taskList.worker_jobs.get(i).uploadFlag = 1;
					TaskDb.saveNotFinishTask(taskList.worker_jobs.get(i));
				}
			}
			result.setResultObj(TaskDb.getAllTaskList("1"));
			return result;
		}
	}
	//----------------------------------------
	private boolean checkInput(Calendar date1, Calendar date2) {

		
		if ((date2.getTimeInMillis()-date1.getTimeInMillis())<0) {
			DialogUtil.showToast(this, "起始日期必须小于截止日期");
			return false;
		}

		return true;
	}
}
