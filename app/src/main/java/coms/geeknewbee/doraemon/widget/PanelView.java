package coms.geeknewbee.doraemon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 控制面板
 * @author Li Shaoqing
 *
 */
public class PanelView extends View {

	/**----------------画布元素---------------**/

	Paint paint;

	float width;

	float height;

	/**----------------颜色值---------------**/

	// 背景颜色
	int c_bg = 0xffffffff;

	// 文字颜色
	int c_btn = 0xff00a0e8;

	// 文字颜色
	int c_text = 0xff333333;

	/**----------------数据---------------**/

	String btn[] = {"上", "下", "左", "右"};

	String name = "名称";

	float st = 1;

	float r;

	float radius;

	int divide = 1;

	int index[] = {0, 0, 0, 0};

	Handler handler;

	public PanelView(Context context, AttributeSet attr) {
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
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setTextAlign(Align.CENTER);

			if(width / divide < height){
				radius = width / divide / 2;
			} else {
				radius = height / 2;
			}
			st = radius / 4;
			r = radius / 1.4142135623731F;
		}

		// 绘制背景圆圈
		paint.setColor(c_btn);
		canvas.drawCircle(width / 2, height / 2, radius, paint);
		paint.setColor(c_bg);
		canvas.drawCircle(width / 2, height / 2, radius / 2, paint);

		// 绘制直线
		canvas.drawLine(width / 2 - r, height / 2 - r,
				width / 2 + r, height / 2 + r, paint);
		canvas.drawLine(width / 2 - r, height / 2 + r,
				width / 2 + r, height / 2 - r, paint);

		// 绘制文字
		paint.setColor(c_text);
		paint.setTextSize(st);
		canvas.drawText("" + name, width / 2, height / 2 + st / 2, paint);
		paint.setColor(c_bg);
		canvas.drawText("" + btn[0], width / 2, height / 2 - radius * 3 / 4 + st / 2, paint);
		canvas.drawText("" + btn[1], width / 2, height / 2 + radius * 3 / 4 + st / 2, paint);
		canvas.drawText("" + btn[2], width / 2 - radius * 3 / 4, height / 2 + st / 2, paint);
		canvas.drawText("" + btn[3], width / 2 + radius * 3 / 4, height / 2 + st / 2, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(handler != null && event.getAction() == MotionEvent.ACTION_DOWN){
			float y = event.getY() - height / 2;
			float x = event.getX() - width / 2;
			if(x * x + y * y > radius * radius * 1.1F){
				handler.sendEmptyMessage(0);
			} else if(x * x + y * y < radius * radius * 0.22F){
				if(index.length > 4){
					handler.sendEmptyMessage(index[4]);
				}
			} else if(y > x && y > -x){
				handler.sendEmptyMessage(index[1]);
			} else if(y < x && y < -x){
				handler.sendEmptyMessage(index[0]);
			} else if(x > y && x > -y){
				handler.sendEmptyMessage(index[3]);
			} else if(x < y && x < -y){
				handler.sendEmptyMessage(index[2]);
			}
		}
		return super.onTouchEvent(event);
	}

	public void setName(String name) {
		this.name = name;
		postInvalidate();
	}

	public void setBtn(String... btn) {
		this.btn = btn;
		postInvalidate();
	}

	public void setDivide(int divide) {
		this.divide = divide;
		if(width / divide < height){
			radius = width / divide / 2;
		} else {
			radius = height / 2;
		}
		st = radius / 4;
		r = radius / 1.4142135623731F;
		postInvalidate();
	}

	public void setIndex(int... index) {
		this.index = index;
		postInvalidate();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
		postInvalidate();
	}
}