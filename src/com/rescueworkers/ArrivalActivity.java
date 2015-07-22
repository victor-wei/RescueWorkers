package com.rescueworkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.fg114.main.recorder.SoundMeter;
import com.fg114.main.util.ActivityUtil;
import com.fg114.main.util.CheckUtil;
import com.fg114.main.util.DialogUtil;
import com.fg114.main.util.ViewUtils;
import com.rescueworkers.dto.Task;
import com.rescueworkers.task.PostArrivalTask;

/**
 * “抵达餐厅” 和 “客户到达” 页面
 * 
 * @author xujianjun,2013-05-01
 *
 */
public class ArrivalActivity extends MainFrameActivity {
	private static final int IMAGE_SIZE = 500; // 图片边长限制
	private static final int IMAGE_QUALITY = 66; // 图片压缩率
	private static final int MAX_SIDE_OF_BITMAP = 500; // 图片的最大边长限制

	private View contextView;
	private Task task;
	private ViewGroup guest_arrival_info_layout;
	private LinearLayout image_layout;
	private Uri tempPath;

	private String tableNum;
	private String peopleNum = "0";
	private LinearLayout otherInfoSlectLayout;
	private int roomType;// 餐位类型
	private int haschild;// 服务他人
	private int pricetype;// 价格不同
	private int hasSuperwineType;// 高档烟酒
	private int lateOrLeaveEarly;// 迟到早退
	private int hasVoucher;// 使用代金券
	private String memo;
	private Button button_camera;
	private Button button_record_start;
	private Button button_record_end;
	private SoundMeter mSensor;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		task = (Task) bundle.getSerializable(Settings.BUNDLE_KEY_TASK);
		if (task == null) {
			DialogUtil.showToast(this, "没有找到任务");
			finish();
		}
		try {
			mSensor = new SoundMeter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// task的状态必须是0(等待抽查)或者2(抽查中)
//		if (task.status != 0 && task.status != 2) {
//			DialogUtil.showToast(this, "没有找到任务");
//			finish();
//		}
		// 初始化界面
		initComponent();
	}

	/**
	 * 初始化
	 */
	private void initComponent() {

		// 设置标题栏
		tvTitle.setText( "工作完成");
		btnGoBack.setVisibility(View.VISIBLE);
		btnOption.setVisibility(View.VISIBLE);
		btnOption.setText("提交");

		// LinearLayout.LayoutParams p=(LayoutParams)
		// btnOption.getLayoutParams();
		// p.width=UnitUtil.dip2px(45);
		// p.height=UnitUtil.dip2px(45);
		// btnOption.setLayoutParams(p);
		// btnOption.setBackgroundResource(R.drawable.button_camera);
		// btnOption.setPadding(0, 0, 0, 0);

		// 内容部分
		contextView = View.inflate(this, R.layout.arrival, null);
		guest_arrival_info_layout = (ViewGroup) contextView
				.findViewById(R.id.guest_arrival_info_layout);

		otherInfoSlectLayout = (LinearLayout) contextView
				.findViewById(R.id.other_info_slect_ll);

		button_camera = (Button) contextView.findViewById(R.id.button_camera);
		button_record_start = (Button) contextView.findViewById(R.id.button_record_start);
		button_record_end = (Button) contextView.findViewById(R.id.button_record_end);
		image_layout = (LinearLayout) contextView
				.findViewById(R.id.image_layout);
		image_layout.removeAllViews();

//		if (task.status == 0) {
			otherInfoSlectLayout.setVisibility(View.GONE);
//		} else if (task.status == 2) {
//			otherInfoSlectLayout.setVisibility(View.VISIBLE);
//		}

		button_camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				int count = image_layout.getChildCount();
				if (count >= 2) {
					DialogUtil.showToast(ArrivalActivity.this, "最多拍摄两张照片！");
					return;
				}
				// 以拍摄时间命名照片
				try {
					String fileName = System.currentTimeMillis() + ".jpg";
					tempPath = ActivityUtil
							.captureImage(ArrivalActivity.this,
									Settings.CAMERAIMAGE, fileName,
									"SpotCheck capture");
				} catch (Exception e) {
					e.printStackTrace();
					DialogUtil.showToast(ArrivalActivity.this,
							"对不起，你的手机不支持拍照上传图片");
				}
			}
		});
		button_record_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				try {
					mSensor.start(System.currentTimeMillis()+".amr");
				} catch (Exception e) {
				}
			}
		});
		button_record_end.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				mSensor.stop();
			}
		});
//		if (task.status == 2) {
//			// 抽查中显示额外信息项
//			guest_arrival_info_layout.setVisibility(View.VISIBLE);
//		} else {
			guest_arrival_info_layout.setVisibility(View.GONE);
//		}
		// 提交按钮
		btnOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				excuteTask();
			}
		});
		mainLayout.addView(contextView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadPicture();
	}
	@Override
	protected void onPause() {
		super.onPause();
		recycle();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String imagePath = null;
		if (requestCode != Settings.CAMERAIMAGE) {
			return;
		}

		// 先检查路径是否正确
		if (tempPath != null) {
			imagePath = parseImgPath(tempPath);
		}
		try {
			if (CheckUtil.isEmpty(imagePath)) {
				DialogUtil.showToast(this, "没有选择任何图片");
				return;
			}
			// 如果未拍照或选择了空图片
			File f = new File(imagePath);
			if (!f.exists() || f.length() == 0) {
				getContentResolver().delete(tempPath, null, null);
				return;
			}
			addPicture(tempPath);
			tempPath = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 获得路径
	 */
	private String parseImgPath(Uri uri) {
		String path = null;
		if (uri != null) {
			ContentResolver localContentResolver = getContentResolver();
			// 查询图片真实路径
			Cursor cursor = localContentResolver.query(uri, null, null, null,
					null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					int index = cursor
							.getColumnIndex(MediaStore.MediaColumns.DATA);
					path = cursor.getString(index);
					cursor.close();
				}
			}
		}
		return path;
	}
	// 添加一张图片
	private void addPicture(Uri imagePath) {

		// --
		final View contentView = View.inflate(this, R.layout.image_item, null);
		final ImageView imageView = (ImageView) contentView
				.findViewById(R.id.image);
		final Button deleteButton = (Button) contentView
				.findViewById(R.id.delete_button);
		// --
		Bitmap bmp = getBitmap(parseImgPath(imagePath));
		if (bmp == null) {
			return;
		}

		imageView.setTag(new Object[]{scaleBitmap(bmp), imagePath});
		imageView.setImageBitmap((Bitmap) ((Object[]) imageView.getTag())[0]);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewUtils.preventViewMultipleClick(v, 1000);
				DialogUtil.showAlert(ArrivalActivity.this, true, "确定要删除这张照片吗？",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deletePicture(contentView);
							}
						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

			}
		});
		image_layout.addView(contentView);
	}

	// 删除一张图片
	public void deletePicture(View contentView) {
		if (contentView == null) {
			return;
		}
		ImageView imageView = (ImageView) contentView.findViewById(R.id.image);
		if (imageView == null || image_layout.getChildCount() <= 0) {
			return;
		}
		imageView.setImageBitmap(null);
		Bitmap bmp = (Bitmap) ((Object[]) imageView.getTag())[0];
		bmp.recycle();
		imageView.setTag(null);
		image_layout.removeView(contentView);
		// ---
	}

	// 获取位图
	private Bitmap getBitmap(String path) {

		ContentResolver contentResolver = this.getContentResolver();
		InputStream picStream = null; // 图片流
		if (path == null || path.equals("")) {
			DialogUtil.showToast(this, "图片路径为空!");
			return null;
		}
		try {
			picStream = new FileInputStream(path);
			// picStream =
			// contentResolver.openInputStream(Uri.parse(mImageUri));
			if (picStream.available() == 0) {
				DialogUtil.showToast(this, "图片数据无效");
				return null;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// 获取图片的宽和高
			BitmapFactory.decodeStream(picStream, null, options); // 此时返回bm为空
			options.inJustDecodeBounds = false;

			if (options.outWidth < 0 || options.outHeight < 0) {
				// 选中的是非图像文件
				DialogUtil.showToast(getBaseContext(), "图片格式无效");
				return null;
			}

			// 计算缩放比
			int scale = 1;
			if (options.outWidth > IMAGE_SIZE || options.outHeight > IMAGE_SIZE) {
				if (options.outWidth >= options.outHeight) {
					scale = (int) Math.ceil(options.outWidth
							/ (float) IMAGE_SIZE);
				} else {
					scale = (int) Math.ceil(options.outHeight
							/ (float) IMAGE_SIZE);
				}
			}
			options.inSampleSize = scale;
			picStream.close();
			// picStream =
			// contentResolver.openInputStream(Uri.parse(mImageUri));
			picStream = new FileInputStream(path);
			Bitmap bmp = BitmapFactory.decodeStream(picStream, null, options);
			return bmp;
		} catch (Exception e) {
			DialogUtil.showToast(this, "载入图片出错");
			Log.e("载入图片出错", "" + e.getMessage(), e);
			return null;
		} finally {
			if (picStream != null) {
				try {
					picStream.close();
				} catch (Exception e) {
					DialogUtil.showToast(this, e.getMessage());
				}

			}
		}
	}

	private Bitmap scaleBitmap(Bitmap old) {

		// 没有超过边长，直接返回
		int height = old.getHeight();
		int width = old.getWidth();
		if (width <= MAX_SIDE_OF_BITMAP && height <= MAX_SIDE_OF_BITMAP) {
			return old;
		}
		// 按比例缩小到MAX_SIDE_OF_BITMAP

		// 缩放比例
		float scale = 1;
		if (height > width) {

			scale = MAX_SIDE_OF_BITMAP / ((float) height);
		} else {
			scale = MAX_SIDE_OF_BITMAP / ((float) width);
		}
		try {
			return Bitmap.createScaledBitmap(old, (int) (width * scale),
					(int) (height * scale), false);

		} finally {
			if (old != null) {
				old.recycle();
				old = null;
			}
		}
	}

	private void recycle() {
		int count = image_layout.getChildCount();
		for (int i = 0; i < count; i++) {
			ImageView imageView = (ImageView) image_layout.getChildAt(i)
					.findViewById(R.id.image);
			Object[] tag = (Object[]) imageView.getTag();
			imageView.setImageBitmap(null);
			((Bitmap) tag[0]).recycle();
			tag[0] = null;
		}
	}

	private void reloadPicture() {
		int count = image_layout.getChildCount();
		for (int i = 0; i < count; i++) {
			ImageView imageView = (ImageView) image_layout.getChildAt(i)
					.findViewById(R.id.image);
			Object[] tag = (Object[]) imageView.getTag();
			Bitmap bmp = scaleBitmap(getBitmap(parseImgPath((Uri) tag[1])));
			imageView.setImageBitmap(bmp);
			tag[0] = bmp;
		}
	}
	/**
	 * check
	 */
	private boolean checkInput() {
		// 检查图片数量
		int count = image_layout.getChildCount();
		if (count == 0) {
			DialogUtil.showToast(this, "必须拍照 1~2 张才能提交！");
			return false;
		}
		// 0(等待抽查) 2(抽查中)
	/*	if (task.status == 2) {
			// 抽查中的需要填写其它信息
			peopleNum = people_num.getText().toString().trim();
			if (CheckUtil.isEmpty(peopleNum)
					|| !TextUtils.isDigitsOnly(peopleNum)) {
				DialogUtil.showToast(this, "请输入就餐人数");
				return false;
			}

			tableNum = table_num.getText().toString().trim();
			if (CheckUtil.isEmpty(tableNum)) {
				DialogUtil.showToast(this, "请输入桌台号");
				return false;
			}
			roomType = Integer.parseInt((room_type.findViewById(
					room_type.getCheckedRadioButtonId()).getTag().toString()));

			haschild = service_for_other.isChecked() ? 1 : 0;
			pricetype = price_no_same.isChecked() ? 1 : 0;
			hasSuperwineType = super_wine.isChecked() ? 1 : 0;
			hasVoucher = use_voucher.isChecked() ? 1 : 0;
			lateOrLeaveEarly = late_leave_early.isChecked() ? 1 : 0;

			memo = ui_memo.getText().toString().trim();
			// if (CheckUtil.isEmpty(memo)) {
			// DialogUtil.showToast(this, "请输入备注");
			// return false;
			// }
		}*/
		return true;
	}

	private void excuteTask() {
		if (!checkInput()) {
			return;
		}
		PostArrivalTask postTask = new PostArrivalTask("正在提交，请稍候...", this,
				task, Integer.parseInt(peopleNum), tableNum, roomType, memo,
				haschild, pricetype, hasSuperwineType, lateOrLeaveEarly,
				hasVoucher, image_layout);
		// 执行任务
		postTask.execute(new Runnable() {
			@Override
			public void run() {
				DialogUtil.showToast(ArrivalActivity.this, "上传成功！");
//				if (task.status == 2) {// 等待抽查
//					Bundle bundle = new Bundle();
//					bundle.putSerializable(Settings.BUNDLE_KEY_TASK, task);
//					ActivityUtil.jump(ArrivalActivity.this,
//							UserSignActivity.class, 1, bundle);
//				}

				finish();
			}
		}, new Runnable() {
			@Override
			public void run() {
			}
		});
	}
}
