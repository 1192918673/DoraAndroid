package coms.geeknewbee.doraemon.robot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotBindPresenter;
import coms.geeknewbee.doraemon.robot.view.IBindView;
import coms.geeknewbee.doraemon.utils.ILog;

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

//
//    /**-----------------------数据----------------------**/
//    private static final UUID ROBOT_UUID = UUID.fromString("b5b59b9c-18de-11e6-9409-20c9d0499603");
//    private BluetoothAdapter adapter;
//    private String ROBOT_BT_NAME = "geeknewbee-robot";
//    private OutputStream outputStream;
//
//    /** 可连接设备 **/
//    private BluetoothDevice linkDevice;
//
//    AsyncTask asyncTask;
//
//    boolean stop = false;
//
//    private void bluetoothInit() {
//        // 检查设备是否支持蓝牙
//        adapter = BluetoothAdapter.getDefaultAdapter();
//        if (adapter == null) {
//            // 设备不支持蓝牙
//            tt.showMessage("您的手机不支持蓝牙连接", tt.LONG);
//            finish();
//        }
//        // 打开蓝牙
//        if (!adapter.isEnabled()) {
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            // 设置蓝牙可见性，最多300秒
//            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(intent);
//        }
//        Set<BluetoothDevice> devices = adapter.getBondedDevices();
//        for (int i = 0; i < devices.size(); i++) {
//            BluetoothDevice device = (BluetoothDevice) devices.iterator().next();
//            ILog.e("" + device.getName());
//        }
//        // 设置广播信息过滤
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        // 注册广播接收器，接收并处理搜索结果
//        registerReceiver(searchDevices, intentFilter);
//        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
//        ILog.e("蓝牙搜索广播开始");
//        adapter.startDiscovery();
//    }
//
//    private BluetoothSocket socket;
//    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
//
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Bundle b = intent.getExtras();
//            Object[] lstName = b.keySet().toArray();
//
//            ILog.e("--------------------------");
//            BluetoothDevice device = null;
//            // 搜索设备时，取得设备的MAC地址
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String str = "未配对|" + device.getName() + "|" + device.getAddress();
//                ILog.e(str);
//                if (ROBOT_BT_NAME.equalsIgnoreCase(device.getName())) {
//                    //蓝牙类型
//                    int type = device.getType();
//                    if (type == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
//                        //&& device.getBondState() == BluetoothDevice.BOND_NONE
//                        ILog.e(device.getName() + "|" + ROBOT_BT_NAME);
//                        linkDevice = device;
//                        unregisterReceiver(searchDevices);
//                        adapter.cancelDiscovery();
//                        handler.removeCallbacks(finish);
//                        hideDialog();
//                        tt.showMessage("检测到设备，可以进行控制", tt.SHORT);
//                    }
//                }else{
//                    tt.showMessage("蓝牙类型不匹配",tt.SHORT);
//                }
//            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String str = device.getName() + "|" + device.getAddress();
//                ILog.e(str);
//                switch (device.getBondState()) {
//                    case BluetoothDevice.BOND_BONDING:
//                        ILog.e("BlueToothTestActivity", "正在配对......");
//                        break;
//                    case BluetoothDevice.BOND_BONDED:
//                        ILog.e("BlueToothTestActivity", "完成配对");
//                        break;
//                    case BluetoothDevice.BOND_NONE:
//                        ILog.e("BlueToothTestActivity", "取消配对");
//                    default:
//                        break;
//                }
//            }
//        }
//    };
//
//    Runnable finish = new Runnable() {
//        @Override
//        public void run() {
//            hideDialog();
//            if(linkDevice == null){
//                try {
//                    unregisterReceiver(searchDevices);
//                    adapter.cancelDiscovery();
//                } catch (Exception e){}
//                tt.showMessage("未检测到可连接设备", tt.SHORT);
//                finish();
//            } else if(asyncTask != null) {
//                tt.showMessage("连接超时……", tt.SHORT);
//                asyncTask.cancel(true);
//                cancel();
//                finish();
//            }
//        }
//    };
//
//    public void cancel(){
//        try {
//            stop = true;
//            socket.close();
//            socket = null;
//            if(asyncTask != null){
//                asyncTask.cancel(true);
//            }
//        } catch (Exception e) {
//            ILog.e(e);
//        } finally {
//            linkDevice = null;
//            socket = null;
//        }
//    }

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
