package com.rescueworkers.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fg114.main.service.MyLocation;
import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.SessionManager;
import com.rescueworkers.ArrivalActivity;
import com.rescueworkers.R;
import com.rescueworkers.Settings;
import com.rescueworkers.Db.TaskDb;
import com.rescueworkers.dto.Task;
import com.rescueworkers.dto.TaskListDTO;

/**
 * 任务列表适配器
 * 
 */
public class TaskAdapter extends BaseAdapter {

	private static final String TAG = "TaskAdapter";

	private List<Task> list = null;

	private LayoutInflater mInflater = null;
	private Context context;
	private Runnable refreshListCallBack;

	public TaskAdapter(Context c, Runnable refreshListCallBack) {
		super();
		this.context = c;
		this.refreshListCallBack = refreshListCallBack;
		mInflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {

		public View contentView;
		public TextView tvOrderNo;
		public TextView tvFuwushangName;
		public TextView tvCustomerName;
		public TextView tvCustomerPhone;
		public TextView tvCustomerAddress;
		public TextView tvCarModel;
		public TextView tvCarColor;
		public TextView tvCarVin;
		public TextView tvWorkState;
		public TextView tvUploadState;
		public TextView memo;
		public LinearLayout button_layout;
		public Button btn_work_status_change;
		public Button btn_work_finish_upload_info;
		public Button cancel_button;
		public Task data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item_task_info, null);
			holder.contentView = convertView;
			holder.tvOrderNo = (TextView) convertView
					.findViewById(R.id.tv_order_no);
			holder.tvFuwushangName = (TextView) convertView
					.findViewById(R.id.tv_fuwushang_name);
			holder.tvCustomerName = (TextView) convertView
					.findViewById(R.id.tv_customer_name);
			holder.tvCustomerPhone = (TextView) convertView
					.findViewById(R.id.tv_customer_phone);
			holder.tvCustomerAddress = (TextView) convertView
					.findViewById(R.id.tv_customer_address);
			holder.tvCarModel = (TextView) convertView
					.findViewById(R.id.tv_car_model);
			holder.tvCarColor = (TextView) convertView
					.findViewById(R.id.tv_car_color);
			holder.tvCarVin = (TextView) convertView
					.findViewById(R.id.tv_car_vin);
			holder.tvWorkState = (TextView) convertView
					.findViewById(R.id.tv_work_state);
			holder.tvUploadState = (TextView) convertView
					.findViewById(R.id.tv_upload_condition);
			holder.memo = (TextView) convertView.findViewById(R.id.memo);
			holder.button_layout = (LinearLayout) convertView
					.findViewById(R.id.button_layout);
			holder.btn_work_status_change = (Button) convertView
					.findViewById(R.id.btn_work_status_change);
			holder.btn_work_finish_upload_info = (Button) convertView
					.findViewById(R.id.work_finish_upload_info);
			holder.cancel_button = (Button) convertView
					.findViewById(R.id.cancel_button);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setTag(holder);
		final Task task = (Task) list.get(position);
		holder.data = task;
		// ------------------------------------

		holder.tvOrderNo.setText(task.no);
		holder.tvFuwushangName.setText(task.type);
		holder.tvCustomerPhone.setText(securephoneNumber(task.customer_phone));
		holder.tvCustomerName.setText(task.customer_name);
		holder.tvCarColor.setText(task.car_color);
		holder.tvCarModel.setText(task.car_model);
		holder.tvCarVin.setText(task.car_vin);
		holder.tvCustomerAddress.setText(task.address);
		holder.tvWorkState.setText(task.status);
		if(task.uploadFlag == 1){
			holder.tvUploadState.setText("已上传");
		}else if(task.uploadFlag == 0){
			holder.tvUploadState.setText("未上传");
		}else {
			holder.tvUploadState.setText("未上传");
		}
		holder.memo.setText(task.confirmMemo);
		setWorkState(holder);
		return convertView;
	}

	private String securephoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.length() == 0) {
			return "- -";
		}
		if (phoneNumber.length() > 7) {
			return phoneNumber.substring(0, 3) + "****"
					+ phoneNumber.substring(7);
		}
		return phoneNumber;
	}

	public List<Task> getList() {
		return list;
	}

	public void setList(List<Task> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	public void setWorkState(final ViewHolder holder) {
		holder.button_layout.setVisibility(View.VISIBLE);
		// 按钮状态
		if("新任务".equals(holder.data.status)){
			holder.btn_work_status_change.setVisibility(View.VISIBLE);
			holder.btn_work_status_change.setText("出发");
			holder.btn_work_status_change.setTextColor(0xFF0000FF);
		}else if("已出发".equals(holder.data.status)){
			holder.btn_work_status_change.setVisibility(View.VISIBLE);
			holder.btn_work_status_change.setText("到达");
			holder.btn_work_status_change.setTextColor(0xFF0000FF);
		}else if("已到达".equals(holder.data.status)){
			holder.btn_work_status_change.setVisibility(View.VISIBLE);
			holder.btn_work_status_change.setText("完成");
			holder.btn_work_status_change.setTextColor(0xFF0000FF);
		}else if("已完成".equals(holder.data.status)){
			holder.btn_work_status_change.setVisibility(View.VISIBLE);
			holder.btn_work_status_change.setText("已完成");
			holder.btn_work_status_change.setTextColor(0xFF0000FF);
		}else if("已取消".equals(holder.data.status)){
			holder.btn_work_status_change.setVisibility(View.VISIBLE);
			holder.btn_work_status_change.setText("已取消");
			holder.btn_work_status_change.setTextColor(0xFF0000FF);
		}else {
			holder.button_layout.setVisibility(View.GONE);
		}

		holder.btn_work_finish_upload_info
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bundle bundle = new Bundle();
						bundle.putSerializable(Settings.BUNDLE_KEY_TASK,
								holder.data);
						ActivityUtil.jump(context, ArrivalActivity.class, 1,
								bundle);
					}
				});
		holder.btn_work_status_change.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("新任务".equals(holder.data.status)) {// 已出发操作
					DialogUtil.showComfire(context, "警告", "确定将状态置为已出发吗？",
							new String[] { "确定", "取消" }, new Runnable() {

								@Override
								public void run() {
									 //将数据保存
									 if(holder.data != null){
										 holder.data.uploadFlag = 0;
										 holder.data.status = "已出发";
										 ContentValues values = new ContentValues();
										 values.put("status", holder.data.status);
										 values.put("uploadFlag", holder.data.uploadFlag);
										 TaskDb.updateTaskInfo(holder.data.id, values);
										 TaskDb.addIntoTaskUploadInfo(holder.data);
										 if (refreshListCallBack != null) {
												refreshListCallBack.run();
											}
									 }
//									excuteRescueHaveGoneTask(holder);
								}
							}, new Runnable() {

								@Override
								public void run() {
									// 取消
								}
							});
				} else if ("已出发".equals(holder.data.status)) {// 已到达操作
					DialogUtil.showComfire(context, "警告", "确定将状态置为已到达吗？",
							new String[] { "确定", "取消" }, new Runnable() {

								@Override
								public void run() {
									if(holder.data != null){
										 holder.data.uploadFlag = 0;
										 holder.data.status = "已到达";
										 ContentValues values = new ContentValues();
										 values.put("status", holder.data.status);
										 values.put("uploadFlag", holder.data.uploadFlag);
										 TaskDb.updateTaskInfo(holder.data.id, values);
										 TaskDb.addIntoTaskUploadInfo(holder.data);
										 if (refreshListCallBack != null) {
												refreshListCallBack.run();
											}
									 }
//									excuteRescueArriveTask(holder);
								}
							}, new Runnable() {

								@Override
								public void run() {
									// 取消
								}
							});
				} else if ("已到达".equals(holder.data.status)) {// 已完成操作
					DialogUtil.showComfire(context, "警告", "确定将状态置为已完成吗？",
							new String[] { "确定", "取消" }, new Runnable() {

								@Override
								public void run() {
									 if(holder.data != null){
										 holder.data.uploadFlag = 0;
										 holder.data.status="已完成";
										 ContentValues values = new ContentValues();
										 values.put("status", holder.data.status);
										 values.put("uploadFlag", holder.data.uploadFlag);
										 TaskDb.updateTaskInfo(holder.data.id, values);
										 TaskDb.addIntoTaskUploadInfo(holder.data);
										 if (refreshListCallBack != null) {
											refreshListCallBack.run();
										}
									 }
//									excuteRescueCompleteTask(holder);
								}
							}, new Runnable() {

								@Override
								public void run() {
									// 取消
								}
							});
				}
			}
		});
		// 中途取消
		holder.cancel_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (holder.data.status == 0) {
				// 客户中途取消
				DialogUtil.showComfire(context, "警告", "确认取消吗？", new String[] {
						"确定", "取消" }, new Runnable() {

					@Override
					public void run() {
						 if(holder.data != null){
							 holder.data.uploadFlag = 0;
							 holder.data.status = "已取消";
							 ContentValues values = new ContentValues();
							 values.put("status", holder.data.status);
							 values.put("uploadFlag", holder.data.uploadFlag);
							 TaskDb.updateTaskInfo(holder.data.id, values);
							 TaskDb.addIntoTaskUploadInfo(holder.data);
							 if (refreshListCallBack != null) {
									refreshListCallBack.run();
								}
						 }
//						excuteRescueCanelTask(holder);
					}
				}, new Runnable() {

					@Override
					public void run() {
						// 取消
					}
				});
				// }
			}
		});
	}

	// -------------------------------------------已出发-------------------------------------------------
	private void excuteRescueHaveGoneTask(final ViewHolder holder) {
		PostRescueHaveGone task = new PostRescueHaveGone("正在提交，请稍候...",
				context, holder.data.id,holder.data._id, holder.data.status + "", "", "", "");
		task.execute(new Runnable() {

			@Override
			public void run() {
				if (refreshListCallBack != null) {
					refreshListCallBack.run();
				}
			}
		}, new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	// -------------------------------------------已到达-------------------------------------------------
	private void excuteRescueArriveTask(final ViewHolder holder) {
		postRescueArrive task = new postRescueArrive("正在提交，请稍候...", context,
				holder.data.id,holder.data._id, holder.data.status + "", "", "", "");
		task.execute(new Runnable() {

			@Override
			public void run() {
				if (refreshListCallBack != null) {
					refreshListCallBack.run();
				}
			}
		}, new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	// -------------------------------------------已完成-------------------------------------------------
	private void excuteRescueCompleteTask(final ViewHolder holder) {
		PostRescueComplete task = new PostRescueComplete("正在提交，请稍候...",
				context, holder.data.id, holder.data.status + "", "", "", "");
		task.execute(new Runnable() {

			@Override
			public void run() {
				if (refreshListCallBack != null) {
					refreshListCallBack.run();
				}
			}
		}, new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	// -------------------------------------------取消任务-------------------------------------------------
	private void excuteRescueCanelTask(final ViewHolder holder) {
		PostRescueCancel task = new PostRescueCancel("正在提交，请稍候...", context,
				holder.data.id,holder.data._id, holder.data.status + "", "", "", "");
		task.execute(new Runnable() {

			@Override
			public void run() {
				if (refreshListCallBack != null) {
					refreshListCallBack.run();
				}
			}
		}, new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	class PostRescueHaveGone extends BaseTask {

		public TaskListDTO dto;
		private String taskId;
		private String uuid;
		private String workStatus;
		private String operateTime;

		public PostRescueHaveGone(String preDialogMessage, Context context,
				String taskId,String uuid, String workStatus, String latitude,
				String lontitude, String operateTime) {
			super(preDialogMessage, context);
			this.taskId = taskId;
			this.uuid = uuid;
			this.workStatus = workStatus;
			this.operateTime = operateTime;
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
			if (!Settings.DEBUG) {
				String token = SessionManager.getInstance()
						.getUserInfo(context).token;
				return A57HttpApiV3.getInstance().postRescueHaveGone(token,
						taskId, latitude, longitude, locationTime);
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
			DialogUtil.showToast(context, "“已出发” 提交成功!");
			//更新任务信息表中任务上传状态
			//更新任务状态转换表中上传状态
			ContentValues values = new ContentValues();
			values.put("uploadFlag", 1);
			TaskDb.updateTaskInfo(taskId,values);
			TaskDb.updateTaskUploadInfo(taskId, uuid, "1");
		}

		@Override
		public void onStateError(JsonPack result) {
			closeProgressDialog();
			DialogUtil.showToast(context, result.getMsg());
			ContentValues values = new ContentValues();
			values.put("uploadFlag", 0);
			TaskDb.updateTaskInfo(taskId,values);
			TaskDb.updateTaskUploadInfo(taskId, uuid, "0");
		}

		private JsonPack getTestData() throws JSONException {
			// ---------------------------测试数据
			SystemClock.sleep(500);
			String data = "{}";
			JsonPack result = new JsonPack();
			result.setObj(new JSONObject(data));
			// -----------------------------------
			return result;
		}
	}

	class postRescueArrive extends BaseTask {

		public TaskListDTO dto;
		private String taskId;
		private String uuid;
		private String workStatus;
		private String operateTime;

		public postRescueArrive(String preDialogMessage, Context context,
				String taskId,String uuid, String workStatus, String latitude,
				String lontitude, String operateTime) {
			super(preDialogMessage, context);
			this.taskId = taskId;
			this.workStatus = workStatus;
			this.operateTime = operateTime;
			this.uuid = uuid;
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
			if (!Settings.DEBUG) {
				String token = SessionManager.getInstance()
						.getUserInfo(context).token;
				return A57HttpApiV3.getInstance().postRescueArrical(token,
						taskId, latitude, longitude, locationTime);
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
			DialogUtil.showToast(context, "已到达”  提交成功!");
			ContentValues values = new ContentValues();
			values.put("uploadFlag", 1);
			TaskDb.updateTaskInfo(taskId,values);
			TaskDb.updateTaskUploadInfo(taskId, uuid, "1");
		}

		@Override
		public void onStateError(JsonPack result) {
			closeProgressDialog();
			DialogUtil.showToast(context, result.getMsg());
			ContentValues values = new ContentValues();
			values.put("uploadFlag", 0);
			TaskDb.updateTaskInfo(taskId,values);
			TaskDb.updateTaskUploadInfo(taskId, uuid, "0");
		}

		private JsonPack getTestData() throws JSONException {
			// ---------------------------测试数据
			SystemClock.sleep(500);
			String data = "{}";
			JsonPack result = new JsonPack();
			result.setObj(new JSONObject(data));
			// -----------------------------------
			return result;
		}
	}

	class PostRescueComplete extends BaseTask {

		public TaskListDTO dto;
		private String taskId;
		private String workStatus;
		private String operateTime;

		public PostRescueComplete(String preDialogMessage, Context context,
				String taskId, String workStatus, String latitude,
				String lontitude, String operateTime) {
			super(preDialogMessage, context);
			this.taskId = taskId;
			this.workStatus = workStatus;
			this.operateTime = operateTime;
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
			if (!Settings.DEBUG) {
				String token = SessionManager.getInstance()
						.getUserInfo(context).token;
				return A57HttpApiV3.getInstance().postRescueComplete(token,
						taskId, latitude, longitude, locationTime);
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
			DialogUtil.showToast(context, "“已完成” 提交成功!");
			TaskDb.updateTaskInfo(taskId, "已完成","1");
			TaskDb.updateTaskUploadInfo(taskId, "已完成","1");
		}

		@Override
		public void onStateError(JsonPack result) {
			closeProgressDialog();
			DialogUtil.showToast(context, result.getMsg());
			TaskDb.updateTaskInfo(taskId, "已完成","0");
			TaskDb.updateTaskUploadInfo(taskId, "已完成","0");
		}

		private JsonPack getTestData() throws JSONException {
			// ---------------------------测试数据
			SystemClock.sleep(500);
			String data = "{}";
			JsonPack result = new JsonPack();
			result.setObj(new JSONObject(data));
			// -----------------------------------
			return result;
		}
	}

	class PostRescueCancel extends BaseTask {

		public TaskListDTO dto;
		private String taskId;
		private String uuid;
		private String workStatus;
		private String operateTime;

		public PostRescueCancel(String preDialogMessage, Context context,
				String taskId,String uuid, String workStatus, String latitude,
				String lontitude, String operateTime) {
			super(preDialogMessage, context);
			this.taskId = taskId;
			this.uuid = uuid;
			this.workStatus = workStatus;
			this.operateTime = operateTime;
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
			if (!Settings.DEBUG) {
				String token = SessionManager.getInstance()
						.getUserInfo(context).token;
				return A57HttpApiV3.getInstance().postRescueCancel(token,
						taskId, latitude, longitude, locationTime);
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
			DialogUtil.showToast(context, "“取消任务” 提交成功!");
			ContentValues values = new ContentValues();
			values.put("uploadFlag", 1);
			TaskDb.updateTaskInfo(taskId,values);
			TaskDb.updateTaskUploadInfo(taskId, uuid, "1");
		}

		@Override
		public void onStateError(JsonPack result) {
			closeProgressDialog();
			DialogUtil.showToast(context, result.getMsg());
			ContentValues values = new ContentValues();
			values.put("uploadFlag", 0);
			TaskDb.updateTaskInfo(taskId,values);
			TaskDb.updateTaskUploadInfo(taskId, uuid, "0");
		}

		private JsonPack getTestData() throws JSONException {
			// ---------------------------测试数据
			SystemClock.sleep(500);
			String data = "{}";
			JsonPack result = new JsonPack();
			result.setObj(new JSONObject(data));
			// -----------------------------------
			return result;
		}
	}
}
