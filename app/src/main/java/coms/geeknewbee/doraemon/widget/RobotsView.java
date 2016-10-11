package coms.geeknewbee.doraemon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;

/**
 * 数字图片
 *
 * @author Li Shaoqing
 */
public class RobotsView extends View {

    /**
     * ----------------机器人数据---------------
     **/

    List<RobotBean> robots;

    int current;

    String robotKey;

    /**
     * ----------------画布元素---------------
     **/

    Paint paint;

    float width;

    float height;

    float textSize;

    float circleSize;

    float picSize;

    float divSize;

    int alpha = 51;

    /**
     * ----------------颜色值---------------
     **/

    // 文字颜色
    int c_text = 0xffffffff;

    // 圆形背景颜色
    int c_bg = 0xff058cfe;

    Bitmap robot;

    Matrix matrix;

    float scale;

    /**
     * ----------------动画参数---------------
     **/

    boolean changeing = false;

    int old;

    int times = 20;

    Handler handler = new Handler();

    public RobotsView(Context context, AttributeSet attr) {
        super(context, attr);
        robot = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_robot_dr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);// 重写onDraw方法

        if (width == 0) {
            width = getWidth();
            height = getHeight();
            paint = new Paint();
            paint.setAntiAlias(true);// 去锯齿
            paint.setFilterBitmap(true);
            paint.setStyle(Style.FILL_AND_STROKE);
            paint.setTextAlign(Align.CENTER);

            textSize = height / 8;
            picSize = height / 2;
            circleSize = height * 4 / 5;
            divSize = textSize * 2;

            matrix = new Matrix();
            scale = picSize / robot.getWidth();
        }

        if (robots == null || robots.size() == 0) {
            // 绘制圆形
            paint.setColor(c_bg);
            paint.setAlpha(alpha);
            canvas.drawCircle(width / 2, circleSize / 2, circleSize / 2, paint);
            // 绘制图片
            paint.setAlpha(alpha * 3);
            matrix.setScale(scale, scale);
            matrix.postTranslate(width / 2 - picSize / 2, circleSize / 2 - picSize / 2);
            canvas.drawBitmap(robot, matrix, paint);
            // 绘制文字
            paint.setColor(c_text);
            paint.setAlpha(255);
            paint.setTextSize(textSize);
            canvas.drawText("暂无设备", width / 2, height - 5, paint);
        } else {
            if (changeing) {
                int len = robots.size();
                float d = (current - old) * (circleSize / 2 + divSize + picSize / 2) * times / 20;
                for (int i = 0; i < len; i++) {
                    if (Math.abs(current - i) * (picSize + divSize) + circleSize / 2 > width / 2) {
                        continue;
                    } else if (current == i) {
                        // 绘制圆形
                        paint.setColor(c_bg);
                        paint.setAlpha(alpha * (20 - times) / 20);
                        canvas.drawCircle(width / 2 + d, circleSize / 2, circleSize / 2, paint);
                        // 绘制图片
                        paint.setAlpha(255);
                        matrix.setScale(scale, scale);
                        matrix.postTranslate(width / 2 - picSize / 2 + d, circleSize / 2 - picSize / 2);
                        canvas.drawBitmap(robot, matrix, paint);
                        // 绘制文字
                        paint.setColor(c_text);
                        paint.setTextSize(textSize);
                        canvas.drawText("" + robots.get(i).getName(), width / 2 + d, height - 5, paint);
                    } else {
                        float div = 0;
                        if (i > current) {
                            div = (i - current) * (picSize + divSize) - picSize / 2 + circleSize / 2;
                        } else {
                            div = (i - current) * (picSize + divSize) - circleSize / 2 + picSize / 2;
                        }
                        div += d;
                        if (i == old) {
                            // 绘制圆形
                            paint.setColor(c_bg);
                            paint.setAlpha(alpha * times / 20);
                            canvas.drawCircle(width / 2 + div, circleSize / 2, circleSize / 2, paint);
                        }
                        // 绘制图片
                        paint.setAlpha(255 - 102 * Math.abs(current - i));
                        matrix.setScale(scale, scale);
                        matrix.postTranslate(width / 2 - picSize / 2 + div, circleSize / 2 - picSize / 2);
                        canvas.drawBitmap(robot, matrix, paint);
                        // 绘制文字
                        paint.setColor(c_text);
                        paint.setTextSize(textSize * 4 / 5);
                        paint.setAlpha(255 - 102 * Math.abs(current - i));
                        canvas.drawText("" + robots.get(i).getName(), width / 2 + div, height - 30, paint);
                    }
                }
            } else {
                int len = robots.size();
                for (int i = 0; i < len; i++) {
                    if (Math.abs(current - i) * (picSize + divSize) + circleSize / 2 > width / 2) {
                        continue;
                    } else if (current == i) {
                        // 绘制圆形
                        paint.setColor(c_bg);
                        paint.setAlpha(alpha);
                        canvas.drawCircle(width / 2, circleSize / 2, circleSize / 2, paint);
                        // 绘制图片
                        paint.setAlpha(255);
                        matrix.setScale(scale, scale);
                        matrix.postTranslate(width / 2 - picSize / 2, circleSize / 2 - picSize / 2);
                        canvas.drawBitmap(robot, matrix, paint);
                        // 绘制文字
                        paint.setColor(c_text);
                        paint.setTextSize(textSize);
                        canvas.drawText("" + robots.get(i).getName(), width / 2, height - 5, paint);
                    } else {
                        // 绘制图片
                        paint.setAlpha(255 - 102 * Math.abs(current - i));
                        float div = 0;
                        if (i > current) {
                            div = (i - current) * (picSize + divSize) - picSize / 2 + circleSize / 2;
                        } else {
                            div = (i - current) * (picSize + divSize) - circleSize / 2 + picSize / 2;
                        }
                        matrix.setScale(scale, scale);
                        matrix.postTranslate(width / 2 - picSize / 2 + div, circleSize / 2 - picSize / 2);
                        canvas.drawBitmap(robot, matrix, paint);
                        // 绘制文字
                        paint.setColor(c_text);
                        paint.setTextSize(textSize * 4 / 5);
                        paint.setAlpha(255 - 102 * Math.abs(current - i));
                        canvas.drawText("" + robots.get(i).getName(), width / 2 + div, height - 30, paint);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (changeing) {

        } else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (robots != null && robots.size() > 0) {
                int len = robots.size();
                for (int i = 0; i < len; i++) {
                    if (Math.abs(current - i) * (picSize + divSize) + circleSize / 2 > width / 2) {
                        continue;
                    } else if (current == i) {
                        continue;
                    } else {
                        float div = 0;
                        if (i > current) {
                            div = (i - current) * (picSize + divSize) - picSize / 2 + circleSize / 2;
                        } else {
                            div = (i - current) * (picSize + divSize) - circleSize / 2 + picSize / 2;
                        }
                        if (event.getY() < height - 30
                                && event.getX() > width / 2 - picSize / 2 + div
                                && event.getX() < width / 2 + picSize / 2 + div) {
                            old = current;
                            current = i;
                            changeing = true;
                            times = 20;
                            handler.postDelayed(update, 25);
                            postInvalidate();
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    Runnable update = new Runnable() {
        @Override
        public void run() {
            times--;
            if (times > 0) {
                postInvalidate();
                handler.postDelayed(update, 25);
            } else {
                changeing = false;
                postInvalidate();
            }
        }
    };

    public RobotBean getRobot() {
        if (robots == null) {
            return null;
        }
        return robots.get(current);
    }

    public void setRobotKey(String robotKey) {
        this.robotKey = robotKey;
        current = 0;
        if (robotKey != null && robots != null && robots.size() > 0) {
            int len = robots.size();
            for (int i = 0; i < len; i++) {
                if (robotKey.equals("" + robots.get(i).getId())) {
                    current = i;
                    break;
                }
            }
        }
        postInvalidate();
    }

    public int getCurrent() {
        return current;
    }

    public void setRobots(List<RobotBean> robots) {
        this.robots = robots;
        current = 0;
        if (robotKey != null && robots != null && robots.size() > 0) {
            int len = robots.size();
            for (int i = 0; i < len; i++) {
                if (robotKey.equals("" + robots.get(i).getId())) {
                    current = i;
                    break;
                }
            }
        }
        postInvalidate();
    }
}