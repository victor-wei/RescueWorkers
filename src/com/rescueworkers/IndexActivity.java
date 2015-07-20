package com.rescueworkers;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fg114.main.service.MyLocation;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CommonObservable;
import com.fg114.main.util.CommonObserver;
import com.fg114.main.util.ContextUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.ViewUtils;
import com.rescueworkers.Db.TaskDb;
import com.rescueworkers.dto.MainPageInfoDTO;
import com.rescueworkers.dto.Task;
import com.rescueworkers.task.GetNotFinishWorkTask;

public class IndexActivity extends MainFrameActivity {

	private View contextView;
	private Button button_today_task;
	private TextView task_num;
	private Button button_task_history;

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
		tvTitle.setText("首页");
		btnGoBack.setVisibility(View.VISIBLE);
		btnGoBack.setText("退出");
		btnOption.setVisibility(View.VISIBLE);
		btnOption.setBackgroundResource(R.drawable.button_settings);
		btnOption.setPadding(0, 0, 0, 0);

		// 内容部分

		contextView = View.inflate(this, R.layout.index, null);
		button_today_task = (Button) contextView
				.findViewById(R.id.button_today_task);
		task_num = (TextView) contextView.findViewById(R.id.task_num);
		button_task_history = (Button) contextView
				.findViewById(R.id.button_task_history);
		task_num.setVisibility(View.GONE);

		button_today_task.setOnClickListener(new OnClickListener() {

			/**
			 * 
			 */
			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				ActivityUtil.jump(IndexActivity.this, TodayTaskActivity.class,
						1, null, true);
			}
		});
		button_task_history.setOnClickListener(new OnClickListener() {

			/**
			 * 登录
			 */
			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				ActivityUtil.jump(IndexActivity.this,
						TaskHistoryActivity.class, 1, null, true);
			}
		});
		btnOption.setOnClickListener(new OnClickListener() {

			/**
			 * 登录
			 */
			@Override
			public void onClick(View v) {
				// Bundle bundle=new Bundle();
				// bundle.putSerializable(Settings.BUNDLE_KEY_TASK,
				// holder.data);
				// ActivityUtil.jump(IndexActivity.this, ArrivalActivity.class,
				// 1,bundle);
				//
				// return;
				ViewUtils.preventViewMultipleClick(v, 1000);
				ActivityUtil.jump(IndexActivity.this, SettingsActivity.class,
						1, null, true);
			}
		});
		mainLayout.addView(contextView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
	}

	private MainPageinfoObserver observer = new MainPageinfoObserver(
			new Runnable() {

				@Override
				public void run() {
					setTaskNum();

				}
			});

	@Override
	protected void onResume() {
		super.onResume();
		CommonObservable.getInstance().addObserver(observer);
		// 进页面要刷一下任务数
		exeMainPageTask();
		TextView txtTestVer = (TextView) findViewById(R.id.txt_test_ver);
		// -----------------------------------------------------------------------------
		if (Settings.DEBUG) {
			DialogUtil.showToast(this, "测试版");

			txtTestVer.setVisibility(View.VISIBLE);
		} else if (A57HttpApiV3.getInstance().mApiBaseUrl == A57HttpApiV3.TEST_SERVICE_URL) {
			DialogUtil.showToast(this, "测试版");
			txtTestVer.setVisibility(View.VISIBLE);
		} else
			txtTestVer.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		CommonObservable.getInstance().deleteObserver(observer);
	}

	private void setTaskNum() {
		List<Task> taskList = TaskDb.getNotFinishOrNotUploadTaskList("已完成", "已取消", "0");
		if(taskList != null && taskList.size() > 0 ){
			task_num.setVisibility(View.VISIBLE);
			task_num.setText("" + taskList.size());
		}else{
			task_num.setVisibility(View.GONE);
			task_num.setText("0");
		}
	}

	/****************************************************************************************
	 * 监听处理器
	 ****************************************************************************************/
	public static class MainPageinfoObserver extends CommonObserver {
		Runnable runnable;

		public MainPageinfoObserver(Runnable runnable) {
			this.runnable = runnable;
		}

		public void update(CommonObservable commonObservable, Object arg) {
			runnable.run();
		}
	}

	private void exeMainPageTask() {
		MyLocation myLocation = MyLocation.getInstance();
		String latitude = "";
		String longitude = "";
		String locationTime = "";
		if(myLocation != null){
			latitude = myLocation.getLatitude()+"";
			longitude = myLocation.getLongitude()+"";
			locationTime = myLocation.getLocationTime();
		}
		GetNotFinishWorkTask task = new GetNotFinishWorkTask(null,
				ContextUtil.getContext(), new Runnable() {
					@Override
					public void run() {
						CommonObservable.getInstance().notifyObservers(
								IndexActivity.MainPageinfoObserver.class);
					}
				});
//		GetMainPageInfoTask task = new GetMainPageInfoTask(null,
//				ContextUtil.getContext(), new Runnable() {
//			@Override
//			public void run() {
//				CommonObservable.getInstance().notifyObservers(
//						IndexActivity.MainPageinfoObserver.class);
//			}
//		},latitude,longitude,locationTime);
		// ---
		task.execute(new Runnable() {
			public void run() {
			}
		}, new Runnable() {
			public void run() {
			}
		});
	}

	public void usersignopen(View v) {
		ActivityUtil.jump(IndexActivity.this, UserSignActivity.class, 1, null,
				true);
	}

}
