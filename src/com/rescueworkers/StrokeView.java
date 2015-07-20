package com.rescueworkers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StrokeView extends View {

	private final static String TAG = "StrokeView";

	private Paint paint = new Paint();

	private int viewW = 0; // 显示区域的宽度
	private int viewH = 0; // 显示区域的高度

	private static final int ADD_POINT = 0;

	private static final String savePath = Environment
			.getExternalStorageDirectory() + "/temp.jpg";
	/**
	 * pen point data waiting for drawed
	 */

	public List<PenPointData> tempPointArr;

	public List<List<PenPointData>> pointArr = new ArrayList<List<PenPointData>>();

	public String curPage = "";
	public long showLid = 0;
	public Canvas canvas;

	public StrokeView(Context context) {
		super(context);

		paint.setColor(Color.BLACK);
		invalidate();
	}

	public StrokeView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint.setColor(Color.BLACK);
		invalidate();

	}

	// 清除所有数据
	public void pointClear() {
		pointArr = new ArrayList<List<PenPointData>>();
		invalidate();
	}

	// 把屏幕内容保存
	public void pointSaveImage() {
		Bitmap bitmap = null;
		FileOutputStream fileOutputStream = null;
		try {

			bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
					Config.ARGB_8888);
			Canvas canvas = new Canvas();
			canvas.setBitmap(bitmap);
			this.draw(canvas);
			fileOutputStream = new FileOutputStream(savePath);
			bitmap.compress(CompressFormat.JPEG, 70, fileOutputStream);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		viewW = right - left;
		viewH = bottom - top;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "actiondown ...");
			tempPointArr = new ArrayList<PenPointData>();
			pointArr.add(tempPointArr);
			break;

		case MotionEvent.ACTION_UP:

			invalidate();
			// Log.i(TAG, "actionup ...");
			break;

		case MotionEvent.ACTION_MOVE:
			PenPointData data = new PenPointData((int) event.getX(),
					(int) event.getY(), System.currentTimeMillis());
			if (tempPointArr != null)
				tempPointArr.add(data);
			Log.i(TAG, "action move ... ");
			invalidate();

			break;
		}

		return true;
	}

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ADD_POINT:
				invalidate();
				break;

			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.canvas == null)
			this.canvas = canvas;
		for (int i = 0; i < pointArr.size(); i++) {
			List<PenPointData> dataarr = pointArr.get(i);
			if (dataarr == null) {
				continue;
			}
			int predatax = 0;
			int predatay = 0;
			for (int j = 0; j < dataarr.size(); j++) {
				PenPointData data = dataarr.get(j);
				if (j > 0) {
					if (data.x > 0 && data.y > 0) {
						canvas.drawLine(predatax, predatay, data.x, data.y,
								paint);
						canvas.drawLine(predatax - 1, predatay - 1, data.x - 1,
								data.y - 1, paint);
					}
				}
				predatax = data.x;
				predatay = data.y;
			}

		}
	}

	// public class PenStrokeData {
	//
	// public int startx = 0;
	//
	// public int starty = 0;
	//
	// public int endx = 0;
	//
	// public int endy = 0;
	//
	// /**
	// * points
	// */
	// public List<PenPointData> pointArr = new ArrayList<PenPointData> ();
	//
	// /**
	// * log time
	// */
	// public long logtime = 0;
	//
	// }

	public class PenPointData {
		/**
		 * x坐标
		 */
		public int x;
		/**
		 * y坐标
		 */
		public int y;
		/**
		 * 时间
		 */
		public long time;

		public PenPointData(int x, int y, long t) {

			this.x = x;
			this.y = y;
			this.time = t;

		}

	}

}