package com.rescueworkers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.ViewUtils;
import com.rescueworkers.dto.MainPageInfoDTO;
import com.rescueworkers.dto.UserInfoDTO;

/**
 * 设置
 */
public class SettingsActivity extends MainFrameActivity {

	private View contextView;
	private TextView name;
	private TextView true_name;
	private RadioGroup testType;
	private TextView version;
	private TextView new_version;

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
		tvTitle.setText("设置");
		btnGoBack.setVisibility(View.VISIBLE);
		btnGoBack.setText("首页");
		btnOption.setVisibility(View.VISIBLE);
		btnOption.setText("退出登录");
		
		

		// 内容部分
		contextView = View.inflate(this, R.layout.settings, null);
		name = (TextView) contextView.findViewById(R.id.name);
		true_name = (TextView) contextView.findViewById(R.id.true_name);
		version = (TextView) contextView.findViewById(R.id.version);
		new_version = (TextView) contextView.findViewById(R.id.new_version);
		
		UserInfoDTO user=SessionManager.getInstance().getUserInfo(this);
//		name.setText(user.name);
//		true_name.setText(user.trueName);

		btnOption.setOnClickListener(new OnClickListener() {
			/**
			 * 退出登录
			 */
			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				executeLoginout();
			}
		});
		
		//新版本逻辑--------------------------------------------------
		MainPageInfoDTO mainPageInfo=SessionManager.getInstance().getMainPageInfo();
		version.setText(Settings.VERSION_NAME);
		if(!mainPageInfo.hasNewVersion){
			new_version.setText("没有新版本");
		}else{
			new_version.setText("新版下载");	
			//http://mk.cdn.jccjd.com/cms/prod/upload/apks/586/cms_338_dddfb9f5fc0e9219.apk
			ViewUtils.setURL(new_version, 0, 4, mainPageInfo.newVersionUrl, true);
		}
		//-----------------------测试机逻辑----------------------------
		testType=(RadioGroup) contextView.findViewById(R.id.testType);
		testType.setVisibility(View.GONE);
		
		if(ActivityUtil.isTestDev(this)){
			testType.setVisibility(View.VISIBLE);
		}
		//恢复选择状态
		if(Settings.DEBUG){
			testType.check(R.id.radio0);
		}else if(A57HttpApiV3.getInstance().mApiBaseUrl==A57HttpApiV3.TEST_SERVICE_URL){
			testType.check(R.id.radio1);
		}else{
			testType.check(R.id.radio2);
		}
		testType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
					case R.id.radio0: //模拟数据
						Settings.DEBUG=true;
						break;
					case R.id.radio1: //测试接口
						Settings.DEBUG=false;
						A57HttpApiV3.getInstance().mApiBaseUrl=A57HttpApiV3.TEST_SERVICE_URL;
						break;
					default : //正式接口
						Settings.DEBUG=false;
						A57HttpApiV3.getInstance().mApiBaseUrl=A57HttpApiV3.SERVICE_URL;
						break;
				}
			}
		});
		//------------------------------------------------------------
		mainLayout.addView(contextView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}

	private void executeLoginout() {

		LoginOutTask task = new LoginOutTask("正在退出登录，请稍候...", this);
		task.execute(new Runnable() {

			@Override
			public void run() {
				SessionManager.getInstance().setIsUserLogin(getApplicationContext(), false);
				SessionManager.getInstance().setUserInfo(getApplicationContext(), new UserInfoDTO());
				finish();
			}

		});
	}

}

class LoginOutTask extends BaseTask {

	public LoginOutTask(String preDialogMessage, Context context) {
		super(preDialogMessage, context);
	}

	@Override
	public JsonPack getData() throws Exception {

		String token = SessionManager.getInstance().getUserInfo(context).token;
		if (!Settings.DEBUG) {
			return A57HttpApiV3.getInstance().logout(token);
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
	}

	@Override
	public void onStateError(JsonPack result) {
		closeProgressDialog();
		DialogUtil.showToast(context, result.getMsg());
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
