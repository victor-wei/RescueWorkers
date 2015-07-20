package com.rescueworkers.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fg114.main.service.dto.JsonPack;
import com.fg114.main.service.http.A57HttpApiV3;
import com.fg114.main.service.task.BaseTask;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.SessionManager;
import com.fg114.main.util.UnitUtil;
import com.rescueworkers.R;
import com.rescueworkers.Settings;
import com.rescueworkers.dto.Task;

/**
 * 提交“抵达餐厅” 或者 “客户到达” 数据
 * 
 * @author xujianjun, 2013-05-01
 */
public class PostArrivalTask extends BaseTask {
	private static final int IMAGE_SIZE = UnitUtil.dip2px(500); // 图片边长限制
	private static final int IMAGE_QUALITY = 80; // 图片压缩率

	private String token;
	private LinearLayout imagesLayout; // 图片容器
	private Context context;
	private Task task;
	private int peopleNum;
	private String tableNum;
	private int roomType;
	private String memo;
	private int haschildType;
	private int priceType;
	private int hassuperwine;
	private int arrivelate;
	private int usecashcoupon;

	public PostArrivalTask(String preDialogMessage, Context context, Task task,
			int peopleNum, String tableNum, int roomType, String memo,
			int haschildType, int priceType, int hassuperwine, int arrivelate,
			int usecashcoupon, LinearLayout image_layout) {
		super(preDialogMessage, context, false, 100);
		this.token = SessionManager.getInstance().getUserInfo(context).token;
		this.context = context;
		this.task = task;
		this.peopleNum = peopleNum;
		this.tableNum = tableNum;
		this.roomType = roomType;
		this.haschildType = haschildType;
		this.arrivelate = arrivelate;
		this.usecashcoupon = usecashcoupon;
		this.priceType = priceType;
		this.memo = memo;
		this.hassuperwine = hassuperwine;
		this.imagesLayout = image_layout;

	}

	@Override
	public JsonPack getData() throws Exception {
		String imageLengthList = "";
		InputStream[] input = new InputStream[1];
		imageLengthList = prepareImageData(input);
		progressDialog.setMax(input[0].available());
		// ----------测试流数据组合是否正确，必须是两张图片
		// String[] s=imageLengthList.split(";");
		// FileOutputStream f=new FileOutputStream("/sdcard/book/1.jpg");
		// int len1=Integer.parseInt(s[0]);
		// int len2=Integer.parseInt(s[1]);
		// byte[] b=new byte[len1];
		// ((ByteArrayInputStream)input[0]).read(b, 0, len1);
		// f.write(b);
		// f.close();
		//
		// f=new FileOutputStream("/sdcard/book/2.jpg");
		// b=new byte[len2];
		//
		// ((ByteArrayInputStream)input[0]).read(b, 0, len2);
		// f.write(b);
		// f.close();
		// --------------------------------
		Log.d("图片总大小", "字节：" + input[0].available());
		if (!Settings.DEBUG) {
//			if ("新任务") {//  新任务
				return A57HttpApiV3.getInstance().postArrival(token, task.id,
						imageLengthList, input[0]);
//			} else {// 抽查中
//				return A57HttpApiV3.getInstance().postSFGuestArrival(token,
//						task.id, tableNum, peopleNum, roomType, memo,
//						haschildType, priceType, hassuperwine, arrivelate,
//						usecashcoupon, imageLengthList, input[0]);
//			}
		} else {
			return getTestData();
		}
	}

	private String prepareImageData(InputStream[] inputStream) {
		String imageLengthList = "";
		Bitmap bmp = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = null;
		int currentSize = 0;
		int count = imagesLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			ImageView imageView = (ImageView) imagesLayout.getChildAt(i)
					.findViewById(R.id.image);
			bmp = (Bitmap) ((Object[]) imageView.getTag())[0];
			// 获得数据流
			bmp.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, out);
			imageLengthList = imageLengthList + (out.size() - currentSize)
					+ ((i == count - 1) ? "" : ";");
			currentSize = out.size();
		}
		inputStream[0] = new ProgressInputStream(new ByteArrayInputStream(
				out.toByteArray()));
		return imageLengthList;
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
	class ProgressInputStream extends InputStream {
		InputStream inner = null;
		int readedCount = 0;

		public ProgressInputStream(InputStream inner) {
			this.inner = inner;
		}
		@Override
		public int read() throws IOException {
			int r = inner.read();
			doProgress(r == -1 ? -1 : 1);
			return r;
		}
		@Override
		public int read(byte[] b) throws IOException {
			int r = inner.read(b);
			doProgress(r);
			return r;
		}
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int r = inner.read(b, off, len);
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
		private void doProgress(int c) {
			try {
				// Log.e("读"+inner.available(), "读 " + readedCount + " +" + c +
				// " ->" + (readedCount + c));
				if (c != -1) {
					if (readedCount % 1024 + c >= 1024
							|| inner.available() == 0) {
						// 每过于1K，触发一次
						Log.d("读了1K", "读了1K " + readedCount + " +" + c + " ->"
								+ (readedCount + c));
						progressDialog.setProgress(readedCount + c);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			readedCount += c;
		}
	}
}
