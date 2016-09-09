package coms.geeknewbee.doraemon.robot;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.global.BaseActivity;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.global.SptConfig;
import coms.geeknewbee.doraemon.index.IndexActivity;
import coms.geeknewbee.doraemon.robot.presenter.IRobotBindPresenter;
import coms.geeknewbee.doraemon.robot.utils.BluetoothCommand;
import coms.geeknewbee.doraemon.robot.utils.NetworkStateReceiver;
import coms.geeknewbee.doraemon.utils.ILog;
import coms.geeknewbee.doraemon.utils.Session;
import coms.geeknewbee.doraemon.utils.SoftKeyboardManager;
import coms.geeknewbee.doraemon.utils.StringHandler;

public class RobotWifiActivity extends BaseActivity {

    /**-----------------------组件----------------------**/

    EditText wifi_ssid;

    EditText wifi_password;

    Button wifi_ok;

    ImageButton ibBack;

    /**-----------------------数据----------------------**/
    private static final UUID ROBOT_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");//b5b59b9c-18de-11e6-9409-20c9d0499603
    private BluetoothAdapter adapter;
//    private String ROBOT_BT_NAME = "DoraemonTest";//geeknewbee-robot //sangeyeye
    private OutputStream outputStream;
    private String SSID;

    String type;

    /** 可连接设备 **/
    private BluetoothDevice linkDevice;

    AsyncTask asyncTask;

    private boolean reLink = false;

    boolean stop = false;

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
            handler.postDelayed(finish, 20000);
            bluetoothInit();
        }
    }

    private void assignViews() {
        wifi_ssid = (EditText) findViewById(R.id.wifi_ssid);
        wifi_password = (EditText) findViewById(R.id.wifi_password);
        wifi_ok = (Button) findViewById(R.id.wifi_ok);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        skm = new SoftKeyboardManager(wifi_ssid);

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
        reLink = false;
        wifi_password.setOnFocusChangeListener(focusChangeListener);
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                skm.show();
            }
        }
    };

    private void bluetoothInit() {
        if (!NetworkStateReceiver.isConnected) {
            tt.showMessage("请先配置好您手机的WIFI网络", tt.LONG);
            finish();
        }
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ILog.e("wifiInfo", wifiInfo.toString());
        ILog.e("SSID", wifiInfo.getSSID());

        SSID = wifiInfo.getSSID().replaceAll("\\\"", "");
        wifi_ssid.setText(""+SSID);
        // 检查设备是否支持蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // 设备不支持蓝牙
            tt.showMessage("您的手机不支持蓝牙连接", tt.LONG);
            finish();
        }
        // 打开蓝牙
        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
        //查找已配对的蓝牙设备
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (int i = 0; i < devices.size(); i++) {
            BluetoothDevice device = (BluetoothDevice) devices.iterator().next();
            ILog.e("" + device.getName());
        }
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播接收器，接收并处理搜索结果
        registerReceiver(searchDevices, intentFilter);
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        ILog.e("蓝牙搜索广播开始");
        adapter.startDiscovery();
    }

    private void linkDevice(){
        if(StringHandler.isEmpty(wifi_password.getText().toString())){
            showMsg("系统提示", "WIFI密码不能为空");
            return;
        }
        showDialog("正在设置网络……");
        handler.postDelayed(finish, 35000);
        if(reLink){
            bluetoothInit();
        } else {
            connect();
        }
    }

    private void bindRobot(){
        Intent intent = new Intent(RobotWifiActivity.this, RobotZxingActivity.class);
        startActivity(intent);
    }

    public static String strPsw = "0000";

    //蓝牙设备
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
                String str = "未配对|" + device.getName() + "|" + device.getAddress()+"---"+device.getType();
                ILog.e(str);
                if (GlobalContants.ROBOT_BT_NAME.equalsIgnoreCase(device.getName())) {
                    //&& device.getBondState() == BluetoothDevice.BOND_NONE
                    //蓝牙类型
                    int type = device.getType();
                    ILog.e(device.getName() + "---" + GlobalContants.ROBOT_BT_NAME+"----"+type);

//                    boolean isRightUuid = false;
//                    //空指针了
//                    for (ParcelUuid uuid : device.getUuids()) {
//                        if(uuid.getUuid().equals(ROBOT_UUID)) {
//                            isRightUuid = true;
//                            break;
//                        }
//                    }

                    if (type==BluetoothDevice.DEVICE_TYPE_CLASSIC /*&& device.getUuids()!=null && isRightUuid*/){
                        linkDevice = device;
                        wifi_password.requestFocus();//输入焦点放在此控件上
                        unregisterReceiver(searchDevices);
                        adapter.cancelDiscovery();
                        if(reLink){
                            connect();
                        } else {
                            handler.removeCallbacks(finish);
                            hideDialog();
                            tt.showMessage("检测到设备，请为它设置WIFI", tt.SHORT);
                        }
                    }else{
                        tt.showMessage("蓝牙类型不匹配",tt.SHORT);
                    }
                }
                //如果远程设备的连接状态发生改变
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str = device.getName() + "|" + device.getAddress();
                ILog.e(str);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        ILog.e("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        ILog.e("BlueToothTestActivity", "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        ILog.e("BlueToothTestActivity", "取消配对");
                    default:
                        break;
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
        ILog.e("------------------"+reLink+"--------------------");
        asyncTask = new ConnectTask();
        asyncTask.execute();
    };

    Runnable finish = new Runnable() {
        @Override
        public void run() {
            hideDialog();
            if(linkDevice == null){
                try {
                    unregisterReceiver(searchDevices);
                    adapter.cancelDiscovery();
                } catch (Exception e){}
                if(reLink){
                    tt.showMessage("连接到设备失败，请重新连接", tt.SHORT);
                } else {
                    tt.showMessage("未检测到可连接设备", tt.SHORT);
                }
                finish();
            } else if(asyncTask != null) {
                tt.showMessage("连接超时……", tt.SHORT);
                asyncTask.cancel(true);
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

    public void cancel(){
        try {
            stop = true;
            socket.close();
            if(asyncTask != null){
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
        super.onDestroy();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String response = (String)msg.obj;
            hideDialog();
            if ("1".equals(response)) {//WIFI设置成功
                tt.showMessage("Wifi设置成功", tt.LONG);
                if(type == null){
                    bindRobot();
                    finish();
                } else if(type.equals("reLink")){
                    getIntent().putExtra("ssid", SSID);
                    setResult(0, getIntent());
                    finish();
                }
            } else if ("0".equals(response)) {//WIFI设置失败
                showMsg("系统提示", "Wifi设置失败");
                reLink = true;
            } else {
                showMsg("系统提示", "机器人返回未知数据：" + response);
                reLink = true;
            }
            asyncTask = null;
            handler.removeCallbacks(finish);
        }
    };

    public class ConnectTask extends AsyncTask<Object, Object, String>{
        java.lang.String pwd;

        @Override
        protected void onPreExecute() {
            pwd = wifi_password.getText().toString();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String response) {
            hideDialog();
            if ("1".equals(response)) {//WIFI设置成功
                tt.showMessage("Wifi设置成功", tt.LONG);
                if(type == null){
                    bindRobot();
                    finish();
                } else if(type.equals("reLink")){
                    getIntent().putExtra("ssid", SSID);
                    setResult(0, getIntent());
                    finish();
                }
            } else if ("0".equals(response)) {//WIFI设置失败
                showMsg("系统提示", "Wifi设置失败");
                reLink = true;
            } else {
                showMsg("系统提示", "机器人返回未知数据：" + response);
                reLink = true;
            }
            handler.removeCallbacks(finish);
            super.onPostExecute(response);
        }

        @Override
        protected String doInBackground(Object... params) {

            String response = null;
            int status = 0;
            try {
                stop = false;
                socket = linkDevice.createInsecureRfcommSocketToServiceRecord(ROBOT_UUID);

                // 固定的UUID
                ILog.e("连接蓝牙设备：" + linkDevice.getName());
                socket.connect();
                status = 1;
                ILog.e("-----------------连接成功----------------");

                InputStream inputStream = socket.getInputStream();
                ILog.e("inputStream : " + inputStream);
                outputStream = new BufferedOutputStream(socket.getOutputStream());
                ILog.e("outputStream : " + outputStream);

                ILog.e("向蓝牙设备发送数据：" + linkDevice.getName() + "[" + SSID + "|" + pwd + "|3EOM]");
//                String sendData = SSID + "|" + pwd + "|3EOM";

                Gson gson=new Gson();
                BluetoothCommand bluetoothCommand=new BluetoothCommand(new BluetoothCommand.WifiInfo(3,SSID,pwd));
                /*bluetoothCommand.wifiInfo.type=3;
                bluetoothCommand.wifiInfo.SSID=SSID;
                bluetoothCommand.wifiInfo.pwd=pwd;*/

                String send = gson.toJson(bluetoothCommand);
                String sendData = "COMMAND_ROBOT"+send+"COMMAND_ROBOT_SUFFIX";
                ILog.e("BluetoothCommand", "sendData--json: "+sendData);

                write(sendData.getBytes());
                byte[] data = new byte[40];
                int dataSize = 0;
                while (true) {
                    try {
                        dataSize = inputStream.read(data);
                        if (dataSize > 0) {
                            response = new java.lang.String(data, 0, dataSize);
                            ILog.e("读取到数据：" + response);
                            break;
                        }
                        if(socket == null || linkDevice == null || stop){
                            break;
                        }
                    } catch (Exception e) {
                        dataSize = 0;
                        ILog.e(e);
                        break;
                    }
                }
                status = 2;
                asyncTask = null;
                try {
                    RobotWifiActivity.this.cancel();
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {

                }

                ILog.e("断开蓝牙设备……");
            } catch (IOException e) {
                Log.e("连接异常", e.getStackTrace().toString());
                ILog.e(e);
                if(status == 0){
                    try {
                        RobotWifiActivity.this.cancel();
                    } catch (Exception e2) {
                        ILog.e(e2);
                    }
                    reLink = true;
                    bluetoothInit();
                } else if(status == 1){
                    try {
                        outputStream.close();
                        RobotWifiActivity.this.cancel();
                    } catch (Exception e2) {
                        ILog.e(e2);
                    }
                    reLink = true;
                    bluetoothInit();
                }
            }
            return response;
        }
    };
}
