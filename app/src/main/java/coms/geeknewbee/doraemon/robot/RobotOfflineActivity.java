package coms.geeknewbee.doraemon.robot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.UUID;

import coms.geeknewbee.doraemon.BLE.BleManager;
import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.robot.utils.BluetoothCommand;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.widget.PanelView;
import coms.geeknewbee.doraemon.widget.Rudder;

public class RobotOfflineActivity extends BaseActivity implements Runnable {

    private static final int ACT_MOVE = 0;

    private static final int ACT_FORWARD = 1;

    private static final int ACT_BACKWARD = 2;

    private static final int ACT_LEFT = 3;

    private static final int ACT_RIGHT = 4;

    private static final int ACT_DISCONNECT = 5;

    private static final int ACT_INTRO = 6;

    private static final int ACT_DANCE = 7;

    private static final int ACT_MOVIE = 8;

    private static final int ACT_STOP = 9;

    private static final int ACT_HEAD_LEFT = 10;

    private static final int ACT_HEAD_RIGHT = 11;

    private static final int ACT_HEAD_UP = 12;

    private static final int ACT_HEAD_DOWN = 13;

    private static final int ACT_ARM_LFRONT = 14;

    private static final int ACT_ARM_LEND = 15;

    private static final int ACT_ARM_LUP = 16;

    private static final int ACT_ARM_LDOWN = 17;

    private static final int ACT_ARM_RFRONT = 18;

    private static final int ACT_ARM_REND = 19;

    private static final int ACT_ARM_RUP = 20;

    private static final int ACT_ARM_RDOWN = 21;

    private static final int ACT_HEAD_FRONT = 22;

    /**
     * -----------------------组件----------------------
     **/

    Button olInfo;

    Button olDance;

    Button olMovie;

    Button olAct;

    Button olStop;

    LinearLayout rlAct;

    ImageButton ibBack;

    PanelView pvHead;

    PanelView pvLarm;

    PanelView pvRarm;

    Rudder pvFoot;

    /**
     * 可连接设备
     **/
    private BluetoothDevice linkDevice;

    //  是否进行过连接
    public boolean hasConnect = false;

//    //  网络连接是否超时
//    private boolean isMuchTime;

    //  是否扫描到服务
    private Boolean hasService = false;

    private BleManager bleManager;

    private static final int REQUEST_ENABLE_BT = 1000;

    //  向设备发送命令的UUID
    private UUID characWriteUuid = UUID.fromString("00002a32-0000-1000-8000-00805f9b34fb");

    /**
     * -----------------------使用蓝牙返回的信息----------------------
     **/
    private static final int MSG_WHAT_NO_SUPPORT_BLE = 100;
    private static final int MSG_WHAT_NO_SUPPORT_BL = 200;
    private static final int MSG_WHAT_OPEN_BL = 300;
    private static final int MSG_WHAT_FOUND_DEVICE = 400;
    private static final int MSG_WHAT_NO_FOUND_DEVICE = 500;
    private static final int MSG_WHAT_GET_INFO = 600;
    private static final int MSG_HAS_SERVICE = 700;
    private static final int MSG_DIS_CONNET = 800;

    // Stops scanning and connect after 20 seconds.
    private static final long SCAN_PERIOD = 20000;

    int index = 0;

    String cmd[] = {"move", "forward", "backward", "left", "right", "disconnect", "intro_self", "dance",
            "movie", "stop", "head_left", "head_right", "head_up", "head_down", "l_arm_front",
            "l_arm_end", "l_arm_up", "l_arm_down", "r_arm_front", "r_arm_end", "r_arm_up",
            "r_arm_down", "head_front"};
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_offline);
        assignViews();

        showDialog("正在连接蓝牙。。。。");
        if (bluetoothInit()) {
            startDiscoverDevice();
        }
    }

    private void assignViews() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        olInfo = (Button) findViewById(R.id.olInfo);
        olDance = (Button) findViewById(R.id.olDance);
        olAct = (Button) findViewById(R.id.olAct);
        olMovie = (Button) findViewById(R.id.olMovie);

        olStop = (Button) findViewById(R.id.olStop);
        rlAct = (LinearLayout) findViewById(R.id.rlAct);
        pvHead = (PanelView) findViewById(R.id.pvHead);
        pvLarm = (PanelView) findViewById(R.id.pvLarm);
        pvRarm = (PanelView) findViewById(R.id.pvRarm);
        pvFoot = (Rudder) findViewById(R.id.pvFoot);

        ibBack.setOnClickListener(clickListener);
        olInfo.setOnClickListener(clickListener);
        olDance.setOnClickListener(clickListener);
        olAct.setOnClickListener(clickListener);
        rlAct.setOnClickListener(clickListener);

        olMovie.setOnClickListener(clickListener);
        olStop.setOnClickListener(clickListener);
        pvHead.setOnClickListener(clickListener);
        pvLarm.setOnClickListener(clickListener);
        pvRarm.setOnClickListener(clickListener);
        // pvFoot.setOnClickListener(clickListener);
        pvFoot.setRudderListener(rudderListener);

        bleManager = BleManager.getInstance();

        pvHead.setName("头");
        pvHead.setBtn("上", "下", "左", "右");
        pvHead.setHandler(handler);
        pvHead.setIndex(ACT_HEAD_UP, ACT_HEAD_DOWN, ACT_HEAD_LEFT, ACT_HEAD_RIGHT, ACT_HEAD_FRONT);

        pvLarm.setName("左手");
        pvLarm.setBtn("上", "下", "前", "后");
        pvLarm.setHandler(handler);
        pvLarm.setIndex(ACT_ARM_LUP, ACT_ARM_LDOWN, ACT_ARM_LFRONT, ACT_ARM_LEND);

        pvRarm.setName("右手");
        pvRarm.setBtn("上", "下", "前", "后");
        pvRarm.setHandler(handler);
        pvRarm.setIndex(ACT_ARM_RUP, ACT_ARM_RDOWN, ACT_ARM_RFRONT, ACT_ARM_REND);

        /*pvFoot.setName("脚");
        pvFoot.setBtn("前", "后", "左", "右");
        pvFoot.setHandler(handler);
        pvFoot.setIndex(ACT_FORWARD, ACT_BACKWARD, ACT_LEFT, ACT_RIGHT);*/
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibBack:
//                    onBackPressed();
                    finish();
                    break;

                case R.id.olInfo:
                    handler.sendEmptyMessage(ACT_INTRO);
                    break;

                case R.id.olDance:
                    handler.sendEmptyMessage(ACT_DANCE);
                    break;

                case R.id.olMovie:
                    handler.sendEmptyMessage(ACT_MOVIE);
                    break;

                case R.id.olStop:
                    handler.sendEmptyMessage(ACT_STOP);
                    break;

                case R.id.rlAct:// 控制界面
                    // rlAct.setVisibility(View.GONE);
                    break;

                case R.id.olAct:// 动作控制按钮
                    rlAct.setVisibility(View.VISIBLE);
                    pvFoot.setVisibility(View.VISIBLE);
                    isExit = false;
                    new Thread(RobotOfflineActivity.this).start();
                    break;
            }
        }
    };

    //  调用蓝牙管理功能初始化蓝牙设备
    private boolean bluetoothInit() {
        return bleManager.initBluetooth(handler, this);
    }

    //  开始扫描设备
    private void startDiscoverDevice() {
        handler.postDelayed(finish, SCAN_PERIOD);
        bleManager.startScan();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what >= 100) {
                switch (msg.what) {
                    case MSG_WHAT_NO_SUPPORT_BLE:   //不支持BLE
                        tt.showMessage("您的手机不支持蓝牙BLE连接", tt.LONG);
                        finish();
                        break;

                    case MSG_WHAT_NO_SUPPORT_BL:    //不支持bl
                        tt.showMessage("您的手机不支持蓝牙连接", tt.LONG);
                        finish();
                        break;

                    case MSG_WHAT_OPEN_BL:  //打开蓝牙
                        hideDialog();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        break;

                    case MSG_WHAT_FOUND_DEVICE: //发现蓝牙设备
                        //停止扫描
                        bleManager.stopScan();
                        linkDevice = (BluetoothDevice) msg.obj;
                        connect();
                        break;

                    case MSG_WHAT_NO_FOUND_DEVICE:  //没有发现需要的蓝牙设备
                        tt.showMessage("蓝牙类型不匹配", tt.SHORT);
                        break;

                    case MSG_HAS_SERVICE:  //是否扫描到设备的服务
                        hasService = (Boolean) msg.obj;
                        hideDialog();
                        handler.removeCallbacks(finish);
                        if (hasService) {
                            tt.showMessage("检测并连接到设备，可以进行控制", tt.SHORT);
                        } else {
                            tt.showMessage("没有扫描到服务", tt.SHORT);
                            finish();
                        }
                        break;

                    case MSG_DIS_CONNET:    //连接已断开
                        ILog.e("连接已断开");
                        hideDialog();
                        handler.removeCallbacks(finish);
                        tt.showMessage("连接已断开，请重新连接", tt.LONG);
                        finish();
                        break;
                    default:
                        break;
                }
            } else if (msg.what == 0 && msg.obj == null) {
                rlAct.setVisibility(View.GONE);
            } else if (msg.obj == null) {
                index = msg.what;
                sendCMD();
            }
        }
    };

    //  连接设备
    public void connect() {
        hasConnect = true;
        bleManager.connect(linkDevice);
    }

    //  发送命令
    private void sendCMD() {
        showDialog("正在发送控制命令……");
        handler.postDelayed(finish, 30000);
        BluetoothCommand command = new BluetoothCommand();
        command.action = cmd[index];
        sendInfo(command);
    }

    //  向设备发送信息
    private void sendInfo(BluetoothCommand command) {
        if (linkDevice == null) {
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(command);
        String jsonCommand = "DRC" + json + "DRC_SUFFIX";
        ILog.e("向蓝牙设备发送数据：" + jsonCommand);
        bleManager.writeInfo(jsonCommand, characWriteUuid);
        hideDialog();
        handler.removeCallbacks(finish);
    }

    //  超时处理
    Runnable finish = new Runnable() {
        @Override
        public void run() {
            hideDialog();
            if (linkDevice == null) {
                bleManager.stopScan();
                tt.showMessage("未检测到可控制设备", tt.SHORT);
                finish();
            } else if (hasConnect) {
//                isMuchTime = true;
                hasConnect = false;
                tt.showMessage("连接超时……", tt.SHORT);
                cancel();
                finish();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            showDialog("正在连接蓝牙。。。。");
            // 允许打开蓝牙后就进行扫描
            startDiscoverDevice();
            return;
        }
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            hideDialog();
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        hideDialog();
        if (rlAct.getVisibility() == View.VISIBLE) {
            rlAct.setVisibility(View.GONE);
            return;
        }
    }

    public void cancel() {
        try {
            bleManager.close();
        } catch (Exception e) {
            ILog.e(e);
        } finally {
            linkDevice = null;
        }
    }

    @Override
    protected void onDestroy() {
        isExit = true;
        ILog.e("onDestory:断开蓝牙连接");
        hideDialog();
        cancel();
        super.onDestroy();
    }

    private boolean isRudderUse;
    private float mSpeedV;
    private float mSpeedW;
    Rudder.RudderListener rudderListener = new Rudder.RudderListener() {
        @Override
        public void onSteeringWheelChanged(int action, float radius, float radian) {
            isRudderUse = true;
            float tv = (float) (radius * Math.cos(radian));
            float av = (float) (radius * Math.sin(radian));

            mSpeedV = tv * 3;
            mSpeedW = -av * 10;
            Log.e("RobotFoot", "onSteeringWheelChanged:" + tv + "  " + av);
        }

        @Override
        public void onTouchUp() {
            isRudderUse = false;
            mSpeedV = 0;
            mSpeedW = 0;
            Log.e("RobotFoot", "onTouchUp");

            BluetoothCommand command = new BluetoothCommand();
            command.setBluetoothFootCommand(new BluetoothCommand.FootCommand((int) mSpeedV, (int) mSpeedW));
            sendInfo(command);
//            String message = new Gson().toJson(command);
//            message = "DRC" + message + "DRC_SUFFIX";
//            bleManager.writeInfo(message, characWriteUuid);
        }
    };

    @Override
    public void run() {
        BluetoothCommand command = new BluetoothCommand();
        while (!isExit) {
            if (isRudderUse) {
                command.setBluetoothFootCommand(new BluetoothCommand.FootCommand((int) mSpeedV, (int) mSpeedW));
                sendInfo(command);
//                String message = new Gson().toJson(command);
//                message = "DRC" + message + "DRC_SUFFIX";
//                bleManager.writeInfo(message, characWriteUuid);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
