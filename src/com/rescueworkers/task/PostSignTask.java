package com.rescueworkers.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.UnitUtil;
import com.rescueworkers.Settings;
import com.rescueworkers.dto.Task;

/**
 * 提交客户签名 数据
 * @author sunquan1
 */
public class PostSignTask extends BaseTask {
	private static final int IMAGE_SIZE = UnitUtil.dip2px(500); // 图片边长限制
	private static final int IMAGE_QUALITY = 70; // 图片压缩率


	private String token;
	private Context context;
	private Task task;




	public PostSignTask(String preDialogMessage, Context context, 
			Task task 
			) {
		super(preDialogMessage, context, false, 100);		
		this.token = SessionManager.getInstance().getUserInfo(context).token;
		this.context = context;
		this.task = task;


	}

	@Override
	public JsonPack getData() throws Exception {
		String imageLengthList = "";
		InputStream[] input = new InputStream[1];
		imageLengthList=prepareImageData(input);
		progressDialog.setMax(input[0].available());
		//----------测试流数据组合是否正确，必须是两张图片
		//		String[] s=imageLengthList.split(";");
		//		FileOutputStream f=new FileOutputStream("/sdcard/book/1.jpg");
		//		int len1=Integer.parseInt(s[0]);
		//		int len2=Integer.parseInt(s[1]);
		//		byte[] b=new byte[len1];
		//		((ByteArrayInputStream)input[0]).read(b, 0, len1);
		//		f.write(b);
		//		f.close();
		//		
		//		f=new FileOutputStream("/sdcard/book/2.jpg");
		//		b=new byte[len2];
		//
		//		((ByteArrayInputStream)input[0]).read(b, 0, len2);
		//		f.write(b);
		//		f.close();
		//--------------------------------
		Log.d("图片总大小","字节："+input[0].available());
		if (!Settings.DEBUG) {

			return A57HttpApiV3.getInstance().PostUserSign_SC(token, task.id, imageLengthList, input[0]);

		} else {
			return getTestData();
		}
	}

	private String prepareImageData(InputStream[] inputStream) {
		String imageLengthList = "";
		Bitmap bmp = null;
		ByteArrayOutputStream out =  new ByteArrayOutputStream();
		InputStream in = null;



//		ImageView imageView = (ImageView) imagesLayout.getChildAt(i).findViewById(R.id.image);
//
//		bmp = (Bitmap) ((Object[]) imageView.getTag())[0];
		bmp=convertToBitmap("/mnt/sdcard/temp.jpg");

		// 获得数据流
		bmp.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, out);
		imageLengthList=imageLengthList+out.size();


		inputStream[0] = new ProgressInputStream(new ByteArrayInputStream(out.toByteArray()));
		return imageLengthList;
	}
	public Bitmap convertToBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		
		opts.inJustDecodeBounds = false;

		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
		return Bitmap.createBitmap(weak.get());
	
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
	private JsonPack getTestData() throws JSONException{
		//---------------------------测试数据
		SystemClock.sleep(500);
		String data="{}";
		JsonPack result=new JsonPack();
		result.setObj(new JSONObject(data));
		//-----------------------------------
		return result;
	}
	class ProgressInputStream extends InputStream{
		InputStream inner=null;
		int readedCount=0;

		public ProgressInputStream(InputStream inner){
			this.inner=inner;
		}
		@Override
		public int read() throws IOException {
			int r=inner.read();
			doProgress(r==-1?-1:1);
			return r;
		}
		@Override
		public int read(byte[] b) throws IOException {
			int r=inner.read(b);
			doProgress(r);
			return r;
		}
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int r=inner.read(b, off, len);
			doProgress(r);
			return r;
		}
		@Override
		public long skip(long n) throws IOException {
			return inner.skip(n);
		}
		@Override
		public int available() throws IOException {
			return inner.available();
		}
		@Override
		public void close() throws IOException {
			inner.close();
		}
		@Override
		public void mark(int readlimit) {
			inner.mark(readlimit);
		}
		@Override
		public synchronized void reset() throws IOException {
			inner.reset();
		}
		@Override
		public boolean markSupported() {
			return inner.markSupported();
		}
		private void doProgress(int c){
			try {
				//Log.e("读"+inner.available(), "读 " + readedCount + " +" + c + " ->" + (readedCount + c));
				if (c != -1) {
					if (readedCount % 1024 + c >= 1024||inner.available()==0) {
						//每过于1K，触发一次
						Log.d("读了1K", "读了1K " + readedCount + " +" + c + " ->" + (readedCount + c));
						progressDialog.setProgress(readedCount + c);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			readedCount+=c;
		}
	}
}
