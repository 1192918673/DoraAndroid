package coms.geeknewbee.doraemon.communicate.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.List;
import java.util.UUID;

import coms.geeknewbee.doraemon.communicate.IControl;
import coms.geeknewbee.doraemon.global.GlobalContants;
import coms.geeknewbee.doraemon.utils.ILog;

/**
 * Created by GYY on 2016/9/9.
 */
public class BleManager implements IControl{
    public static boolean isConnect;
    private static BleManager bleManager = new BleManager();

    private BleRead bleRead;
    private BleSender bleSender;

    private static Handler handler;
    private static Context context;

    private static BluetoothDevice device;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothGatt mBluetoothGatt;

    // Stops scanning after 30 seconds.
    private static final long SCAN_PERIOD = 10000;

    //  固定的
    private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    //  服务的uuid
    private static UUID SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    //  读charac的uuid
    private static UUID CHARAC_READ_UUID = UUID.fromString("00002a31-0000-1000-8000-00805f9b34fb");
    //  向设备设置wifi的uuid
    private static UUID wifiWriteUUID = UUID.fromString("00002a30-0000-1000-8000-00805f9b34fb");
    //  向设备发送控制命令的uuid
    private static UUID controlWriteUuid = UUID.fromString("00002a32-0000-1000-8000-00805f9b34fb");

    private static final UUID[] ROBOT_UUID = new UUID[]{SERVICE_UUID};


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

    public BleManager() {
        bleSender = new BleSender();
        bleRead = new BleRead();
    }

    public static BleManager getInstance() {
        return bleManager;
    }

    @Override
    public boolean init(Handler handler, Context context) {

        this.handler = handler;
        this.context = context;

        //  判断当前手机是否支持BLE
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_NO_SUPPORT_BLE;
            handler.sendMessage(msg);
            return false;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 判断是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_NO_SUPPORT_BL;
            handler.sendMessage(msg);
            return false;
        }

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            ILog.e("蓝牙未打开");
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_OPEN_BL;
            handler.sendMessage(msg);
            return false;
        }
        return true;
    }

    //  开始扫描蓝牙设备
    @Override
    public void startScan() {
        ILog.e("---------开始扫描-----------");
        mBluetoothAdapter.startLeScan(ROBOT_UUID, mLeScanCallback);
    }

    //  停止扫描蓝牙设备
    @Override
    public void stopScan() {
        ILog.e("---------停止扫描-----------");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    // Device scan callback.
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        // 开始扫描和停止扫描都会调用此方法
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Message msg = Message.obtain();

            String str = "rssi:" + rssi + "|未配对|" + device.getName() + "|" + device.getAddress() + "---" + device.getType();
            ILog.e(str);

            //  如果扫描到的设备名字和给定的机器猫名一致，将设备返回
            if (GlobalContants.ROBOT_BT_NAME.equalsIgnoreCase(device.getName())) {
                BleManager.device = device;
                //蓝牙类型
                int type = device.getType();
                ILog.e(device.getName() + "---" + GlobalContants.ROBOT_BT_NAME + "----" + type);
                if (type == BluetoothDevice.DEVICE_TYPE_LE) {
                    //  扫描到所需设备，停止扫描
                    stopScan();
                    msg.what = MSG_WHAT_FOUND_DEVICE;
                    msg.obj = device;
                } else {
                    msg.what = MSG_WHAT_NO_FOUND_DEVICE;
                }
                handler.sendMessage(msg);
            }
        }
    };

    //  连接设备
    @Override
    public void connect(String ip) {
        if (device == null) {
            return;
        }
        if (mBluetoothAdapter != null) {
            //  获取BluetoothGatt，连接设备，设为了自动连接，可能会存在问题
            mBluetoothGatt = device.connectGatt(context, false, callback);
        } else {
            init(handler, context);
        }
    }

    //  连接设备的回调接口
    final BluetoothGattCallback callback = new BluetoothGattCallback() {

        // 若连接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //  status 表示相应的连接或断开操作是否完成，而不是指连接状态
            Message msg = Message.obtain();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnect = true;
                ILog.e("Connected to GATT server.");
                bleRead.clearData();
                // Attempts to discover services after successful connection.
                boolean discover = mBluetoothGatt.discoverServices();
                ILog.e("Attempting to service discovery:" + discover);
                bleSender.init(mBluetoothGatt);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnect = false;
                ILog.e("Disconnected from GATT server.status:" + status);
                msg.what = MSG_DIS_CONNET;
                bleSender.stopSend();
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Message msg = Message.obtain();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ILog.e("扫描到服务");
                //  获取我们需要的服务
                BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID);
                //  我需要得到的应该是一个特定的BluetoothGattCharacteristic,根据uuid获取
                BluetoothGattCharacteristic mCharacteristic = service.getCharacteristic(CHARAC_READ_UUID);
                //  给设备设置notifity功能
                // 如果设备主动给手机发信息，则可以通过notification的方式，这种方式不用手机去轮询地读设备上的数据
                mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
                BluetoothGattDescriptor descripter = mCharacteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                if (descripter != null) {
                    descripter.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descripter);
                }
                msg.what = MSG_HAS_SERVICE;
                msg.obj = true;
            } else {
                ILog.e("onServicesDiscovered received: " + status);
                msg.obj = false;
            }
            handler.sendMessage(msg);
        }

        //  接收到信息
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            ILog.e("收到信息");
            readInfo(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ILog.e("回调：信息写入成功");
//                bleSender.sendData(characteristic);
            } else {
                ILog.e("回调：信息写入失败");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }
    };

    // 读取设备信息
    private void readInfo(BluetoothGattCharacteristic characteristic) {
        String result = bleRead.read(characteristic);
        if (!TextUtils.isEmpty(result)) {
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_GET_INFO;
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }

    /**
     * 向蓝牙设备写信息
     * @param data  写入的信息
     * @param type  发送的类型，1：设置wifi；2：使用BLE发送控制命令；3：使用socket发送控制命令
     */
    @Override
    public void writeInfo(String data, int type) {
        UUID characWriteUuid = null;
        if (type == 1) {
            characWriteUuid = wifiWriteUUID;
        } else if (type == 2) {
            characWriteUuid = controlWriteUuid;
        }
        //  获取我们需要的服务
        BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID);
        //  我需要得到的应该是一个特定的BluetoothGattCharacteristic,根据uuid获取
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characWriteUuid);
        bleSender.addData(characteristic, data);
    }

    //  关闭通信 BluetoothGatt
    public void close() {
        bleSender.stopSend();
        if (mBluetoothGatt != null) {
            ILog.e("断开蓝牙连接");
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }
}
