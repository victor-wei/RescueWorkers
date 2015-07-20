package com.rescueworkers;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

import com.fg114.main.util.DialogUtil;
import com.rescueworkers.dto.Task;
import com.rescueworkers.task.PostSignTask;

public class UserSignActivity extends MainFrameActivity {
	
	private View contextView;
	private Button btClear;
	private StrokeView strokeView;
	private Task task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 Bundle bundle = this.getIntent().getExtras();
		 if(bundle!=null){
			 task=(Task)bundle.getSerializable(Settings.BUNDLE_KEY_TASK);
		 }
	     
		initComponent();
	}
	
	private void initComponent() {
		
		// 设置标题栏
		tvTitle.setText("签名");
		btnGoBack.setVisibility(View.VISIBLE);
		btnGoBack.setText("退出");
		btnOption.setVisibility(View.VISIBLE);
		btnOption.setText("提交");
		btnOption.setPadding(0, 0, 0, 0);
		contextView = View.inflate(this,R.layout.user_sign, null);
		strokeView = (StrokeView)  contextView.findViewById(R.id.strokeView);
		btClear = (Button) contextView.findViewById(R.id.btclear);
		btClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				strokeView.pointClear();
			}
		});
		
		btnOption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DialogUtil.showAlert(UserSignActivity.this, true,"一旦提交,无法再次提交,是否提交？", new DialogInterface.OnClickListener() {
					// 确定事件
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 提交签名
						strokeView.pointSaveImage();
						excuteTask();
						
					}
				}, new DialogInterface.OnClickListener() {
					// 取消事件
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 什么也不做
						dialog.cancel();
					}
				});
				
			}
		});
		
		mainLayout.addView(contextView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}

	@Override
	protected void onResume() {
		super.onResume();

		
	}

	
	private void excuteTask() {
		
		PostSignTask postTask = new PostSignTask("正在提交，请稍候...", this,task);
		// 执行任务
		postTask.execute(new Runnable() {
			@Override
			public void run() {
				DialogUtil.showToast(UserSignActivity.this, "上传成功！");
				
				finish();
			}
		}, new Runnable() {
			@Override
			public void run() {
			}
		});
	}	
	
	
	
}
