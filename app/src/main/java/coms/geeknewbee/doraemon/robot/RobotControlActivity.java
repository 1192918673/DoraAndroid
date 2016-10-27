package coms.geeknewbee.doraemon.robot;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.communicate.BLE.BleManager;
import coms.geeknewbee.doraemon.communicate.IControl;
import coms.geeknewbee.doraemon.communicate.socket.SocketManager;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.robot.readface.ReadFaceActivity;
import coms.geeknewbee.doraemon.robot.utils.BluetoothCommand;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.widget.PanelView;
import coms.geeknewbee.doraemon.widget.Rudder;

public class RobotControlActivity extends BaseActivity implements Runnable {

    private static final int ACT_MOVE = 0;

    private static final int ACT_FORWARD = 1;

    private static final int ACT_BACKWARD = 2;

    private static final int ACT_LEFT = 3;

    private static final int ACT_RIGHT = 4;

    private static final int ACT_DISCONNECT = 5;

    private static final int ACT_INTRO = 6;

//    private static final int ACT_DANCE = 7;

//    private static final int ACT_MOVIE = 8;

//    private static final int ACT_STOP = 9;

    private static final int ACT_HEAD_LEFT = 7;

    private static final int ACT_HEAD_RIGHT = 8;

    private static final int ACT_HEAD_UP = 9;

    private static final int ACT_HEAD_DOWN = 10;

    private static final int ACT_ARM_LFRONT = 11;

    private static final int ACT_ARM_LEND = 12;

    private static final int ACT_ARM_LUP = 13;

    private static final int ACT_ARM_LDOWN = 14;

    private static final int ACT_ARM_RFRONT = 15;

    private static final int ACT_ARM_REND = 16;

    private static final int ACT_ARM_RUP = 17;

    private static final int ACT_ARM_RDOWN = 18;

    private static final int ACT_HEAD_FRONT = 19;

    private static final int ACT_SAY_HI = 20;

    private static final int ACT_END_SAY = 21;

//    private static final int ACT_READ_NEWS = 22;

    //    private static final int ACT_SLEEP = 26;
    //呼和浩特使用
    private static final int ACT_CAN_DO = 22;
    private static final int ACT_HOW_TO_TEACH = 23;
    private static final int ACT_FOR_OLD = 24;
    private static final int ACT_HH = 25;

    /**
     * -----------------------组件----------------------
     **/

    Button olEmotion;

    Button olReadFace;

    TextView tv_control;

    Button olSayhi;

    Button olEnd_say;

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

    Button bt_go;
    Button bt_back;
    Button bt_stop;

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

    public static IControl control;

    private static final int REQUEST_ENABLE_BT = 1000;

    /**
     * -----------------------使用socket返回的信息----------------------
     **/
    private final int MSG_WHAT_SOCKET_CONNECT = 1000;
    private final int MSG_WHAT_SOCKET_DISCONNECT = 1001;
    private final int MSG_WHAT_CONNECT_FAILED = 1006;

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

    // Stops scanning and connect after 30 seconds.
    private static final long SCAN_PERIOD = 30000;
    // socket连接时长10s
    private static final long SOCKET_PERIOD = 20000;

    int index = 0;
    private List<String> lines;
    //    String cmd[] = {"move", "forward", "backward", "left", "right", "disconnect", "intro_self", "dance", "movie", "stop",
//            "head_left", "head_right", "head_up", "head_down", "l_arm_front", "l_arm_end", "l_arm_up", "l_arm_down", "r_arm_front", "r_arm_end",
//            "r_arm_up", "r_arm_down", "head_front", "say_hi", "end_say", "read_news", "sleep", "can_do", "how_teach", "for_old",
//            "hh"};
    private boolean isExit = false;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_offline);
        ip = getIntent().getStringExtra("ip");
        assignViews();
        lines = new ArrayList<>();
        showDialog("正在连接。。。。");
        if (controlInit()) {
            if (ip == null) {
                startDiscoverDevice();
            } else {
                //  使用socket，此时进行连接
//                handler.postDelayed(finish, SOCKET_PERIOD);
                connect();
            }
        }
    }

    private void assignViews() {
        tv_control = (TextView) findViewById(R.id.tv_control);
        olReadFace = (Button) findViewById(R.id.olReadFace);

        if (ip == null) {
            control = BleManager.getInstance();
            tv_control.setText("蓝牙控制");
            olReadFace.setVisibility(View.GONE);
        } else {
            control = SocketManager.getInstance();
            tv_control.setText("Socket控制");
            olReadFace.setVisibility(View.VISIBLE);
        }
        olEmotion = (Button) findViewById(R.id.olEmoticon);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        olSayhi = (Button) findViewById(R.id.olSayhi);
        olEnd_say = (Button) findViewById(R.id.olEnd_say);
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

        bt_go = (Button) findViewById(R.id.bt_go);
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_stop = (Button) findViewById(R.id.bt_stop);

        olReadFace.setOnClickListener(clickListener);
        olEmotion.setOnClickListener(clickListener);

        ibBack.setOnClickListener(clickListener);
        olSayhi.setOnClickListener(clickListener);
        olEnd_say.setOnClickListener(clickListener);
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

        bt_go.setOnClickListener(clickListener);
        bt_back.setOnClickListener(clickListener);
        bt_stop.setOnClickListener(clickListener);

        //呼和浩特添加
        findViewById(R.id.olCanDo).setOnClickListener(clickListener);
        findViewById(R.id.olHowToTeach).setOnClickListener(clickListener);
        findViewById(R.id.olForOld).setOnClickListener(clickListener);
        findViewById(R.id.olHH).setOnClickListener(clickListener);

        findViewById(R.id.olSleep).setOnClickListener(clickListener);

        findViewById(R.id.olTest).setOnClickListener(clickListener);
        findViewById(R.id.bt_left).setOnClickListener(clickListener);
        findViewById(R.id.bt_right).setOnClickListener(clickListener);
        findViewById(R.id.bt_stopFoot).setOnClickListener(clickListener);

        //自动演示
        findViewById(R.id.olOpen).setOnClickListener(clickListener);
        findViewById(R.id.olClose).setOnClickListener(clickListener);

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
            BluetoothCommand command = new BluetoothCommand();
            switch (v.getId()) {
                case R.id.ibBack:
//                    onBackPressed();
                    toRobotActivity();
                    finish();
                    break;
                //自动演示
                case R.id.olOpen:
                    command.action = "open_auto_demonstration";
                    sendInfo(command);
                    break;
                case R.id.olClose:
                    command.action = "close_auto_demonstration";
                    sendInfo(command);
                    break;

                //呼和浩特使用
                case R.id.olCanDo:
                    handler.sendEmptyMessage(ACT_CAN_DO);
                    break;
                case R.id.olHowToTeach:
                    handler.sendEmptyMessage(ACT_HOW_TO_TEACH);
                    break;
                case R.id.olForOld:
                    handler.sendEmptyMessage(ACT_FOR_OLD);
                    break;
                case R.id.olHH:
                    handler.sendEmptyMessage(ACT_HH);
                    break;

                case R.id.olReadFace:    //添加人脸
                    Intent intent_addface = new Intent(RobotControlActivity.this, ReadFaceActivity.class);
                    startActivity(intent_addface);
                    break;

                case R.id.olEmoticon:   //表情
                    Intent intent_Emotion = new Intent(RobotControlActivity.this, EmotionActivity.class);
                    intent_Emotion.putExtra("ip", ip);
                    startActivity(intent_Emotion);
                    break;

                case R.id.olSayhi:
                    handler.sendEmptyMessage(ACT_SAY_HI);
                    break;

                case R.id.olEnd_say:
                    handler.sendEmptyMessage(ACT_END_SAY);
                    break;

                case R.id.olInfo:
                    handler.sendEmptyMessage(ACT_INTRO);
                    break;

                case R.id.olDance:
                    command.action = "dance";
                    sendInfo(command);
                    break;

//                case R.id.olMovie:
//                    handler.sendEmptyMessage(ACT_MOVIE);
//                    break;

                case R.id.olStop:
                    command.action = "stop";
                    sendInfo(command);
                    break;

                case R.id.olSleep:  //休眠
                    command.action = "sleep";
                    sendInfo(command);
                    break;

                case R.id.rlAct:// 控制界面
                    // rlAct.setVisibility(View.GONE);
                    break;

                case R.id.olAct:// 动作控制按钮
                    rlAct.setVisibility(View.VISIBLE);
                    pvFoot.setVisibility(View.VISIBLE);
                    isExit = false;
                    new Thread(RobotControlActivity.this).start();
                    break;

                case R.id.bt_left:  //向左转
                    handler.sendEmptyMessage(ACT_LEFT);
                    break;

                case R.id.bt_right:  //向右转
                    handler.sendEmptyMessage(ACT_RIGHT);
                    break;

                case R.id.bt_stopFoot:  //脚步停止
                    mSpeedV = 0;
                    mSpeedW = 0;
                    command.setBluetoothFootCommand(new BluetoothCommand.FootCommand(0, 0));
                    sendInfo(command);
                    isRudderUse = false;
                    break;

                case R.id.bt_go:    //点击前进
                    mSpeedV = 1000;
                    mSpeedW = 0;
                    isRudderUse = true;
                    break;
                case R.id.bt_back:    //点击后退
                    mSpeedV = -1000;
                    mSpeedW = 0;
                    isRudderUse = true;
                    break;
                case R.id.bt_stop:  //点击停止
                    mSpeedV = 0;
                    mSpeedW = 0;
                    command.setBluetoothFootCommand(new BluetoothCommand.FootCommand(0, 0));
                    sendInfo(command);
                    isRudderUse = false;
                    break;

                case R.id.olTest:   //跳转到测试界面
                    Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                    intent.putExtra("ip", ip);
                    startActivity(intent);
                    break;

            }
        }
    };

    private void toRobotActivity() {
        Intent intent = new Intent(RobotControlActivity.this, RobotActivity.class);
        startActivity(intent);
    }

    //  初始化操作
    private boolean controlInit() {
        return control.init(handler, this);
    }

    //  开始扫描设备
    private void startDiscoverDevice() {
        handler.postDelayed(finish, SCAN_PERIOD);
        control.startScan();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what >= 100) {
                switch (msg.what) {
                    case MSG_WHAT_CONNECT_FAILED:  //socket连接失败
                        ILog.e("socket连接失败");
                        tt.showMessage("socket连接失败，请设置wifi", tt.LONG);
                        Intent intent = new Intent(RobotControlActivity.this, RobotWifiActivity.class);
                        intent.putExtra("type", "control");
                        startActivity(intent);
                        finish();
                        break;
                    case MSG_WHAT_SOCKET_CONNECT:   //socket连接成功
                        hideDialog();
                        handler.removeCallbacks(finish);
                        tt.showMessage("连接到设备，可以进行控制", tt.SHORT);
                        break;

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

                    case MSG_WHAT_SOCKET_DISCONNECT:    //socket连接断开

                    case MSG_DIS_CONNET:    //蓝牙连接已断开
                        ILog.e("连接已断开");
                        hideDialog();
                        handler.removeCallbacks(finish);
                        if (!isExit) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RobotControlActivity.this)
                                    .setTitle("温馨提示")
                                    .setMessage("连接已断开，请重新连接,点击确认关闭当前页面")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            builder.show();
                        }
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
        control.connect(ip);
    }

    //初始化动作集合
    private void initLines(int rawId) {
        if (rawId == -1) {
            return;
        }
        //清空动作集合
        lines.clear();
        //获取raw文件输入流
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(rawId)));
        String line;
        try {
            while ((line = bufReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //  发送命令
    private void sendCMD() {
        ILog.e("正在发送控制命令");
        tt.showMessage("正在发送控制命令", tt.LONG);
        BluetoothCommand command = new BluetoothCommand();
//        command.action = cmd[index];
        command.setFaceName(GlobalContants.img[index]);
        command.setLoop(5);
        command.setSound(GlobalContants.word[index]);
        initLines(GlobalContants.act[index]);
        command.setLines(lines);
        sendInfo(command);
    }

    //  向设备发送信息
    private void sendInfo(BluetoothCommand command) {
        if (ip == null && linkDevice == null) {
            tt.showMessage("没有可控制设备", tt.SHORT);
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(command);
        String jsonCommand = "";
        if (ip == null) {
            jsonCommand = GlobalContants.COMMAND_ROBOT_PREFIX + json + GlobalContants.COMMAND_ROBOT_SUFFIX;
        } else {
            jsonCommand = GlobalContants.COMMAND_ROBOT_PREFIX_FOR_SOCKET + GlobalContants.SEND_SOCKET_CONTROL
                    + json + GlobalContants.COMMAND_ROBOT_SUFFIX_FOR_SOCKET;
        }
        ILog.e("发送数据：" + jsonCommand);
        control.writeInfo(jsonCommand.getBytes(), 2);
    }

    //  超时处理
    Runnable finish = new Runnable() {
        @Override
        public void run() {
            hideDialog();
            if (ip == null && linkDevice == null) {
                control.stopScan();
                tt.showMessage("未检测到可控制设备", tt.SHORT);
                finish();
            } else if (hasConnect) {
//                isMuchTime = true;
                hasConnect = false;
                tt.showMessage("连接超时……", tt.SHORT);
                cancel();
                finish();
            } else {
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
        if (rlAct.getVisibility() == View.VISIBLE) {
            rlAct.setVisibility(View.GONE);
            return;
        } else {
            toRobotActivity();
            finish();
        }
    }

    public void cancel() {
        try {
            if (control != null) {
                control.close();
            }
        } catch (Exception e) {
            ILog.e(e);
        } finally {
            linkDevice = null;
        }
    }

    @Override
    protected void onDestroy() {
        isExit = true;
        ILog.e("onDestory:断开连接");
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
        }
    };

    @Override
    public void run() {
        BluetoothCommand command = new BluetoothCommand();
        while (!isExit) {
            if (isRudderUse) {
                if (mSpeedV < -300) {
                    mSpeedV = -300;
                } else if (mSpeedV > 300) {
                    mSpeedV = 300;
                }
                if (mSpeedW < -400) {
                    mSpeedW = -400;
                } else if (mSpeedW > 400) {
                    mSpeedW = 400;
                }
                command.setBluetoothFootCommand(new BluetoothCommand.FootCommand((int) mSpeedV, (int) mSpeedW));
                sendInfo(command);
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
