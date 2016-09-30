package coms.geeknewbee.doraemon.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import coms.geeknewbee.doraemon.global.MyApplication;
import dou.utils.DensityUtil;
import dou.utils.StringUtils;
import mobile.ReadFace.YMFace;

/**
 * Created by mac on 16/6/23.
 */
public class TrackUtil {

    private static final int count = 20;
    private static List<Integer> emo_list = new ArrayList<>();//储存每张面部表情的集合

    public static void addFace(YMFace face) {
        if (face == null) return;
        emo_list.add(getMaxFromArr(face.getEmotions()));
        if (emo_list.size() > count) {
            emo_list.remove(0);
        }
    }


    private static int getMaxFromArr(float arr[]) {
        int position = 0;
        float max = 0;
        for (int j = 0; j < arr.length; j++) {
            if (max <= arr[j]) {
                max = arr[j];
                position = j;
            }
        }
        return position;
    }


    public static boolean isSmile() {//微笑拍照
        if (emo_list.size() <= 18) return false;
        return countPosition(emo_list) == 0;
    }


    private static int countPosition(List<Integer> emo_list) {

        Map<Integer, Integer> map = new HashMap();
        for (int i = 0; i < emo_list.size(); i++) {
            int position = emo_list.get(i);
            Integer count = map.get(position);
            map.put(position, (count == null) ? 1 : count + 1);
        }

        int max = 0;
        int position = 0;

        Iterator<Integer> iter = map.keySet().iterator();

        while (iter.hasNext()) {
            int key = iter.next();
            int value = map.get(key);
            if (max <= value) {
                position = key;
                max = value;
            }
        }
        return position;
    }

    public static void cleanFace() {
        emo_list.clear();
    }

    public static void startCountDownAnimation(CountDownAnimation countDownAnimation) {
        // Customizable animation
        // Use a set of animations
        Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
                0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        countDownAnimation.setAnimation(animationSet);
        countDownAnimation.start();
    }


    public static void drawAnim(List<YMFace> faces, SurfaceView outputView, float scale_bit, int cameraId) {
        drawAnim(faces, outputView, scale_bit, cameraId, null, false);
    }

    public static void drawAnim(YMFace face, SurfaceView outputView, float scale_bit, int cameraId) {
        List<YMFace> faces = new ArrayList<>();
        faces.add(face);
        drawAnim(faces, outputView, scale_bit, cameraId, null, false);
    }

    public static void drawAnim(List<YMFace> faces, SurfaceView outputView, float scale_bit, int cameraId, String fps, boolean showPoint) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = outputView.getHolder().lockCanvas();

        if (canvas != null) {
            try {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                for (int i = 0; i < faces.size(); i++) {
                    paint.setColor(Color.WHITE);
                    int size = DensityUtil.dip2px(MyApplication.getContext(), 3);
                    paint.setStrokeWidth(size);
                    paint.setStyle(Paint.Style.STROKE);
                    YMFace ymFace = faces.get(i);
                    int viewW = outputView.getLayoutParams().width;
                    float[] rect = ymFace.getRect();

                    //注意前置后置摄像头问题
                    float x1 = viewW - rect[0] * scale_bit - rect[2] * scale_bit;
                    if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                        x1 = rect[0] * scale_bit;
                    float y1 = rect[1] * scale_bit;

                    float length = rect[3] * scale_bit / 5;
                    float width = rect[3] * scale_bit;
                    float heng = size / 2;
                    canvas.drawLine(x1 - heng, y1, x1 + length, y1, paint);
                    canvas.drawLine(x1, y1 - heng, x1, y1 + length, paint);

                    x1 = x1 + width;
                    canvas.drawLine(x1 + heng, y1, x1 - length, y1, paint);
                    canvas.drawLine(x1, y1 - heng, x1, y1 + length, paint);

                    y1 = y1 + width;
                    canvas.drawLine(x1 + heng, y1, x1 - length, y1, paint);
                    canvas.drawLine(x1, y1 + heng, x1, y1 - length, paint);

                    x1 = x1 - width;
                    canvas.drawLine(x1 - heng, y1, x1 + length, y1, paint);
                    canvas.drawLine(x1, y1 + heng, x1, y1 - length, paint);

                    if (showPoint) {
                        paint.setColor(Color.rgb(57, 138, 243));
                        size = DensityUtil.dip2px(MyApplication.getContext(), 2.5f);
                        paint.setStrokeWidth(size);
                        float[] points = ymFace.getLandmarks();
                        for (int j = 0; j < points.length / 2; j++) {
                            x1 = viewW - points[j * 2] * scale_bit;
                            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                                x1 = points[j * 2] * scale_bit;
                            y1 = points[j * 2 + 1] * scale_bit;
                            canvas.drawPoint(x1, y1, paint);
                        }
                    }
                }
                if (!StringUtils.isEmpty(fps)) {
                    paint.setColor(Color.rgb(57, 138, 243));
                    paint.setStrokeWidth(0);
                    paint.setAntiAlias(true);
                    paint.setStyle(Paint.Style.FILL);
                    int size = DensityUtil.sp2px(MyApplication.getContext(), 17);
                    paint.setTextSize(size);
                    canvas.drawText("fps = " + fps, 20, 280, paint);
                }
            } catch (Exception e) {

            } finally {
                outputView.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public static int computingAge(int age) {
        int ran = new Random().nextInt(3) + 3;
        age = (age / ran) * ran;
        return age;
    }
}
