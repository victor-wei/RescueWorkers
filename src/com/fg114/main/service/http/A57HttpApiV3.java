package com.fg114.main.service.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.fg114.main.service.dto.JsonPack;
import com.rescueworkers.Settings;
import com.rescueworkers.XApplication;

/**
 * 获得后台数据方法类
 *
 */
public class A57HttpApiV3 {

	// 单态实例
	private static A57HttpApiV3 instance = null;
	// #define A2 "mobileapi.xiaomishu.com/javamobile.svc/spotcheck"
	// #define A2 "tmobileapi.xiaomishu.com/javamobile.svc/spotcheck"
	public static final String TEST_SERVICE_URL = "http://tmobileapi.xiaomishu.com/javamobile.svc";
	public static final String SERVICE_URL = "http://mobileapi.xiaomishu.com/javamobile.svc";

	public HttpApi mHttpApi;
	public String mApiBaseUrl;

	// 用户登录
	private static final String URL_API_USER_LOGIN = "/rescueWorkers/userLogin";
	private static final String URL_API_LOGOUT = "/rescueWorkers/logout";
	// 获取任务主信息
	private static final String URL_API_GET_MAIN_PAGE_INFO = "/rescueWorkers/getMainPageInfo";
	// 获取未完成任务列表
	private static final String URL_API_GET_NOT_FINISH_WORK_LIST = "/rescueWorkers/getNotFinishWorkList";
	// 获取一定时期内所有任务列表
	private static final String URL_API_GET_TASK_LIST = "/rescueWorkers/getTaskList";
	// 工作状态改变
	private static final String URL_API_POST_WORK_STATUS_CHANGE = "/rescueWorkers/workStatusChange";
	// 已出发状态
	private static final String url_api_rescue_have_gone = "/worker_jobs/id/departure";
	// 已到达状态
	private static final String url_api_rescue_arrive = "/worker_jobs/id/arrive";
	// 已完成状态
	private static final String url_api_rescue_complete = "/worker_jobs/id/complete";
	// 已取消状态
	private static final String url_api_rescue_cancel = "/worker_jobs/id/cancel";
	// 上传gps信息
	private static final String url_api_rescue_gps = "/gps";
	// 上传任务完成 照片或录音
	private static final String url_api_rescue_upload = "/media";
	// 更新任务完成照片或录音
	private static final String url_api_rescue_update = "/media/id";
	// 报错URL
	private static final String URL_API_ERROR_LOG = "/errorLog";
	private static final String URL_API_POST_Sign = "/spotcheck/postusersign";
	private static final String URL_API_POST_SF_GUEST_ARRIVAL = "/spotcheck/postSFGuestArrival";
	private static final String URL_API_POST_ARRIVAL = "/spotcheck/postArrival";

	// static {
	// System.loadLibrary("zip");
	// System.loadLibrary("chk");
	// }

	private native static String get(String ass);

	/**
	 * 实例化
	 * 
	 * @param domain
	 * @param port
	 * @param clientVersion
	 */
	private A57HttpApiV3(String url) {
		mApiBaseUrl = url;
		mHttpApi = new HttpApiWithOAuth(XApplication.mHttpClient);
	}

	public static A57HttpApiV3 getInstance() {
		if (instance == null) {
			// String sss = get(Settings.ASS_PATH);
			if (Settings.DEBUG) {
				instance = new A57HttpApiV3(TEST_SERVICE_URL);
			} else {
				instance = new A57HttpApiV3(SERVICE_URL);
			}

		}
		return instance;
	}

	public String getBaseParamsString() {
		if (mHttpApi == null || !(mHttpApi instanceof HttpApiWithOAuth)) {
			return "";
		}
		HttpApiWithOAuth httpApi = (HttpApiWithOAuth) mHttpApi;
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		httpApi.addBaseParams(paramList);
		String params = URLEncodedUtils.format(paramList, HTTP.UTF_8);
		return params;
	}

	/**
	 * 动态参数的URL生成
	 * 
	 * @param url
	 * @param args
	 * @return
	 */
	private String fullUrl(String url, String... args) {
		String fullUrl = mApiBaseUrl + url;
		for (int i = 0; i < args.length; i++) {
			fullUrl = fullUrl.replace("{" + i + "}", args[i]);
		}
		return fullUrl;
	}

	/**
	 * 用户登录
	 * 
	 * @return 返回用户UserInfoDTO
	 * @throws Exception
	 */
	public JsonPack userLogin(String userName,// 用户名
			String userPwd // 密码
	) throws Exception {
		HttpPost httpPost = mHttpApi.createHttpPost(
				fullUrl(URL_API_USER_LOGIN), new BasicNameValuePair("userName",
						userName), new BasicNameValuePair("userPwd", userPwd));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpPost);
		return jsonPack;
	}

	// 登出 成功返回JsonPack.re=200
	public JsonPack logout(String token // 用户token
	) throws Exception {
		HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_LOGOUT),
				new BasicNameValuePair("token", token));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	// 获得用户首页的信息，返回MainPageInfoDTO
	// 如果没有数据更新，MainPageInfoDTO.needUpdateTag=false
	public JsonPack getMainPageInfo(String token, // 用户token
			long timestamp // 每次回传上次接收到的MainPageInfoDTO里的timestamp
			, String latitude, String longitude, String acquisition_at)
			throws Exception {
		DefaultHttpClient client = null;
		try {
			client = AbstractHttpApi.createHttpClient();
			HttpPost httpGet = mHttpApi.createHttpPost(
					fullUrl(URL_API_GET_MAIN_PAGE_INFO),
					new BasicNameValuePair("token", token),
					new BasicNameValuePair("latitide", latitude),
					new BasicNameValuePair("longitude", longitude),
					new BasicNameValuePair("acquisition_at", acquisition_at));
			JsonPack jsonPack = mHttpApi.doHttpRequest(client, httpGet);

			return jsonPack;
		} catch (Exception e) {
			JsonPack jsonPack = new JsonPack();
			jsonPack.setRe(-1);
			return jsonPack;
		} finally {
			if (client != null) {
				try {
					client.getConnectionManager().shutdown();
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 获得未完成任务列表
	 * 
	 * @param token
	 * @param latitude
	 * @param longitude
	 * @param acquisition_at
	 *            操作时间
	 * @return 返回TaskListDTO
	 * @throws Exception
	 */
	public JsonPack getNotFinishWorkList(String token, String latitude,
			String longitude, String acquisition_at) throws Exception {
		HttpGet httpGet = mHttpApi.createHttpGet(
				fullUrl(URL_API_GET_NOT_FINISH_WORK_LIST),
				new BasicNameValuePair("token", token), new BasicNameValuePair(
						"latitide", latitude), new BasicNameValuePair(
						"longitude", longitude), new BasicNameValuePair(
						"acquisition_at", acquisition_at));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	public JsonPack postRescueWorkStatusChange(
			String token, // 用户token
			String workJason) throws Exception {
		HttpPost httpGet = mHttpApi.createHttpPost(
				fullUrl(URL_API_POST_WORK_STATUS_CHANGE),
				new BasicNameValuePair("token", token), new BasicNameValuePair(
						"worker_jobs", workJason));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}
	public JsonPack postGpsInfo(
			String token, // 用户token
			String gps) throws Exception {
		HttpPost httpGet = mHttpApi.createHttpPost(
				fullUrl(URL_API_POST_WORK_STATUS_CHANGE),
				new BasicNameValuePair("token", token), new BasicNameValuePair(
						"gps", gps));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	/**
	 * 已出发状态提交
	 * 
	 * @param token
	 * @param uuid
	 * @param latitude
	 * @param longitude
	 * @param acquisition_at
	 * @return
	 * @throws Exception
	 */
	public JsonPack postRescueHaveGone(
			String token, // 用户token
			String uuid, String latitude, String longitude,
			String acquisition_at) throws Exception {
		HttpPost httpGet = mHttpApi.createHttpPost(
				fullUrl(url_api_rescue_have_gone), new BasicNameValuePair(
						"token", token), new BasicNameValuePair("id", uuid),
				new BasicNameValuePair("latitide", latitude),
				new BasicNameValuePair("longitude", longitude),
				new BasicNameValuePair("acquisition_at", acquisition_at));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	/**
	 * 已到达提交状态
	 * 
	 * @param token
	 * @param uuid
	 * @param latitude
	 * @param longitude
	 * @param acquisition_at
	 * @return
	 * @throws Exception
	 */
	public JsonPack postRescueArrical(
			String token, // 用户token
			String uuid, String latitude, String longitude,
			String acquisition_at) throws Exception {
		HttpPost httpGet = mHttpApi.createHttpPost(
				fullUrl(url_api_rescue_arrive), new BasicNameValuePair("token",
						token), new BasicNameValuePair("id", uuid),
				new BasicNameValuePair("latitide", latitude),
				new BasicNameValuePair("longitude", longitude),
				new BasicNameValuePair("acquisition_at", acquisition_at));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	/**
	 * 已完成提交状态
	 * 
	 * @param token
	 * @param uuid
	 * @param latitude
	 * @param longitude
	 * @param acquisition_at
	 * @return
	 * @throws Exception
	 */
	public JsonPack postRescueComplete(
			String token, // 用户token
			String uuid, String latitude, String longitude,
			String acquisition_at) throws Exception {
		HttpPost httpGet = mHttpApi.createHttpPost(
				fullUrl(url_api_rescue_complete), new BasicNameValuePair(
						"token", token), new BasicNameValuePair("id", uuid),
				new BasicNameValuePair("latitide", latitude),
				new BasicNameValuePair("longitude", longitude),
				new BasicNameValuePair("acquisition_at", acquisition_at));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	/**
	 * 取消任务
	 * 
	 * @param token
	 * @param uuid
	 * @param latitude
	 * @param longitude
	 * @param acquisition_at
	 * @return
	 * @throws Exception
	 */
	public JsonPack postRescueCancel(
			String token, // 用户token
			String uuid, String latitude, String longitude,
			String acquisition_at) throws Exception {
		HttpPost httpGet = mHttpApi.createHttpPost(
				fullUrl(url_api_rescue_cancel), new BasicNameValuePair("token",
						token), new BasicNameValuePair("id", uuid),
				new BasicNameValuePair("latitide", latitude),
				new BasicNameValuePair("longitude", longitude),
				new BasicNameValuePair("acquisition_at", acquisition_at));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	/**
	 * 客户抵达餐厅提交数据
	 * 
	 * @param token
	 * @param uuid
	 * @param tableNum
	 * @param peopleNum
	 * @param typeTag
	 * @param imageLengthArray
	 *            用逗号分隔的方式依次存放了各图片的字节数。例如：13242314,29282
	 * @param pic
	 *            图片数据，目前最多两张图片。
	 * @return 成功JsonPack.re=200
	 * @throws Exception
	 */
	public JsonPack postRescueCompleteUpload(String token, // 用户token
			String uuid, // task的uuid
			String tableNum, // 桌台号
			int peopleNum, // 就餐人数
			int typeTag, // 餐位类型 1:大厅，2：包房
			String memo, // 抽查备注，最大长度250
			String imageLengthList, // 表示图片大小的数组（可支持多张图片）用分号分隔的方式依次存放了各图片的字节数。例如：13242314;29282
			InputStream pic // 见方法说明
			, String latitude, String longitude, String acquisition_at)
			throws Exception {
		DefaultHttpClient client = null;
		try {
			client = AbstractHttpApi.createHttpClientForUpload();
			HttpPost httpPost = mHttpApi.createHttpPost(
					fullUrl(url_api_rescue_upload),
					pic,
					new BasicNameValuePair("token", token),
					new BasicNameValuePair("uuid", uuid),
					new BasicNameValuePair("tableNum", tableNum),
					new BasicNameValuePair("peopleNum", String
							.valueOf(peopleNum)), new BasicNameValuePair(
							"imageLengthList", imageLengthList),
					new BasicNameValuePair("typeTag", String.valueOf(typeTag)),
					new BasicNameValuePair("memo", String.valueOf(memo)),
					new BasicNameValuePair("latitide", latitude),
					new BasicNameValuePair("longitude", longitude),
					new BasicNameValuePair("acquisition_at", acquisition_at));
			JsonPack jsonPack = mHttpApi.doHttpRequest(httpPost);
			return jsonPack;
		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null) {
				try {
					client.getConnectionManager().shutdown();
				} catch (Exception e) {

				}
			}
		}
	}

	// 抽查人任务查询，规定：查询的时间跨度不可以超过一个月。
	// 成功返回TaskListDTO
	public JsonPack getTaskList(String token, // 用户token
			long startDate, // 起始时间，毫秒数GMT+8
			long endDate // 截止时间，毫秒数GMT+8
			, String latitude, String longitude, String acquisition_at)
			throws Exception {
		HttpGet httpGet = mHttpApi.createHttpGet(
				fullUrl(URL_API_GET_TASK_LIST), new BasicNameValuePair("token",
						token),
				new BasicNameValuePair("startDate", String.valueOf(startDate)),
				new BasicNameValuePair("endDate", String.valueOf(endDate)),
				new BasicNameValuePair("latitide", latitude),
				new BasicNameValuePair("longitude", longitude),
				new BasicNameValuePair("acquisition_at", acquisition_at));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpGet);
		return jsonPack;
	}

	/**
	 * 错误提交
	 * 
	 * @return
	 * @throws Exception
	 */
	public JsonPack errorLog(String version,// 版本
			String deviceNumber,// 设备号
			String uuid,// 手机设备号
			String error,// 错误msg
			String description// 描述
	) throws Exception {
		HttpPost httpPost = mHttpApi.createHttpPostWithoutParams(
				fullUrl(URL_API_ERROR_LOG), new BasicNameValuePair("version",
						version), new BasicNameValuePair("deviceNumber",
						deviceNumber), new BasicNameValuePair("uuid", uuid),
				new BasicNameValuePair("error", error), new BasicNameValuePair(
						"description", description));
		JsonPack jsonPack = mHttpApi.doHttpRequest(httpPost);
		return jsonPack;
	}

	/**
	 * 客户签到提交数据
	 * 
	 * @param token
	 * @param uuid
	 * @param imageLengthArray
	 *            图片大小 例如：13242314
	 * @param pic
	 *            图片数据
	 * @return 成功JsonPack.re=200
	 * @throws Exception
	 */

	public JsonPack PostUserSign_SC(String token, // 用户token
			String uuid, // task的uuid
			String imageLengthList, // 表示图片大小
			InputStream pic // 图片数据, //见方法说明
	) throws Exception {

		DefaultHttpClient client = null;
		try {
			client = AbstractHttpApi.createHttpClientForUpload();
			HttpPost httpPost = mHttpApi.createHttpPost(
					fullUrl(URL_API_POST_Sign), pic, new BasicNameValuePair(
							"token", token), new BasicNameValuePair(
							"imageLengthList", imageLengthList),
					new BasicNameValuePair("uuid", uuid));
			JsonPack jsonPack = mHttpApi.doHttpRequest(httpPost);
			return jsonPack;
		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null) {
				try {
					client.getConnectionManager().shutdown();
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * 客户抵达餐厅提交数据
	 * 
	 * @param token
	 * @param uuid
	 * @param tableNum
	 * @param peopleNum
	 * @param typeTag
	 * @param imageLengthArray
	 *            用逗号分隔的方式依次存放了各图片的字节数。例如：13242314,29282
	 * @param pic
	 *            图片数据，目前最多两张图片。
	 * @return 成功JsonPack.re=200
	 * @throws Exception
	 */
	public JsonPack postSFGuestArrival(
			String token, // 用户token
			String uuid, // task的uuid
			String tableNum, // 桌台号
			int peopleNum, // 就餐人数
			int typeTag, // 餐位类型 1:大厅，2：包房
			String memo, // 抽查备注，最大长度250
			int haschild, int ispricesame, int hassuperwine, int usecashcoupon,
			int arrivelate, String imageLengthList, // 表示图片大小的数组（可支持多张图片）用分号分隔的方式依次存放了各图片的字节数。例如：13242314;29282
			InputStream pic // 见方法说明
	) throws Exception {
		DefaultHttpClient client = null;
		try {
			client = AbstractHttpApi.createHttpClientForUpload();
			HttpPost httpPost = mHttpApi
					.createHttpPost(
							fullUrl(URL_API_POST_SF_GUEST_ARRIVAL),
							pic,
							new BasicNameValuePair("token", token),
							new BasicNameValuePair("uuid", uuid),
							new BasicNameValuePair("tableNum", tableNum),
							new BasicNameValuePair("peopleNum", String
									.valueOf(peopleNum)),
							new BasicNameValuePair("imageLengthList",
									imageLengthList),
							new BasicNameValuePair("typeTag", String
									.valueOf(typeTag)),
							new BasicNameValuePair("memo", String.valueOf(memo)),
							new BasicNameValuePair("haschild", String
									.valueOf(haschild)),
							new BasicNameValuePair("hassuperwine", String
									.valueOf(hassuperwine)),
							new BasicNameValuePair("arrivelate", String
									.valueOf(arrivelate)),
							new BasicNameValuePair("usecashcoupon", String
									.valueOf(usecashcoupon)),
							new BasicNameValuePair("ispricesame", String
									.valueOf(ispricesame)));
			JsonPack jsonPack = mHttpApi.doHttpRequest(httpPost);
			return jsonPack;
		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null) {
				try {
					client.getConnectionManager().shutdown();
				} catch (Exception e) {

				}
			}
		}
	}

	public JsonPack postArrival(String token, // 用户token
			String uuid, // task的uuid
			String imageLengthList, // 表示图片大小的数组（可支持多张图片）用分号分隔的方式依次存放了各图片的字节数。例如：13242314;29282
			InputStream pic // 图片数据, //见方法说明
	) throws Exception {

		DefaultHttpClient client = null;
		try {
			client = AbstractHttpApi.createHttpClientForUpload();
			HttpPost httpPost = mHttpApi.createHttpPost(
					fullUrl(URL_API_POST_ARRIVAL), pic, new BasicNameValuePair(
							"token", token), new BasicNameValuePair(
							"imageLengthList", imageLengthList),
					new BasicNameValuePair("uuid", uuid));
			JsonPack jsonPack = mHttpApi.doHttpRequest(httpPost);
			return jsonPack;
		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null) {
				try {
					client.getConnectionManager().shutdown();
				} catch (Exception e) {

				}
			}
		}
	}

}
