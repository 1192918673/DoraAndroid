package coms.geeknewbee.doraemon.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

/**
 * 头像截取类
 */
public abstract class ClipPicture {
	
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	/**
	 * 只返回图片
	 */
	public static final int GET_BITMAP = 0;
	
	/**
	 * 返回图片和文件
	 */
	public static final int GET_BITMAP_AND_FILE = 1;
	
	/**
	 * 使用相册
	 */
	public static final int USE_PHOTO = 0;
	
	/**
	 * 使用相机
	 */
	public static final int USE_CAMERA = 1;
	
	/**
	 * 截取图片的宽高
	 */
	public static final int BITMAP_WIDTH = 256;
	public static final int BITMAP_HEIGHT = 256;
	
	private static final String[] items = { "本地图片", "拍照" };
	
	private Activity activity;
	
	private Fragment fragment;
	
	private Bitmap bitmap;
	
	private File picture;
	
	private String cache_name;
	
	private int type;
	
	/**
	 * 截图方法
	 * @param activity
	 * @param type 返回类型，GET_BITMAP，只返回图片，
	 * GET_BITMAP_AND_FILE，返回图片和文件
	 */
	public ClipPicture(Activity activity, Fragment fragment, int type){
		this.activity = activity;
		this.fragment = fragment;
		this.type = type;
	}

	public void clipPicture() {
		
		new AlertDialog.Builder(activity)
		.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case 0:
					Intent fromgallery = new Intent();
					fromgallery.setType("image/*");
					fromgallery
							.setAction(Intent.ACTION_GET_CONTENT);
					if(null != fragment)
						fragment.startActivityForResult(fromgallery,
								IMAGE_REQUEST_CODE);
					else
						activity.startActivityForResult(fromgallery,
								IMAGE_REQUEST_CODE);
					break;
				case 1:
					FileHandler fh = new FileHandler(activity);
					String path = fh.SDPATH + fh.Path + "cache/20130201ll254558658.jpg";
					Intent intentFromCapture = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					intentFromCapture.putExtra(
							MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(path)));
					if(null != fragment)
						fragment.startActivityForResult(
								intentFromCapture,
								CAMERA_REQUEST_CODE);
					else
						activity.startActivityForResult(
								intentFromCapture,
								CAMERA_REQUEST_CODE);

					break;
				}
			}
		}).setNegativeButton("取消", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
	
	public void clipPicture(int which) {
		switch (which) {
		case 0:
			Intent fromgallery = new Intent();
			fromgallery.setType("image/*");
			fromgallery
					.setAction(Intent.ACTION_GET_CONTENT);
			if(null != fragment)
				fragment.startActivityForResult(fromgallery,
						IMAGE_REQUEST_CODE);
			else
				activity.startActivityForResult(fromgallery,
						IMAGE_REQUEST_CODE);
			break;
		case 1:

			FileHandler fh = new FileHandler(activity);
			String path = fh.SDPATH + fh.Path + "cache/20130201ll254558658.jpg";
			Intent intentFromCapture = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			intentFromCapture.putExtra(
					MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(path)));
			if(null != fragment)
				fragment.startActivityForResult(
						intentFromCapture,
						CAMERA_REQUEST_CODE);
			else
				activity.startActivityForResult(
						intentFromCapture,
						CAMERA_REQUEST_CODE);

			break;
		}
	}
	
	/**
	 * 选择图片的处理，
	 * 必须在activity的onActivityResult方法里调用
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void dealPictureResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		// 如果是直接从相册获取
		case IMAGE_REQUEST_CODE:
			if (data != null) {
				startPhotoZoom(data.getData());
			}
			break;
		// 如果是调用相机拍照时
		case CAMERA_REQUEST_CODE:
			if (resultCode == -1) {
				FileHandler fh = new FileHandler(activity);
				File temp = new File(fh.SDPATH + fh.Path
						+ "cache/20130201ll254558658.jpg");
				startPhotoZoom(Uri.fromFile(temp));
			}
			break;
		// 取得裁剪后的图片
		case RESULT_REQUEST_CODE:
			if (data != null) {
				getImageToView(data);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 剪裁图片方法
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", BITMAP_WIDTH);
		intent.putExtra("outputY", BITMAP_HEIGHT);
		intent.putExtra("return-data", true);
		if(null != fragment)
			fragment.startActivityForResult(intent, RESULT_REQUEST_CODE);
		else
			activity.startActivityForResult(intent, RESULT_REQUEST_CODE);
	}
	
	/**
	 * 将处理好的图片加入到galleryadapter
	 * 
	 * @param data
	 */
	public void getImageToView(Intent data) {
		final Bundle extras = data.getExtras();
		if (extras != null) {

			new AsyncTask<Object, Object, Object>() {

				@Override
				protected void onPostExecute(Object obj) {
					
					onFinish(bitmap, picture);
					
					super.onPostExecute(obj);
				}

				@Override
				protected void onPreExecute() {
					bitmap = extras.getParcelable("data");
					if(bitmap.getWidth() < BITMAP_WIDTH){
						bitmap = getBitmap(bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, true);
					}
					super.onPreExecute();
				}

				@Override
				protected Object doInBackground(Object... params) {

					if(type == GET_BITMAP_AND_FILE){
						
						FileHandler fh = new FileHandler(activity);

						try {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
							InputStream is = new ByteArrayInputStream(
									baos.toByteArray());

							cache_name = fh.store("cache", is);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (null != cache_name) {
							picture = new File(fh.SDPATH + fh.Path + cache_name);
						}
					}

					return null;
				}
			}.execute();

		}
	}

	/**
	 * 压缩Bitmap到一定大小
	 * @param temp
	 * @param width
	 * @param height
	 * @param release 压缩完成，是否释放原来Bitmap
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap temp, int width, int height, boolean release){

		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Matrix matrix=new Matrix();
		matrix.setScale(width * 1.0F / temp.getWidth(), height * 1.0F / temp.getHeight());

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawBitmap(temp, matrix, paint);

		if(release){
			temp.recycle();
			temp = null;
		}

		return output;
	}
	
	public abstract void onFinish(Bitmap bitmap, File picture);
}
