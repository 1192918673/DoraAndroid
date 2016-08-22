package coms.geeknewbee.doraemon.robot;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewListener;
import com.mining.app.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotBindPresenter;
import coms.geeknewbee.doraemon.robot.view.IBindView;

/**
 * Created by chen on 2016/4/6
 */
public class RobotZxingActivity extends BaseActivity
        implements IBindView, ViewListener, SurfaceHolder.Callback {

    /**-------------------组件-----------------**/
    private SurfaceView previewView;

    private ViewfinderView viewFinder;

    /**-------------------二维码扫描需要的模块-----------------**/
    private CaptureActivityHandler handler;

    private boolean hasSurface;

    private Vector<BarcodeFormat> decodeFormats;

    private String characterSet;

    private InactivityTimer inactivityTimer;

    private MediaPlayer mediaPlayer;

    private boolean playBeep;

    private static final float BEEP_VOLUME = 0.10f;

    private boolean vibrate;

    private String serialNo;

    IRobotBindPresenter bindPresenter;

    private void assignViews() {
        previewView = (SurfaceView) findViewById(R.id.preview_view);
        viewFinder = (ViewfinderView) findViewById(R.id.viewfinder_view);
        bindPresenter = new IRobotBindPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_zxing);
        assignViews();

        CameraManager.init(this);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = previewView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
        int height = (Integer) session.get(session.HEIGHT);
        viewFinder.getLayoutParams().height = height * 3 / 4;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewFinder;
    }

    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            tt.showMessage("Scan failed!", tt.SHORT);
        }else {
            String code = resultString.substring(resultString.indexOf("qr/") + 3);
            Log.e("test", "result : " + code);
            // TODO 扫描成功
            serialNo = code;
            bindPresenter.bindRobot();
            showDialog("正在绑定机器人……");
        }
    }

    @Override
    public void drawViewfinder() {
        viewFinder.drawViewfinder();
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            //通过系统服务获得手机震动服务
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener =
            new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public String getToken() {
        return spt.getString(SptConfig.LOGIN_TOKEN, null);
    }

    @Override
    public String getSerialNo() {
        return serialNo;
    }

    @Override
    public void bindSuccess(RobotBean robotBean) {
        List<RobotBean> robotBeans = new ArrayList<RobotBean>();
        robotBeans.add(robotBean);
        session.put("robotBeans", robotBeans);
        session.remove("index_refresh");
        hideDialog();
        tt.showMessage("绑定成功！", tt.LONG);
        finish();
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        tt.showMessage("" + msg, tt.LONG);
        finish();
    }
}
