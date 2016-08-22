package coms.geeknewbee.doraemon.utils;

import java.util.Date;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class ToastTool {

	private static Toast toast;
	private static ToastTool tt;
	private static Context context;

	/** 长时间显示 */
	public static final int LONG = Toast.LENGTH_LONG;
	/** 短时间显示 */
	public static final int SHORT = Toast.LENGTH_SHORT;

	/** 靠左对齐 */
	public static final int LEFT = Gravity.LEFT;
	/** 靠右对齐 */
	public static final int RIGHT = Gravity.RIGHT;
	/** 水平居中对齐 */
	public static final int CENTER_HORIZONTAL = Gravity.CENTER_HORIZONTAL;
	/** 靠上对齐 */
	public static final int TOP = Gravity.TOP;
	/** 靠下对齐 */
	public static final int BOTTOM = Gravity.BOTTOM;
	/** 垂直居中对齐 */
	public static final int CENTER_VERTICAL = Gravity.CENTER_VERTICAL;
	/** 居中对齐 */
	public static final int CENTER = Gravity.CENTER;

	private static long preTime;
	// 双击的最大时间间隔
	private static long totalTime = 2000;

	private ToastTool(Context context) {
		preTime = 0;
	};

	public static ToastTool getToast(Context context) {
		if (null == tt) {
			tt = new ToastTool(context);
		}
		tt.context = context;
		return tt;
	}

	/**
	 * 输出消息
	 * 
	 * @param message
	 */
	public void showMessage(String message, int duration) {
		if (null != toast) {
			toast.cancel();
		}
		toast = Toast.makeText(context, message, duration);
		toast.show();
	}

	/**
	 * 输出自定义组件的Toast输出方式
	 * 
	 * @param horizontal
	 *            水平对齐方式
	 * @param vertical
	 *            垂直对齐方式
	 * @param x
	 *            水平偏移
	 * @param y
	 *            垂直偏移
	 * @param view
	 *            要显示的组件
	 * @param duration
	 *            显示的时间
	 */
	public void showMessage(int horizontal, int vertical, int x, int y,
			View view, int duration) {
		if (null != toast) {
			toast.cancel();
		}
		toast = new Toast(context);
		toast.setGravity(horizontal | vertical, x, y);
		toast.setDuration(duration);
		toast.setView(view);
		toast.show();
	}

	/**
	 * 判断是否双击，不是的话，输出消息
	 * @param message
	 * @return
	 */
	public boolean isDoubleClick(String message) {

		if (null != toast)
			toast.cancel();

		long nowTime = new Date().getTime();
		if (nowTime < preTime + totalTime) {
			return true;
		} else {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			toast.show();
			preTime = nowTime;
			return false;
		}
	}

	/**
	 * 关闭Toast信息
	 */
	public void cancel() {
		if (null != toast)
			toast.cancel();
	}

	/**
	 * 设置双击返回按钮的时间
	 * @param totalTime
	 */
	public static void setTotalTime(long totalTime) {
		ToastTool.totalTime = totalTime;
	}
}
