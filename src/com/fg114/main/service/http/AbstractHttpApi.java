package com.fg114.main.service.http;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.fg114.main.service.LocationUtil;
import com.fg114.main.service.MyLocation;
import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CalendarUtil;
import com.fg114.main.util.CipherUtils;
import com.fg114.main.util.ContextUtil;
import com.fg114.main.util.ConvertUtil;
import com.fg114.main.util.SessionManager;
import com.rescueworkers.Settings;

/**
 * 
 *
 */
public abstract class AbstractHttpApi implements HttpApi {
	
	private static final String TAG = "AbstractHttpApi";
	private static final boolean DEBUG = Settings.DEBUG;
	
    private static final int TIMEOUT = 50;
    
    private final DefaultHttpClient mHttpClient;
    
    public AbstractHttpApi(DefaultHttpClient httpClient) {
        mHttpClient = httpClient;
    }
    
    /**
     * 建立一个线程安全的client。
     *
     * @return HttpClient
     */
    public static final DefaultHttpClient createHttpClient() {
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();
        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        // Set some client http client parameter defaults.
        final HttpParams httpParams = createHttpParams(TIMEOUT);
        HttpClientParams.setRedirecting(httpParams, false);

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams,supportedSchemes);

        return new DefaultHttpClient(ccm, httpParams);
    }
    //具有不同的超时时间
    public static final DefaultHttpClient createHttpClientForUpload() {
    	return createHttpClient(-1);
    }

    
	/**
	 * 停止Client连接管理器
	 */
	public static final void shutdownHttpClient(DefaultHttpClient client) {
		if (client != null && client.getConnectionManager() != null) {
			client.getConnectionManager().shutdown();
		}
	}

    
    public static final DefaultHttpClient createHttpClient(int soTimeoutSecond) { 
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();
        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        // Set some client http client parameter defaults.
        final HttpParams httpParams = createHttpParams(soTimeoutSecond);
        HttpClientParams.setRedirecting(httpParams, false);

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams,
                supportedSchemes);

        return new DefaultHttpClient(ccm, httpParams);
    } 
    /**
     * 创建默认设置的http协议参数，可以设置超时时间的
     */
    public static final HttpParams createHttpParams(int soTimeoutSecond) {
    	final HttpParams params = new BasicHttpParams();
    	
    	// Turn off stale checking. Our connections break all the time anyway,
    	// and it's not worth it to pay the penalty of checking every time.
    	 HttpConnectionParams.setTcpNoDelay(params, true);
         HttpConnectionParams.setLinger(params, 20000);
    	HttpConnectionParams.setStaleCheckingEnabled(params, false);
    	HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
    	if(soTimeoutSecond>20){
    		HttpConnectionParams.setSoTimeout(params, soTimeoutSecond * 1000);
    	}
    	HttpConnectionParams.setSocketBufferSize(params, 512);
    	
    	return params;
    }
    
    /**
     * 执行请求
     * @param httpRequest
     * @param clazz
     * @return
     * @throws Exception
     */
    public JsonPack executeHttpRequestWithJson(HttpRequestBase httpRequest) throws Exception {
    	return executeHttpRequestWithJson(mHttpClient, httpRequest);
    }
    
    public JsonPack executeHttpRequestWithJson(DefaultHttpClient client, HttpRequestBase httpRequest) throws Exception {
    	//将 设备号+时间戳 放入请求头
    	String content = Settings.DEV_ID + "," + String.valueOf(System.currentTimeMillis());
//    	String enc = CipherUtils.toHexString(CipherUtils.encryptAesByMd5Key(Settings.KK.getBytes("UTF-8"), content.getBytes("UTF-8")));
    	String enc = CipherUtils.encodeXms(content);
    	httpRequest.addHeader(Settings.REST_EC_NAME, enc);
//    	Log.d("iOS", CipherUtils.encodeXms("162ca6a8a0ed82231921cba8897607c2d33fe671,1325152547208"));
    	
    	httpRequest.addHeader("Accept-Encoding", "gzip");
    	String responseString = "";
		try {
			// 获得返回的流数据
			InputStream is = executeHttpRequestSuccess(client, httpRequest);
			responseString = ConvertUtil.convertStreamToString(is);
			logRequestAndResponse(httpRequest, responseString);
			
		} catch (Exception e) {
			ByteArrayOutputStream bos=new ByteArrayOutputStream(1024);
			PrintWriter pw=new PrintWriter(bos,true);
			e.printStackTrace(pw);
			pw.flush();
			logRequestAndResponse(httpRequest, e.getMessage()+"\n-->"+bos.toString());
			throw e;
		}
        //设置返回值
        JsonPack jp = new JsonPack();
        if (responseString != null && !"".equals(responseString)) {
	        JSONObject jsonResponse = new JSONObject(responseString);
	        
	        jp.setRe(jsonResponse.getInt("code"));
	        jp.setUrl(httpRequest.getURI().toString());
	        
	        if (jp.getRe() == 500) {
	        	jp.setMsg("网络查询出现错误");
				Exception ex = new Exception("Exception 500 from server");
				String msg = "Exception 500 from server ";
				if (httpRequest != null && httpRequest.getURI() != null) {
					msg += httpRequest.getURI().toString();
				}
				ActivityUtil.saveException(ex, msg);
			}else {
				jp.setMsg(jsonResponse.getString("msg"));
			}
	        
        	if(jsonResponse.has("data")){
        		Object obj = jsonResponse.get("data");
        		if (obj instanceof JSONObject) {
        			JSONObject successResultObject = jsonResponse.getJSONObject("data");  
        			jp.setObj(successResultObject);
        		}
        	}
        }
        return jp;
    }
 

    /**
     * 请求成功判断
     * @param httpRequest
     * @return
     * @throws Exception
     */
    public InputStream executeHttpRequestSuccess(HttpRequestBase httpRequest) throws Exception{
    	return executeHttpRequestSuccess(mHttpClient, httpRequest);
    }
    
    /**
     * 请求成功判断
     * @param httpRequest
     * @return
     * @throws Exception
     */
    public InputStream executeHttpRequestSuccess(DefaultHttpClient client, HttpRequestBase httpRequest) throws Exception{
    	 HttpResponse response = executeHttpRequest(client, httpRequest);
    	 int statusCode = response.getStatusLine().getStatusCode();
         switch (statusCode) {
			case 200:
				boolean isGzip = false;
				Header ceheader = response.getEntity().getContentEncoding();
				if (ceheader != null) {
					HeaderElement[] codecs = ceheader.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().equalsIgnoreCase("gzip")) {
							isGzip = true;
							break;
						}
					}
				}
				if (isGzip) {
					return new GZIPInputStream(response.getEntity().getContent());
				} else {
					return response.getEntity().getContent();
				}
			default :
	             response.getEntity().consumeContent();
	             throw new Exception(response.getStatusLine().toString() +" |request=: "+httpRequest.getURI().toString());
         }
    }
 
    /**
     * execute() an httpRequest catching exceptions and returning null instead.
     *
     * @param httpRequest
     * @return
     * @throws Exception 
     * @throws IOException
     */
    public HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws Exception {
    	return executeHttpRequest(mHttpClient, httpRequest);
    }
    
    /**
     * execute() an httpRequest catching exceptions and returning null instead.
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpResponse executeHttpRequest(DefaultHttpClient client, HttpRequestBase httpRequest) throws Exception {
        try {
//        	Log.e("executeHttpRequest", httpRequest.getURI().toString());
        	client.getConnectionManager().closeExpiredConnections();
            return client.execute(httpRequest);
        } catch (Exception e) {
            httpRequest.abort();
            throw e;
        }
    }
    
    /**
     * 建立GET请求
     */
    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
        if (DEBUG) Log.d(TAG, query);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        httpGet.setHeader("Connection", "close");
        httpGet.setHeader("accept", "application/json");
        httpGet.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
        return httpGet;
    }

    /**
     * 建立POST请求
     */
    public HttpPost createHttpPost(String url, boolean isSuper57, NameValuePair... nameValuePairs) {
    	String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
        HttpPost httpPost = new HttpPost(url + "?" + query);
        try {
	        if (isSuper57) {
	        	//超级小秘书的请求的场合
	        	if (DEBUG) Log.d(TAG, generateJsonRequest(nameValuePairs));
	        	httpPost.setHeader("Connection", "close");
	        	httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(nameValuePairs), HTTP.UTF_8));
	        } else {
	        	//一般的场合
	        	httpPost.setHeader("accept", "application/json");
	            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
	        	StringEntity se = new StringEntity(generateJsonRequest(nameValuePairs), HTTP.UTF_8);
	        	if (DEBUG) Log.d(TAG, generateJsonRequest(nameValuePairs));
	      	  	se.setContentType("application/json; charset=utf-8");
	            httpPost.setEntity(se);
	        }
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }
        return httpPost;
    }
    
    /**
     * 建立POST请求
     */
    public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
    	String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
    	HttpPost httpPost = new HttpPost(url + "?" + query);
//        HttpPost httpPost = new HttpPost(url);
        try {
        	//一般的场合
        	httpPost.setHeader("accept", "application/json");
        	httpPost.setHeader("Connection", "close");
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
        	StringEntity se = new StringEntity(generateRequest(nameValuePairs), HTTP.UTF_8);
        	if (DEBUG) Log.d(TAG, generateJsonRequest(nameValuePairs));
      	  	se.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(se);
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }
        return httpPost;
    }
    /**
     * 建立POST请求，重载方法目的是，传输大String（不能放在url里传输）
     */
    public HttpPost createHttpPost(NameValuePair largeString, String url,NameValuePair... nameValuePairs) {
    	String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
    	HttpPost httpPost = new HttpPost(url + "?" + query);
//        HttpPost httpPost = new HttpPost(url);
    	try {
    		//一般的场合
    		httpPost.setHeader("accept", "application/json");
    		httpPost.setHeader("Connection", "close");
    		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
    		StringEntity se = new StringEntity(generateRequest(largeString,nameValuePairs), HTTP.UTF_8);
    		if (DEBUG) Log.d(TAG, generateJsonRequest(nameValuePairs));
    		se.setContentType("application/json; charset=utf-8");
    		httpPost.setEntity(se);
    	} catch (UnsupportedEncodingException e1) {
    		throw new IllegalArgumentException("Unable to encode http parameters.");
    	}
    	return httpPost;
    }
    
    /**
     * 建立上传文件POST请求
     */
    public HttpPost createHttpPost(String url,InputStream stream, NameValuePair... nameValuePairs) {
    	String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
    	if (DEBUG) Log.d(TAG, query);
    	HttpPost httpPost = new HttpPost(url + "?" + query);
        try {
//        	httpPost.setHeader("accept", "application/json");
//            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
        	httpPost.setHeader("Connection", "close"); 
        	//httpPost.setHeader("Content-Length", ""+stream.available());
        	httpPost.setHeader("Content-Type", "image/*");
            InputStreamEntity inEntity = new InputStreamEntity(stream, stream.available());
        	if (DEBUG) Log.d(TAG, generateJsonRequest(nameValuePairs));
            httpPost.setEntity(inEntity);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        } catch (IOException e) {
        	throw new IllegalArgumentException("Unable to encode http parameters.");
        }
        return httpPost;
    }
    
    /**
     * 建立POST请求
     */
    public HttpPost createHttpPostWithoutParams(String url, NameValuePair... nameValuePairs) {
    	//String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
        HttpPost httpPost = new HttpPost(url);
        try {
        	//一般的场合
        	httpPost.setHeader("accept", "application/json");
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
            httpPost.setHeader("Connection", "close");
        	StringEntity se = new StringEntity(generateJsonRequest(nameValuePairs), HTTP.UTF_8);
        	if (DEBUG) Log.d(TAG, generateJsonRequest(nameValuePairs));
      	  	se.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(se);
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }
        return httpPost;
    }
    
    /**
     * 建立POST请求
     */
    public HttpPost createHttpPostGoogle(String url, NameValuePair... nameValuePairs) {
        HttpPost httpPost = new HttpPost(url);
        try {
        	//一般的场合
        	httpPost.setHeader("accept", "application/json");
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
            httpPost.setHeader("Connection", "close");
        	StringEntity se = new StringEntity(generateGoogleRequest(nameValuePairs), HTTP.UTF_8);
        	if (DEBUG) Log.d(TAG, generateJsonRequest(nameValuePairs));
      	  	se.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(se);
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }
        return httpPost;
    }
    
    private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair param = nameValuePairs[i];
            if (param.getValue() != null) {
                params.add(param);
            }
        }
        addBaseParams(params);
        return params;
    }
    
    /**
     * 生成Json形式参数
     * @param nameValuePairs
     * @return
     */
    private String generateJsonRequest(NameValuePair... nameValuePairs) {
    	List<NameValuePair> params = stripNulls(nameValuePairs);
    	JSONStringer jStringer = new JSONStringer();
    	try {
	    	jStringer.object();
	        for (NameValuePair param : params) {
	            if (param.getValue() != null) {
	            	Object value;
	            	try {
	            		value = new JSONArray(param.getValue());
	            	} catch (JSONException e) {
	            		try {
	            			value = new JSONObject(param.getValue());
	            		} catch (JSONException ex) {
	     	            	value = param.getValue();
	            		}
	            	}
	            	jStringer.key(param.getName()).value(value);
	            }
	        }
	        jStringer.endObject();
    	} catch (JSONException e) {
    		return null;
    	}
        return jStringer.toString();
    }
    
    /**
     * 生成Json形式参数
     * @param nameValuePairs
     * @return
     */
    private String generateRequest(NameValuePair... nameValuePairs) {
    	List<NameValuePair> params = stripNulls(nameValuePairs);
    	JSONStringer jStringer = new JSONStringer();
    	try {
	    	jStringer.object();
	        for (NameValuePair param : params) {
	            if (param.getValue() != null) {
	            	jStringer.key(param.getName()).value(param.getValue());
	            }
	        }
	        jStringer.endObject();
    	} catch (JSONException e) {
    		return null;
    	}
        return jStringer.toString();
    }
    /**
     * 生成Json形式参数
     * @param nameValuePairs
     * @return
     */
    private String generateRequest(NameValuePair largeString, NameValuePair... nameValuePairs) {
    	List<NameValuePair> params = stripNulls(nameValuePairs);
    	params.add(largeString);
    	JSONStringer jStringer = new JSONStringer();
    	try {
    		jStringer.object();
    		for (NameValuePair param : params) {
    			if (param.getValue() != null) {
    				jStringer.key(param.getName()).value(param.getValue());
    			}
    		}
    		jStringer.key(largeString.getName()).value(largeString.getValue());
    		jStringer.endObject();
    	} catch (JSONException e) {
    		return null;
    	}
    	return jStringer.toString();
    }
    
    /**
     * 生成Json形式参数
     * @param nameValuePairs
     * @return
     */
    private String generateGoogleRequest(NameValuePair... nameValuePairs) {
    	JSONStringer jStringer = new JSONStringer();
    	try {
	    	jStringer.object();
	        for (NameValuePair param : nameValuePairs) {
	            if (param.getValue() != null) {
	            	Object value;
	            	try {
	            		value = new JSONArray(param.getValue());
	            	} catch (JSONException e) {
	            		try {
	            			value = new JSONObject(param.getValue());
	            		} catch (JSONException ex) {
	     	            	value = param.getValue();
	            		}
	            	}
	            	jStringer.key(param.getName()).value(value);
	            }
	        }
	        jStringer.endObject();
    	} catch (JSONException e) {
    		return null;
    	}
        return jStringer.toString();
    }
    
    public void addBaseParams(List<NameValuePair> params) {
        // 增加设备类型参数
    	if (containsKey(params, "deviceType") == -1) {
    		params.add(new BasicNameValuePair("deviceType", Build.MODEL));
    	}
    	
    	// 增加版本号信息
    	if (containsKey(params, "version") == -1) {
    		params.add(new BasicNameValuePair("version", Settings.VERSION_NAME));
    	}
    	
    	// 增加当前页面参数
    	if (containsKey(params, "currentPage") == -1) {
    		params.add(new BasicNameValuePair("currentPage", Settings.CURRENT_PAGE));
    	}
    	
//    	// 增加城市id
//    	if (containsKey(params, "cityId") == -1) {
//    		CityInfo city = SessionManager.getInstance().getCityInfo(ContextUtil.getContext());
//    		if (city == null || TextUtils.isEmpty(city.getId())) {
//    			params.add(new BasicNameValuePair("cityId", ""));
//    		} else {
//    			params.add(new BasicNameValuePair("cityId", city.getId()));
//    		}
//    	}
    	
    	// 增加gps信息
		if (containsKey(params, "haveGpsTag") == -1 && containsKey(params, "longitude") == -1 && containsKey(params, "latitude") == -1) {
			boolean haveGpsTag = MyLocation.getInstance().isValid(); //ActivityUtil.isGpsAvailable();
			double longitude = 0;
			double latitude = 0;
			if (haveGpsTag) {
				MyLocation loc=MyLocation.getInstance();
				if (loc == null || !loc.isValid()) {
					haveGpsTag = false;
				} else {
					longitude = loc.getLongitude();
					latitude = loc.getLatitude();
				}
			}
			params.add(new BasicNameValuePair("haveGpsTag", String.valueOf(haveGpsTag)));
			params.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
			params.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
		}
    	
    	// 增加设备号信息
    	int indexOfDeviceNumber = containsKey(params, "deviceNumber");
    	if (indexOfDeviceNumber == -1) {
    		params.add(new BasicNameValuePair("deviceNumber", Settings.DEV_ID));
    	} else {
    		// 保证传递的设备号为全局参数，而不是即时获取的
    		String fakeDeviceNumber = params.get(indexOfDeviceNumber).getValue(); // 一些接口即时获取的设备号
    		if (!Settings.DEV_ID.equals(fakeDeviceNumber)) {
    			// 即时获取的设备号和全局设备号不同时，做特殊处理
    			params.remove(indexOfDeviceNumber);
        		params.add(new BasicNameValuePair("deviceNumber", Settings.DEV_ID));
        		params.add(new BasicNameValuePair("fakeDeviceNumber", fakeDeviceNumber));
    		}
    	}
    	
//    	 // 增加渠道号参数
//    	if (containsKey(params, "sellChannelNumber") == -1) {
//    		params.add(new BasicNameValuePair("sellChannelNumber", Settings.SELL_CHANNEL_NUM));
//    	}
    }
    
    private int containsKey(List<NameValuePair> params, String key) {
    	if (params == null || params.size() == 0 || TextUtils.isEmpty(key)) {
    		return -1;
    	}
    	for (int i=0; i<params.size(); i++) {
    		NameValuePair pair = params.get(i);
    		if (key.equals(pair.getName())) {
    			return i;
    		}
    	}
    	return -1;
    }
    //测试机能在内存中记录，最近的一些请求和响应，方便调试，（测试机按 android的“菜单键”可查看）
    private void logRequestAndResponse(HttpRequestBase httpRequest,String response){//errorLog
    	
    	if (ActivityUtil.isTestDev(ContextUtil.getContext())) {
			// 记录日志
			if (Settings.requestLog.length() > 1024 * 500) {
				Settings.requestLog.delete(0, Settings.requestLog.length());
			}
			String request=httpRequest.getURI().toString();
			if(request.indexOf("errorLog")>0){//如果是错误日志，记录下
				request=request+"\n长度"+((HttpPost)httpRequest).getEntity().getContentLength();
			}
			Settings.requestLog.insert(0, "\n========>" + CalendarUtil.getDateTimeString() + "<========\n" + request + "\n"
					+ "************************************\n" + response + "\n");
		}
    }
}
