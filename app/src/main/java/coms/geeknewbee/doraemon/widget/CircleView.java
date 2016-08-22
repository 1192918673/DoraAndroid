package coms.geeknewbee.doraemon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 数字图片
 * @author Li Shaoqing
 *
 */
public class CircleView extends View {
	
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
	
	int num = -1;

	String info;

	float br = 0;

	float sr = 0;

	float bt = 0;

	float st = 0;

	float sw = 0;
	
	public CircleView(Context context, AttributeSet attr) {
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
			bt = width / 2.5F;
			sw = paint.getStrokeWidth();
		}

		// 绘制背景圆圈
		paint.setColor(c_bg);
		paint.setStrokeWidth(width / 26);
		canvas.drawCircle(width / 2, height / 2, br, paint);
		paint.setColor(c_text);
		paint.setStrokeWidth(width / 15);
		if(num >= 0){
			canvas.drawArc(new RectF(sr, sr, width - sr, height - sr),
					270, num * 360 / 100, false, paint);
		} else if(num == -1){
			canvas.drawArc(new RectF(sr, sr, width - sr, height - sr), 270, 360, false, paint);
		} else {
			canvas.drawArc(new RectF(sr, sr, width - sr, height - sr), 270, 0, false, paint);
		}

		// 绘制文字
		paint.setStrokeWidth(sw);
		paint.setColor(c_text);
		paint.setTextSize(st);
		canvas.drawText("" + info, width / 2, height / 3, paint);
		paint.setTextSize(bt);
		if(num >= 0){
			canvas.drawText("" + num, width / 2, height * 3 / 4, paint);
		} else if(num == -1){
			canvas.drawText("ON", width / 2, height * 3 / 4, paint);
		} else {
			canvas.drawText("OFF", width / 2, height * 3 / 4, paint);
		}
	}
	
	public void setNum(int num) {
		this.num = num;
		postInvalidate();
	}

	public void setInfo(String info) {
		this.info = info;
		postInvalidate();
	}
}