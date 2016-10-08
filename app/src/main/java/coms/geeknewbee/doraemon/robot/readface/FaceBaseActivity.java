package coms.geeknewbee.doraemon.robot.readface;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import dou.helper.CameraHelper;
import dou.utils.DLog;
import dou.utils.DisplayUtil;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

/**
 * Created by mac on 16/7/13.
 */
public abstract class FaceBaseActivity extends FragmentActivity implements Camera.PreviewCallback,
        CameraHelper.OnCameraStatusListener {
    private SurfaceView camera_view;
    private SurfaceView draw_view;
    private boolean busy = true;
    private CameraHelper mCameraHelper;
    protected YMFaceTrack faceTrack;

    protected int iw = 0, ih;
    private float scale_bit;
    protected int sw;
    protected int sh;
    private boolean showFps = false;
    protected Context mContext;
    private List<Float> timeList = new ArrayList<>();
    private boolean stop = false;
    private int camera_max_width = 640;
    protected int orientation;

    public void initCamera() {
        DLog.d("start initCamera");
        mContext = this;
        sw = DisplayUtil.getScreenWidthPixels(this);
        sh = DisplayUtil.getScreenHeightPixels(this);
        camera_view = (SurfaceView) findViewById(R.id.camera_preview);
        draw_view = (SurfaceView) findViewById(R.id.pointView);
        draw_view.setZOrderOnTop(true);
        draw_view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mCameraHelper = new CameraHelper(this, camera_view, camera_max_width);
        mCameraHelper.setOnCameraStatusListener(this);
        mCameraHelper.setPreviewCallback(this);
        DLog.d("end initCamera");
//        faceTrack = new YMFaceTrack();
////        此处默认初始化，initCameraMsg()处会根据设备设置自动更改设置
//        faceTrack.initTrack(this, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640);
//        faceTrack.setRecognitionConfidence(80);
//        busy = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (faceTrack != null) {
            faceTrack.onRelease();
            faceTrack = null;
        }
        DLog.d("start YMFaceTrack");
        mContext = this;
        iw = 0;//重新调用initCameraMsg的开关
        faceTrack = new YMFaceTrack();
        //此处默认初始化，initCameraMsg()处会根据设备设置自动更改设置
        faceTrack.initTrack(this, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640);
        faceTrack.setRecognitionConfidence(80);
        busy = false;
        stop = false;
        DLog.d("end YMFaceTrack");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop = true;
        drawAnim(null, draw_view, scale_bit, 0, "");
        mContext = null;
    }

    @Override
    public void onPreviewFrame(final byte[] bytes, Camera camera) {
        if (!busy && !stop) {
            initCameraMsg();//此处会根据屏幕设置对检测器重新设置
            busy = true;
            try {
                long time = System.currentTimeMillis();
                List<YMFace> faces = analyse(bytes, iw, ih);
                String fps = "";
                if (showFps) {
                    long now = System.currentTimeMillis();
                    float than = now - time;
                    timeList.add(than);
                    if (timeList.size() >= 20) {
                        float sum = 0;
                        for (int i = 0; i < timeList.size(); i++) {
                            sum += timeList.get(i);
                        }
                        fps = String.valueOf((int) (1000f * timeList.size() / sum)) + "  time:" + than;
                        timeList.remove(0);
                    }
                }
                drawAnim(faces, draw_view, scale_bit, getCameraId(), String.valueOf(fps));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                busy = false;
            }
        }
    }

    protected abstract void drawAnim(List<YMFace> faces, SurfaceView draw_view, float scale_bit, int cameraId, String fps);

    protected abstract List<YMFace> analyse(byte[] bytes, int iw, int ih);

    @Override
    public void onCameraStopped(byte[] data) {
    }


    private void initCameraMsg() {
        if (iw == 0) {
            iw = mCameraHelper.getPreviewSize().width;
            ih = mCameraHelper.getPreviewSize().height;
            orientation = 0;
            ////注意横屏竖屏问题

            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                scale_bit = sw / (float) ih;
                if (mCameraHelper.getCameraId() == 1) {
                    orientation = YMFaceTrack.FACE_270;
                } else {
                    orientation = YMFaceTrack.FACE_90;
                }
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                scale_bit = sh / (float) ih;
                orientation = YMFaceTrack.FACE_0;
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                scale_bit = sw / (float) ih;
                if (mCameraHelper.getCameraId() == 1) {
                    orientation = YMFaceTrack.FACE_90;
                } else {
                    orientation = YMFaceTrack.FACE_270;
                }
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                scale_bit = sh / (float) ih;
                orientation = YMFaceTrack.FACE_180;
            }

            faceTrack.setOrientation(orientation);
            ViewGroup.LayoutParams params = draw_view.getLayoutParams();
            params.height = camera_view.getLayoutParams().height;
            params.width = camera_view.getLayoutParams().width;
            draw_view.requestLayout();
            startAddPerson();
        }
    }


    public int getCameraId() {
        return mCameraHelper.getCameraId();
    }

    public int switchCamera() {
        return mCameraHelper.switchCameraId();
    }

    public void stopCamera() {
        mCameraHelper.stopCamera();
    }

    public void takePicture() {
        mCameraHelper.stopPreview();
    }

    public void reStartCamera() {
        mCameraHelper.reStartCamera();
        mCameraHelper.setPreviewCallback(this);
    }

    protected void showFps(boolean show) {
        showFps = show;
    }

    protected void resetDb() {
        faceTrack.resetAlbum();
    }

    protected abstract void startAddPerson();

    protected int getDoomW(int tar) {
        if (sw >= 1080) return tar;
        return sw * tar / 1080;
    }

    protected int getDoomH(int tar) {
        return sh * tar / 1920;
    }

    public void setCamera_max_width(int width) {
        this.camera_max_width = width;
    }
}
