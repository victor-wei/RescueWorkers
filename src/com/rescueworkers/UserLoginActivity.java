package com.rescueworkers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CheckUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.JsonUtils;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.SharedprefUtil;
import com.rescueworkers.dto.UserInfoDTO;

/**
 * 用户登录
 * 
 */
public class UserLoginActivity extends MainFrameActivity {

	// 传入参数获得
	private int fromPage; // 返回页面

	// 变量
	private String userName;
	private String password;

	// 界面组件
	private LayoutInflater mInflater;
	private View contextView;
	private EditText etUserName;
	private EditText etPassword;
	private Button btnUserLogin;

	public static boolean needHintToChangePassword = false;

	// 任务
	private UserLoginTask userLoginTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获得传入参数
		Bundle bundle = this.getIntent().getExtras();
		userName = SharedprefUtil.get(this, Settings.KEY_LOGIN_USERNAME, "");

		// 初始化界面
		initComponent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 判断是否已登录
		boolean hasLogined = SessionManager.getInstance().isUserLogin(this);

		if (hasLogined) {
			ActivityUtil.jump(this, IndexActivity.class, 1);
			finish();
		}
	}

	/**
	 * 初始化
	 */
	private void initComponent() {

		// 设置标题栏
		tvTitle.setText("登录");
		btnGoBack.setVisibility(View.INVISIBLE);

		// 内容部分
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contextView = mInflater.inflate(R.layout.user_login, null);
		etUserName = (EditText) contextView.findViewById(R.id.login_name);
		etPassword = (EditText) contextView.findViewById(R.id.password);
		btnUserLogin = (Button) contextView.findViewById(R.id.button_login);
		etUserName.setText(userName);
		btnUserLogin.setOnClickListener(new OnClickListener() {

			/**
			 * 登录
			 */
			@Override
			public void onClick(View v) {
				// 检查输入内容
				if (checkInput() == false) {
					return;
				}
				// 提交登录
				executePostLogin();
			}
		});

		//
		// btnFindPwd.setOnClickListener(new OnClickListener() {
		//
		// /** 找回密码 */
		// @Override
		// public void onClick(View v) {
		// findPwd();
		// }
		// });
		//
		etPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL) {
					btnUserLogin.performClick();
				}
				return false;
			}
		});

		mainLayout.addView(contextView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				DialogUtil.showToast(UserLoginActivity.this, "登录时发生错误，请稍候再试!");
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 提交登陆
	 */
	private void executePostLogin() {

		userLoginTask = new UserLoginTask("正在提交，请稍候...", this, userName,
				password);
		userLoginTask.execute(new Runnable() {

			@Override
			public void run() {
				if (userLoginTask.dto == null) {
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					msg.sendToTarget();
					return;
				}

				// 获得用户信息
				SessionManager.getInstance().setUserInfo(
						UserLoginActivity.this, userLoginTask.dto);
				// 设置登录状态
				SessionManager.getInstance().setIsUserLogin(
						UserLoginActivity.this, true);
				SharedprefUtil.save(UserLoginActivity.this,
						Settings.KEY_LOGIN_USERNAME, userName);
				ActivityUtil.jump(UserLoginActivity.this, IndexActivity.class,
						1);
				finish();
			}

		});
	}

	/**
	 * check
	 */
	private boolean checkInput() {

		userName = etUserName.getText().toString().trim();
		if (CheckUtil.isEmpty(userName)) {
			DialogUtil.showToast(this, "请输入用户名");
			return false;
		}

		password = etPassword.getText().toString().trim();
		if (CheckUtil.isEmpty(password)) {
			DialogUtil.showToast(this, "请输入密码");
			return false;
		}

		return true;
	}

	// /**
	// * 找回密码
	// */
	// private void findPwd() {
	// Bundle bundle = new Bundle();
	// bundle.putInt(Settings.BUNDLE_KEY_FROM_PAGE,
	// Settings.USER_LOGIN_ACTIVITY);
	// String name = etUserEmail.getText().toString().trim();
	// if (!name.equals("")) {
	// bundle.putString(Settings.BUNDLE_FINDPASS_NAME, name);
	// }
	//
	// ActivityUtil.jump(UserLoginActivity.this, UserFindPwdActivity.class,
	// Settings.USER_LOGIN_ACTIVITY, bundle);
	//
	// // 记录flag，以便使用“忘记密码”功能后第一次登录时提示修改密码
	// UserLoginActivity.needHintToChangePassword = true;
	// }

}

class UserLoginTask extends BaseTask {

	public UserInfoDTO dto;

	private String userName;
	private String userPwd;

	public UserLoginTask(String preDialogMessage, Context context,
			String userName, String userPwd) {
		super(preDialogMessage, context);
		this.userName = userName;
		this.userPwd = userPwd;
	}

	@Override
	public JsonPack getData() throws Exception {
		if (!Settings.DEBUG) {
			return A57HttpApiV3.getInstance().userLogin(userName,// 用户名
					userPwd // 密码
					);
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
					UserInfoDTO.class);
			Log.i("TAG", dto.token+" token");
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
		String data = "{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJubyI6IjAwMDAiLCJleHAiOjE0NDAwNzg3ODF9.WspzBN8lu8e2jsgIava2XnZj2zT5JaeasVmBjaLWQpA\"}";
		JsonPack result = new JsonPack();
		result.setObj(new JSONObject(data));
		// -----------------------------------
		return result;
	}
}
