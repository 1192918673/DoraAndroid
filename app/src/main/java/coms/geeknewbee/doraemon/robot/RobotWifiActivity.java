package coms.geeknewbee.doraemon.robot;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.robot.bean.BTPostBackCommand;
import coms.geeknewbee.doraemon.robot.bean.RobotBean;
import coms.geeknewbee.doraemon.robot.presenter.IRobotBindPresenter;
import coms.geeknewbee.doraemon.robot.utils.BluetoothCommand;
import coms.geeknewbee.doraemon.robot.utils.NetworkStateReceiver;
import coms.geeknewbee.doraemon.robot.view.IBindView;
import coms.geeknewbee.doraemon.communicate.BLE.BleManager;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class RobotWifiActivity extends BaseActivity
        implements IBindView {

    /**
     * -----------------------组件----------------------
     **/

    EditText wifi_ssid;

    EditText wifi_password;

    Button wifi_ok;

    ImageButton ibBack;


    IRobotBindPresenter bindPresenter;
    BleManager bleManager;

    private String SSID;
    private String serialNo;

    String type;

    public static String strPsw = "0000";


    /**
     * 可连接设备
     **/
    private BluetoothDevice linkDevice;

    boolean hasService = false;

    private static final int REQUEST_ENABLE_BT = 1000;

    //  是否进行过连接
    public boolean hasConnect = false;

    //  网络连接是否超时
    private boolean isMuchTime;

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

    // Stops scanning after 30 seconds.
    private static final long SCAN_PERIOD = 10000;

    Gson gson = new Gson();
    private String send;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_wifi);

        assignViews();

        if (!NetworkStateReceiver.isNetworkAvailable(this)) {
            tt.showMessage("请先配置好您手机的WIFI网络", tt.LONG);
            finish();
        } else {
            showDialog("正在检测可以连接的设备……");
            if (bluetoothInit())
                startDiscoveryDevice();
        }
    }

    private void assignViews() {
        wifi_ssid = (EditText) findViewById(R.id.wifi_ssid);
        wifi_password = (EditText) findViewById(R.id.wifi_password);
        wifi_ok = (Button) findViewById(R.id.wifi_ok);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        skm = new SoftKeyboardManager(wifi_ssid);
        bleManager = BleManager.getInstance();
        bindPresenter = new IRobotBindPresenter(this);

        wifi_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linkDevice();
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
                cancel();
                finish();
            }
        });
        type = getIntent().getStringExtra("type");
        wifi_password.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                skm.show();
            }
        }
    };

    private boolean bluetoothInit() {
        if (!NetworkStateReceiver.isConnected) {
            tt.showMessage("请先配置好您手机的WIFI网络", tt.LONG);
            finish();
        }
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ILog.e("wifiInfo", wifiInfo.toString());
        ILog.e("SSID", wifiInfo.getSSID());

        SSID = wifiInfo.getSSID().replaceAll("\\\"", "");
        wifi_ssid.setText("" + SSID);

        //调用蓝牙管理功能初始化蓝牙设备
        boolean isSuccess = bleManager.init(handler, this);
        return isSuccess;
    }

    private void startDiscoveryDevice() {
        handler.postDelayed(finish, SCAN_PERIOD);
        //  初始化成功，开始扫描
        bleManager.startScan();
    }

    //蓝牙调用的结果处理
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    break;

                case MSG_WHAT_FOUND_DEVICE: //发现蓝牙设备
                    //停止扫描
                    bleManager.stopScan();
                    handler.removeCallbacks(finish);
                    linkDevice = (BluetoothDevice) msg.obj;
                    wifi_password.requestFocus();//输入焦点放在此控件上
                    hideDialog();
                    tt.showMessage("检测到设备，请为它设置WIFI", tt.SHORT);
                    break;

                case MSG_WHAT_NO_FOUND_DEVICE:  //没有发现需要的蓝牙设备
                    tt.showMessage("蓝牙类型不匹配", tt.SHORT);
                    break;

                case MSG_DIS_CONNET:    //连接已断开
                    if (!isMuchTime) {
                        hideDialog();
                        removeCallbacks(finish);
                        tt.showMessage("连接已断开，请重新设置wifi进行连接", tt.LONG);
                    }
                    break;

                case MSG_HAS_SERVICE:  //是否扫描到设备的服务
                    hasService = (Boolean) msg.obj;
                    if (hasService) {
                        //  将对象转换为json类型的字符串进行发送
                        BluetoothCommand bluetoothCommand = new BluetoothCommand(new BluetoothCommand.WifiInfo(3, SSID, pwd));
                        //  字符串是否为空可以作为是否进行过设备连接的依据
                        send = gson.toJson(bluetoothCommand);
                        String sendData = GlobalContants.COMMAND_ROBOT_PREFIX + send + GlobalContants.COMMAND_ROBOT_SUFFIX;
                        ILog.e("发送消息：" + sendData);
                        bleManager.writeInfo(sendData, 1);
                    } else {
                        tt.showMessage("未扫描到服务", tt.SHORT);
                    }
                    break;

                case MSG_WHAT_GET_INFO:  //从设备获取到信息
                    handler.removeCallbacks(finish);
                    hideDialog();
                    String info = (String) msg.obj;
                    //  获取到的是JSON字符串，需要将其转换为对象BTPostBackCommand
                    BTPostBackCommand command = gson.fromJson(info, BTPostBackCommand.class);
                    BTPostBackCommand.SetWIFICallBack back = command.getWifiCallBack();
                    boolean isSuccess = back.isSuccess;
                    boolean hadBind = back.hadBound;
                    //  返回的信息
                    String content = back.content;
                    if (isSuccess) {
                        if (type != null) {
                            if (type.equals("reLink")) {
                                ILog.e("WIFI设置成功");
                                tt.showMessage("Wifi设置成功", tt.LONG);
                                getIntent().putExtra("ssid", SSID);
                                setResult(0, getIntent());
                                finish();
                                return;
                            } else if (type.equals("control")) {
                                ILog.e("WIFI设置成功,进入控制界面");
                                tt.showMessage("Wifi设置成功,进入控制界面", tt.LONG);
                                Intent intent_control = new Intent(RobotWifiActivity.this, RobotControlActivity.class);
                                String ip = back.ipAddress;
                                intent_control.putExtra("ip", ip);
                                startActivity(intent_control);
                                finish();
                                return;
                            }
                        }

                        if (hadBind) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(RobotWifiActivity.this)
                                    .setTitle("系统提示").setMessage("该机器猫已经被绑定，请联系管理员添加您为该机器猫用户！")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            dialog.show();
                            return;
                        }
                        ILog.e("WIFI设置成功");
                        tt.showMessage("Wifi设置成功", tt.LONG);
                        // 绑定机器人
                        String code = content.substring(content.indexOf("qr/") + 3);
                        serialNo = code;
                        bindPresenter.bindRobot();
                        showDialog("正在绑定机器人……");

                    } else {
                        showMsg("系统提示", "Wifi设置失败");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //  连接设备
    private void linkDevice() {
        pwd = wifi_password.getText().toString();
        if (StringHandler.isEmpty(pwd)) {
            showMsg("系统提示", "WIFI密码不能为空");
            return;
        }
        showDialog("正在设置网络……");
        handler.postDelayed(finish, 35000);
        // 连接设备
        hasConnect = true;
        bleManager.connect(null);
    }

    //  超时操作
    Runnable finish = new Runnable() {
        @Override
        public void run() {
            hideDialog();
            // 未扫描到可连接设备，停止扫描
            if (linkDevice == null) {
                bleManager.stopScan();
                tt.showMessage("未检测到可连接设备", tt.SHORT);
                finish();
                //  wifi设置超时处理
            } else if (hasConnect) {
                hasConnect = false;
                isMuchTime = true;
                tt.showMessage("连接超时……", tt.SHORT);
                cancel();
                finish();
            }
        }
    };

    @Override
    public void onBackPressed() {
        hideDialog();
        cancel();
        finish();
        super.onBackPressed();
    }

    public void cancel() {
        try {
            bleManager.close();
        } catch (Exception e) {
            ILog.e(e);
        } finally {
            linkDevice = null;
            send = null;
        }
    }

    @Override
    protected void onDestroy() {
        hideDialog();
        cancel();
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
        showMessage("绑定成功！");
    }

    @Override
    public void showMessage(String msg) {
        hideDialog();
        tt.showMessage("" + msg, tt.LONG);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            // 允许打开蓝牙后就进行扫描
            startDiscoveryDevice();
            return;
        }
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

