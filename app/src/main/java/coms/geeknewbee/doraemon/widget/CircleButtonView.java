package coms.geeknewbee.doraemon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

/**
 * 数字图片
 * @author Li Shaoqing
 *
 */
public class CircleButtonView extends View {

	/**----------------画布元素---------------**/

	Paint paint;

	float width;

	float height;

	/**----------------颜色值---------------**/

	// 背景颜色
	int c_bg = 0xff67cdf5;

	// 文字颜色
	int c_text = 0xff00a0e8;

	/**----------------数据---------------**/

	int num = 0;

	String text;

	float br = 0;

	float sr = 0;

	float bt = 0;

	float st = 0;

	float sw = 0;

	boolean isLoading = false;

	public CircleButtonView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);// 重写onDraw方法
		
		if(width == 0){
			width = getWidth();
			height = getHeight();
			paint = new Paint();
			paint.setAntiAlias(true);// 去锯齿
			paint.setStyle(Style.STROKE);
			paint.setTextAlign(Align.CENTER);

			br = width / 2 - width / 52;
			sr = width / 26 + width / 30;
			st = width / 8;
			bt = width / 5;
			sw = paint.getStrokeWidth();
		}

		// 绘制背景圆圈
		paint.setColor(c_bg);
		paint.setStrokeWidth(width / 26);
		canvas.drawCircle(width / 2, height / 2, br, paint);
		paint.setColor(c_text);
		paint.setStrokeWidth(width / 15);
		canvas.drawArc(new RectF(sr, sr, width - sr, height - sr),
				270, num * 360 / 100, false, paint);

		// 绘制文字
		paint.setStrokeWidth(sw);
		if(isLoading){
			paint.setColor(c_bg);
		} else {
			paint.setColor(c_text);
		}
		paint.setTextSize(bt);
		canvas.drawText("" + text, width / 2, height / 2 + bt / 2, paint);
	}

	CountDownTimer timer = new CountDownTimer(800, 20) {
		@Override
		public void onTick(long millisUntilFinished) {
			if(num < 40){
				num += 5;
			} else if(num < 80){
				num += 3;
			} else if(num < 95) {
				num += 1;
			}
			postInvalidate();
		}

		@Override
		public void onFinish() {

		}
	};

	public void setText(String text) {
		this.text = text;
		postInvalidate();
	}

	public void startLoading(){
		isLoading = true;
		postInvalidate();
		timer.start();
	}

	public void stopLoading(){
		isLoading = false;
		postInvalidate();
		timer.cancel();
		num = 0;
	}

	public boolean isLoading() {
		return isLoading;
	}
}