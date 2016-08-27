package coms.geeknewbee.doraemon.robot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.box.movie.bean.MovieBean;
import coms.geeknewbee.doraemon.box.movie.view.IMovieView;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.utils.BluetoothCommand;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.widget.PanelView;
import coms.geeknewbee.doraemon.widget.Rudder;

public class RobotOfflineActivity extends BaseActivity implements Runnable{

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
     * -----------------------数据----------------------
     **/
    private static final UUID ROBOT_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private BluetoothAdapter adapter;
    private String ROBOT_BT_NAME = "sangeyeye";//"geeknewbee-robot";
    private OutputStream outputStream;

    /**
     * 可连接设备
     **/
    private BluetoothDevice linkDevice;

    AsyncTask asyncTask;

    boolean stop = false;
    public boolean isConnect = false;

    int index = 0;

    String cmd[] = {"move", "forward", "backward", "left", "right", "disconnect", "intro_self", "dance",
            "movie", "stop", "head_left", "head_right", "head_up", "head_down", "l_arm_front",
            "l_arm_end", "l_arm_up", "l_arm_down", "r_arm_front", "r_arm_end", "r_arm_up",
            "r_arm_down", "head_front"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_offline);
        assignViews();

        showDialog("正在连接蓝牙。。。。");
//        handler.postDelayed(finish, 20000);
        bluetoothInit();
//        handler.postDelayed(bluetoothConnect,10000);
    }

    Runnable bluetoothConnect = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            if (socket == null) {
                try {
                    socket = linkDevice.createInsecureRfcommSocketToServiceRecord(ROBOT_UUID);

                    socket.connect();
                    outputStream = new BufferedOutputStream(socket.getOutputStream());
                    ILog.e("outputStream : " + outputStream);

                    hideDialog();
                    tt.showMessage("检测到设备，可以进行控制",tt.SHORT);
                    Log.d("bluetoothConnect", "检测到设备，可以进行控制");

                } catch (Exception e) {
                    Log.d("蓝牙连接异常", "bluetoothConnect---" + e.getStackTrace().toString());
                    hideDialog();
                    tt.showMessage("蓝牙连接失败",tt.SHORT);
                    RobotOfflineActivity.this.finish();
                }
            }
            Looper.loop();
        }
    };

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
                    new Thread(RobotOfflineActivity.this).start();
                    break;
            }
        }
    };



    private void bluetoothInit() {
        // 检查设备是否支持蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // 设备不支持蓝牙
            tt.showMessage("您的手机不支持蓝牙连接", tt.LONG);
            finish();
        }

        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播接收器，接收并处理搜索结果
        registerReceiver(searchDevices, intentFilter);
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        ILog.e("蓝牙搜索广播开始");

        // 打开蓝牙
        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        } else
            adapter.startDiscovery();
    }

    private void sendCMD() {
        showDialog("正在发送控制命令……");
        handler.postDelayed(finish,10000);
        connect();
    }

    private void bindRobot() {
        Intent intent = new Intent(RobotOfflineActivity.this, RobotZxingActivity.class);
        startActivity(intent);
    }

    private BluetoothSocket socket;
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            ILog.e("--------------------------");
            BluetoothDevice device = null;
            // 搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str = "未配对|" + device.getName() + "|" + device.getAddress();
                ILog.e(str);
                Log.e("device_name", device.getName());
                if (ROBOT_BT_NAME.equalsIgnoreCase(device.getName())) {
                    //&& device.getBondState() == BluetoothDevice.BOND_NONE
                    ILog.e(device.getName() + "|" + ROBOT_BT_NAME);
                    //蓝牙类型
                    int type = device.getType();
                    if (type == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        linkDevice = device;
                        unregisterReceiver(searchDevices);
                        adapter.cancelDiscovery();
                        handler.removeCallbacks(finish);
                        new Thread(bluetoothConnect).start();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //当扫描结束的时候判断 如果还没有设置 就提示没有发现nsheb
                // TODO:扫描完成
                if (linkDevice == null) {
                    hideDialog();
                    tt.showMessage("未发现可用设备",tt.SHORT);
                    finish();
                }
            }
        }
    };

    /* Call this from the main Activity to send data to the remote device */
    public void write(byte[] bytes) throws IOException {
        if (outputStream == null)
            throw new IllegalStateException("Wait connection to be opened");
        outputStream.write(bytes);
        outputStream.flush();
    }

    public void connect() {
        asyncTask = new ConnectTask();
        asyncTask.execute();
    }

    ;

    Runnable finish = new Runnable() {
        @Override
        public void run() {
            hideDialog();
            if (linkDevice == null) {
                try {
                    unregisterReceiver(searchDevices);
                    adapter.cancelDiscovery();
                } catch (Exception e) {
                }
                tt.showMessage("未检测到可连接设备", tt.SHORT);
                finish();
            } else if (asyncTask != null) {
                tt.showMessage("连接超时……", tt.SHORT);
                asyncTask.cancel(true);
                cancel();
                finish();
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (rlAct.getVisibility() == View.VISIBLE) {
            rlAct.setVisibility(View.GONE);
            return;
        }
        // index = ACT_DISCONNECT;
        // sendCMD();
    }

    public void cancel() {
        try {
            stop = true;
            socket.close();
            socket = null;
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }
        } catch (Exception e) {
            ILog.e(e);
        } finally {
            linkDevice = null;
            socket = null;
        }
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        cancel();
        super.onDestroy();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj == null) {
                if (msg.what == 0) {
                    rlAct.setVisibility(View.GONE);
                } else {
                    index = msg.what;
                    sendCMD();
                }
                return;
            }
        }
    };

    public class ConnectTask extends AsyncTask<Object, Object, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String response) {
            hideDialog();
            handler.removeCallbacks(finish);
            if ("1".equals(response)) {
                tt.showMessage("控制命令发送成功", tt.SHORT);
            } else if ("0".equals(response)) {
                finish();
            } else {
                tt.showMessage("机器人返回未知数据：" + response, tt.SHORT);
            }
            super.onPostExecute(response);
        }

        @Override
        protected String doInBackground(Object... params) {

            if (linkDevice == null) {
                if (index == ACT_DISCONNECT || stop) {
                    try {
                        RobotOfflineActivity.this.cancel();
                        finish();
                    } catch (Exception e) {

                    }
                }
                return null;
            }
            int status = 0;
            try {

                ILog.e("向蓝牙设备发送数据：" + linkDevice.getName() + "[" + cmd[index] + "]");
                //u
                if (outputStream == null) {
                    Log.d("outputStream", "doInBackground: ----"+outputStream);
                    return "0";
                }
                Gson gson = new Gson();
                BluetoothCommand bluetoothCommand = new BluetoothCommand();
                bluetoothCommand.action = cmd[index];
                String json = gson.toJson(bluetoothCommand);
                String jsonCommand = "COMMAND_ROBOT" + json + "COMMAND_ROBOT_SUFFIX";
                Log.d("BluetoothCommand", "jsonCommand----: " + jsonCommand);

                write(jsonCommand.getBytes());

//                write(cmd[index].getBytes());
                status = 2;
                asyncTask = null;
                if (index == ACT_DISCONNECT || stop) {
                    try {
                        RobotOfflineActivity.this.cancel();
                        outputStream.close();
                        finish();
                    } catch (Exception e) {

                    }
                    ILog.e("断开蓝牙设备……");
                    return "0";
                }
            } catch (IOException e) {
                ILog.e(e);
                if (index == ACT_DISCONNECT || stop) {
                    try {
                        finish();
                        RobotOfflineActivity.this.cancel();
                        outputStream.close();
                    } catch (Exception ex) {

                    }
                    ILog.e("断开蓝牙设备……");
                    return "0";
                }
                if (status == 0) {
                    return null;
                } else if (status == 1) {
                }
            }
            return "1";
        }
    };

    private boolean isRudderUse;
    private float mSpeedV;
    private float mSpeedW;
    Rudder.RudderListener rudderListener = new Rudder.RudderListener() {

        BluetoothCommand command = new BluetoothCommand();
        BluetoothCommand.LimbCommand limbCommand = new BluetoothCommand.LimbCommand();

        @Override
        public void onSteeringWheelChanged(int action, float radius, float radian) {
            isRudderUse = true;
            float tv = (float) (radius * Math.cos(radian));
            float av = (float) (radius * Math.sin(radian));

            mSpeedV = tv * 3;
            mSpeedW = -av * 10;
            Log.d("RobotFoot", "onSteeringWheelChanged:" + tv + "  " + av);
        }

        @Override
        public void onTouchUp() {
            isRudderUse = false;
            mSpeedV = 0;
            mSpeedW = 0;
            Log.d("RobotFoot", "onTouchUp");

            limbCommand.setFootCommand(new BluetoothCommand.LimbCommand.FootCommand((int) mSpeedV, (int) mSpeedW));
            command.setLimbCommand(limbCommand);
            try {
                if (outputStream != null) {
                    String message = new Gson().toJson(command);
                    message = "COMMAND_ROBOT" + message + "COMMAND_ROBOT_SUFFIX";
                    write(message.getBytes());
                } else {
                    Log.d("Bluetooth_Control", "无连接");
                }
                // mBluetoothUtils.sendMessage(new Gson().toJson(command));
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (outputStream != null) {
                    String message = new Gson().toJson(command);
                    message = "COMMAND_ROBOT" + message + "COMMAND_ROBOT_SUFFIX";
                    write(message.getBytes());
                } else {
                    Log.d("Bluetooth_Control", "无连接");
                }
                // mBluetoothUtils.sendMessage(new Gson().toJson(command)); //有时候一次停止失败,这里发两次
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void run() {
        BluetoothCommand command = new BluetoothCommand();
        BluetoothCommand.LimbCommand limbCommand = new BluetoothCommand.LimbCommand();
        while (true) {
            if (isRudderUse) {
                limbCommand.setFootCommand(new BluetoothCommand.LimbCommand.FootCommand((int) mSpeedV, (int) mSpeedW));
                command.setLimbCommand(limbCommand);
                try {
                    if (outputStream != null) {
                        String message = new Gson().toJson(command);
                        message = "COMMAND_ROBOT" + message + "COMMAND_ROBOT_SUFFIX";
                        write(message.getBytes());
                    } else {
                        Log.d("Bluetooth_Control", "无连接");
                    }
                    // mBluetoothUtils.sendMessage(new Gson().toJson(command));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
